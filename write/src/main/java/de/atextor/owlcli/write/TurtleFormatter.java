/*
 * This file is part of OWL-CLI.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright (c) 2020, Andreas Textor.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www
 * .gnu.org/licenses/.
 */

package de.atextor.owlcli.write;

import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.collection.Stream;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.function.Function;

public class TurtleFormatter implements Function<Model, String> {
    private static final Logger LOG = LoggerFactory.getLogger( TurtleFormatter.class );

    private final FormattingStyle style;

    private final String beforeDot;

    private final String endOfLine;

    private final Comparator<Tuple2<String, String>> prefixOrder;

    private final Comparator<RDFNode> objectOrder;

    public TurtleFormatter( final FormattingStyle style ) {
        this.style = style;

        endOfLine = switch ( style.endOfLine ) {
            case CR -> "\r";
            case LF -> "\n";
            case CRLF -> "\r\n";
        };

        beforeDot = switch ( style.beforeDot ) {
            case SPACE -> " ";
            case NOTHING -> "";
            case NEWLINE -> endOfLine;
        };

        prefixOrder = Comparator.<Tuple2<String, String>>comparingInt( entry ->
            style.prefixOrder.contains( entry._1() ) ?
                style.prefixOrder.indexOf( entry._1() ) :
                Integer.MAX_VALUE
        ).thenComparing( Tuple2::_1 );

        objectOrder = Comparator.<RDFNode>comparingInt( object ->
            style.objectOrder.contains( object ) ?
                style.objectOrder.indexOf( object ) :
                Integer.MAX_VALUE
        ).thenComparing( RDFNode::toString );
    }

    private static Stream<Statement> statements( final Model model ) {
        return Stream.ofAll( model::listStatements );
    }

    private static Stream<Statement> statements( final Model model, final Resource subject, final Property predicate,
                                                 final RDFNode object ) {
        return Stream.ofAll( () -> model.listStatements( subject, predicate, object ) );
    }

    @Override
    public String apply( final Model model ) {
        final PrefixMapping prefixMapping = buildPrefixMapping( model );

        final Comparator<Property> predicateOrder = Comparator.<Property>comparingInt( property ->
            style.predicateOrder.contains( property ) ?
                style.predicateOrder.indexOf( property ) :
                Integer.MAX_VALUE
        ).thenComparing( property -> prefixMapping.shortForm( property.getURI() ) );

        final State initialState = Stream
            .ofAll( anonymousResourcesThatNeedAnId( model ) )
            .zipWithIndex()
            .map( entry -> new Tuple2<>( entry._1(), style.anonymousNodeIdGenerator.apply( entry._1(), entry._2() ) ) )
            .foldLeft( new State( model, predicateOrder, prefixMapping ), ( state, entry ) ->
                state.withIdentifiedAnonymousResource( entry._1(), entry._2() ) );

        final State prefixesWritten = writePrefixes( prefixMapping, initialState );

        final Comparator<Statement> subjectComparator =
            Comparator.comparing( statement -> statement.getSubject().isURIResource() ?
                prefixMapping.shortForm( statement.getSubject().getURI() ) : statement.getSubject().toString() );

        final Stream<Statement> wellKnownSubjects = Stream.ofAll( style.subjectOrder ).flatMap( subjectType ->
            statements( model, null, RDF.type, subjectType ).sorted( subjectComparator ) );
        final Stream<Statement> otherSubjects = statements( model )
            .filter( statement -> !statement.getPredicate().equals( RDF.type ) )
            .sorted( subjectComparator );
        final Stream<Statement> statements = wellKnownSubjects.appendAll( otherSubjects );

        final State finalState = statements
            .map( Statement::getSubject )
            .foldLeft( prefixesWritten, ( state, resource ) ->
                writeSubject( resource, state.withIndentationLevel( 0 ) ) );

        LOG.debug( "Written {} resources, with {} named anonymous resources", finalState.visitedResources.size(),
            finalState.identifiedAnonymousResources.size() );

        return finalState.print();
    }

    /**
     * Anonymous resources that are referred to more than once need to be given an internal id and
     * can not be serialized using [ ] notation.
     *
     * @param model the input model
     * @return the set of anonymous resources that are referred to more than once
     */
    private Set<Resource> anonymousResourcesThatNeedAnId( final Model model ) {
        return Stream.ofAll( model::listObjects )
            .filter( RDFNode::isResource )
            .map( RDFNode::asResource )
            .filter( RDFNode::isAnon )
            .filter( object -> statements( model, null, null, object ).toList().size() > 1 )
            .toSet();
    }

    private PrefixMapping buildPrefixMapping( final Model model ) {
        final Map<String, String> prefixMap = Stream.ofAll( style.knownPrefixes )
            .filter( knownPrefix -> model.getNsPrefixURI( knownPrefix.getPrefix() ) == null )
            .toMap( FormattingStyle.KnownPrefix::getPrefix, knownPrefix -> knownPrefix.getIri().toString() );
        return PrefixMapping.Factory.create().setNsPrefixes( model.getNsPrefixMap() )
            .setNsPrefixes( prefixMap.toJavaMap() );
    }

    private State writePrefixes( final PrefixMapping prefixMapping, final State state ) {
        final Map<String, String> prefixes = HashMap.ofAll( prefixMapping.getNsPrefixMap() );
        final int maxPrefixLength = prefixes.keySet().map( String::length ).max().getOrElse( 0 );
        final String prefixFormat = switch ( style.alignPrefixes ) {
            case OFF -> "@prefix %s: <%s>" + beforeDot + ".%n";
            case LEFT -> "@prefix %-" + maxPrefixLength + "s: <%s>" + beforeDot + ".%n";
            case RIGHT -> "@prefix %" + maxPrefixLength + "s: <%s>" + beforeDot + ".%n";
        };

        final State prefixesWritten = prefixes.toStream().sorted( prefixOrder ).foldLeft( state,
            ( newState, entry ) -> newState.write( String.format( prefixFormat, entry._1(), entry._2() ) ) );

        return prefixesWritten.write( endOfLine );
    }

    private String indent( final int level ) {
        final String singleIndent = switch ( style.indentStyle ) {
            case SPACE -> " ".repeat( style.indentSize );
            case TAB -> "\\t";
        };
        return singleIndent.repeat( level );
    }

    private String continuationIndent( final int level ) {
        final String continuation = switch ( style.indentStyle ) {
            case SPACE -> " ".repeat( style.continuationIndentSize );
            case TAB -> "\\t".repeat( 2 );
        };
        return indent( level - 1 ) + continuation;
    }

    private State writeDelimiter( final String delimiter, final FormattingStyle.GapStyle before,
                                  final FormattingStyle.GapStyle after, final String indentation,
                                  final State state ) {
        final State beforeState = switch ( before ) {
            case SPACE -> state.write( " " );
            case NOTHING -> state;
            case NEWLINE -> state.write( endOfLine ).write( indentation );
        };

        return switch ( after ) {
            case SPACE -> beforeState.write( delimiter + " " );
            case NOTHING -> beforeState.write( delimiter );
            case NEWLINE -> beforeState.write( delimiter + endOfLine ).write( indentation );
        };
    }

    private State writeComma( final State state ) {
        return writeDelimiter( ",", style.beforeComma, style.afterComma,
            continuationIndent( state.indentationLevel ), state );
    }

    private State writeSemicolon( final State state ) {
        return writeDelimiter( ";", style.beforeSemicolon, style.afterSemicolon,
            indent( state.indentationLevel ), state );
    }

    private State writeDot( final State state ) {
        return writeDelimiter( ".", style.beforeDot, style.afterDot, "", state );
    }

    private State writeResource( final Resource resource, final State state ) {
        if ( resource.isURIResource() ) {
            if ( resource.equals( RDF.nil ) || state.model.contains( resource, RDF.rest, (RDFNode) null ) ) {
                return writeList( resource, state );
            }

            return writeUriResource( resource, state );
        }
        return writeAnonymousResource( resource, state );
    }

    private State writeList( final Resource resource, final State state ) {
        return state;
    }

    private State writeAnonymousResource( final Resource resource, final State state ) {
        if ( !state.model.contains( resource, null, (RDFNode) null ) ) {
            return state.write( "[]" );

        }

        return state.write( "[...]" );
    }

    private State writeUriResource( final Resource resource, final State state ) {
        if ( resource.getURI().equals( RDF.type.getURI() ) && style.useAForRdfType ) {
            return state.write( "a" );
        }

        return state.write( state.prefixMapping.shortForm( resource.getURI() ) );
    }

    private State writeLiteral( final Literal literal, final State state ) {
        if ( literal.getDatatypeURI().equals( XSD.xboolean.getURI() ) ) {
            return state.write( literal.getBoolean() ? "true" : "false" );
        }
        if ( literal.getDatatypeURI().equals( XSD.xstring.getURI() ) ) {
            return state.write( "\"" + literal.getValue().toString() + "\"" );
        }
        if ( literal.getDatatypeURI().equals( XSD.decimal.getURI() ) ) {
            return state.write( literal.getLexicalForm() );
        }
        if ( literal.getDatatypeURI().equals( XSD.integer.getURI() ) ) {
            return state.write( literal.getValue().toString() );
        }
        if ( literal.getDatatypeURI().equals( XSD.xdouble.getURI() ) ) {
            return state.write( "" + literal.getDouble() );
        }

        final Resource typeResource = ResourceFactory.createResource( literal.getDatatypeURI() );
        final State literalWritten = state.write( "\"" + literal.getLexicalForm() + "\"^^" );
        return writeUriResource( typeResource, literalWritten );
    }

    private State writeRdfNode( final RDFNode node, final State state ) {
        if ( node.isResource() ) {
            return writeResource( node.asResource(), state );
        }

        if ( node.isLiteral() ) {
            return writeLiteral( node.asLiteral(), state );
        }

        return state;
    }

    private State writeProperty( final Property property, final State state ) {
        return writeUriResource( property, state );
    }

    private State writeSubject( final Resource resource, final State state ) {
        if ( state.visitedResources.contains( resource ) ) {
            return state;
        }

        // indent
        final State indentedSubject = state.write( indent( state.indentationLevel ) );
        // subject
        final State stateWithSubject = writeResource( resource, indentedSubject )
            .withVisitedResource( resource )
            .write( " " );

        // predicates and objects
        final Set<Property> properties = Stream.ofAll( resource::listProperties )
            .map( Statement::getPredicate ).toSet();

        return Stream
            .ofAll( properties )
            .sorted( state.predicateOrder )
            .zipWithIndex()
            .foldLeft( stateWithSubject.indentedOnce(), ( currentState, indexedProperty ) -> {
                final Property property = indexedProperty._1();
                final int index = indexedProperty._2();
                final boolean firstProperty = index == 0;
                final boolean lastProperty = index == properties.size() - 1;
                return writeProperty( resource, property, firstProperty, lastProperty, currentState );
            } );
    }

    private State writeProperty( final Resource subject, final Property predicate, final boolean firstProperty,
                                 final boolean lastProperty, final State state ) {
        final Set<RDFNode> objects =
            Stream.ofAll( () -> subject.listProperties( predicate ) ).map( Statement::getObject ).toSet();

        final boolean useComma = ( style.useCommaByDefault && !style.noCommaForPredicate.contains( predicate ) )
            || ( !style.useCommaByDefault && style.commaForPredicate.contains( predicate ) );

        final State wrappedPredicate = firstProperty && style.firstPredicateInNewLIne ?
            state.write( endOfLine ).write( indent( state.indentationLevel ) ) : state;

        final State predicateWrittenOnce = useComma ?
            writeProperty( predicate, wrappedPredicate ).write( " " ) : wrappedPredicate;

        return Stream
            .ofAll( objects )
            .sorted( objectOrder )
            .zipWithIndex()
            .foldLeft( predicateWrittenOnce, ( currentState, indexedObject ) -> {
                final RDFNode object = indexedObject._1();
                final int index = indexedObject._2();
                final boolean lastObject = index == objects.size() - 1;

                final State predicateWritten = useComma ? currentState :
                    writeProperty( predicate, currentState ).write( " " );

                final State objectWritten = writeRdfNode( object, predicateWritten );
                if ( useComma && !lastObject ) {
                    return writeComma( objectWritten );

                }
                if ( lastProperty && lastObject ) {
                    return writeDot( objectWritten ).write( endOfLine );
                }
                return writeSemicolon( objectWritten );
            } );
    }

    @Value
    @With
    @AllArgsConstructor
    private static class State {
        StringBuffer buffer;

        Model model;

        Set<Resource> visitedResources;

        Map<Resource, String> identifiedAnonymousResources;

        Comparator<Property> predicateOrder;

        PrefixMapping prefixMapping;

        int indentationLevel;

        public State( final Model model, final Comparator<Property> predicateOrder,
                      final PrefixMapping prefixMapping ) {
            this( new StringBuffer(), model, HashSet.empty(), HashMap.empty(), predicateOrder, prefixMapping, 0 );
        }

        public State withIdentifiedAnonymousResource( final Resource anonymousResource, final String id ) {
            return withIdentifiedAnonymousResources( identifiedAnonymousResources.put( anonymousResource, id ) );
        }

        public State withVisitedResource( final Resource visitedResource ) {
            return withVisitedResources( visitedResources.add( visitedResource ) );
        }

        public State indentedOnce() {
            return withIndentationLevel( indentationLevel + 1 );
        }

        public State write( final String content ) {
            // Interface pretends to use immutable data structures, while the implementation actually reuses the
            // same StringBuffer
            buffer.append( content );
            return withBuffer( buffer );
        }

        public String print() {
            return buffer.toString();
        }
    }

}
