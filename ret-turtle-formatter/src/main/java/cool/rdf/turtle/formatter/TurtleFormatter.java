/*
 * Copyright 2024 Andreas Textor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cool.rdf.turtle.formatter;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import org.apache.jena.atlas.io.AWriter;
import org.apache.jena.atlas.lib.Pair;
import org.apache.jena.irix.IRIException;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.out.NodeFormatterTTL;
import org.apache.jena.riot.system.PrefixLib;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.system.PrefixMapAdapter;
import org.apache.jena.riot.system.PrefixMapBase;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TurtleFormatter implements Function<Model, String>, BiConsumer<Model, OutputStream> {

    public static final String OUTPUT_ERROR_MESSAGE = "Could not write to stream";

    public static final String DEFAULT_EMPTY_BASE = "urn:ret:empty";

    private static final Logger LOG = LoggerFactory.getLogger( TurtleFormatter.class );

    /**
     * String escape sequences as described in <a href="https://www.w3.org/TR/turtle/#sec-escapes">Escape Sequences</a>.
     * <p>
     * ' (single quote) is not in the pattern, because we never write single quoted strings and therefore don't
     * need to escape single quotes.
     * </p>
     */
    private static final Pattern STRING_ESCAPE_SEQUENCES = Pattern.compile( "[\t\b\n\r\f\"\\\\]" );

    private final FormattingStyle style;

    private final String beforeDot;

    private final String endOfLine;

    private final java.nio.charset.Charset encoding;

    private final Comparator<Map.Entry<String, String>> prefixOrder;

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

        encoding = switch ( style.charset ) {
            case UTF_8, UTF_8_BOM -> StandardCharsets.UTF_8;
            case LATIN1 -> StandardCharsets.ISO_8859_1;
            case UTF_16_BE -> StandardCharsets.UTF_16BE;
            case UTF_16_LE -> StandardCharsets.UTF_16LE;
        };

        prefixOrder = Comparator.<Map.Entry<String, String>>comparingInt( entry ->
            style.prefixOrder.contains( entry.getKey() ) ?
                style.prefixOrder.indexOf( entry.getKey() ) :
                Integer.MAX_VALUE
        ).thenComparing( Map.Entry::getKey );

        objectOrder = Comparator.<RDFNode>comparingInt( object ->
            style.objectOrder.contains( object ) ?
                style.objectOrder.indexOf( object ) :
                Integer.MAX_VALUE
        ).thenComparing( RDFNode::toString );
    }

    private static List<Statement> statements( final Model model ) {
        return model.listStatements().toList();
    }

    private static List<Statement> statements( final Model model, final Property predicate, final RDFNode object ) {
        return model.listStatements( null, predicate, object ).toList();
    }

    @Override
    public String apply( final Model model ) {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        accept( model, outputStream );
        return outputStream.toString();
    }

    private void writeByteOrderMark( final OutputStream outputStream ) {
        try {
            outputStream.write( new byte[]{ (byte) 0xEF, (byte) 0xBB, (byte) 0xBF } );
        } catch ( final IOException exception ) {
            LOG.error( OUTPUT_ERROR_MESSAGE, exception );
        }
    }

    @Override
    public void accept( final Model model, final OutputStream outputStream ) {
        if ( style.charset == FormattingStyle.Charset.UTF_8_BOM ) {
            writeByteOrderMark( outputStream );
        }

        final PrefixMapping prefixMapping = buildPrefixMapping( model );

        final Comparator<Property> predicateOrder = Comparator.<Property>comparingInt( property ->
            style.predicateOrder.contains( property ) ?
                style.predicateOrder.indexOf( property ) :
                Integer.MAX_VALUE
        ).thenComparing( property -> prefixMapping.shortForm( property.getURI() ) );

        final State initialState = buildInitialState( model, outputStream, prefixMapping, predicateOrder );

        final State prefixesWritten = writePrefixes( initialState );

        final Comparator<Statement> subjectComparator =
            Comparator.comparing( statement -> statement.getSubject().isURIResource() ?
                prefixMapping.shortForm( statement.getSubject().getURI() ) : statement.getSubject().toString() );

        final List<Statement> statements = determineStatements( model, subjectComparator );
        final State namedResourcesWritten = writeNamedResources( prefixesWritten, statements );
        final State allResourcesWritten = writeAnonymousResources( namedResourcesWritten );
        final State finalState = style.insertFinalNewline ? allResourcesWritten.newLine() : allResourcesWritten;

        LOG.debug( "Written {} resources, with {} named anonymous resources", finalState.visitedResources.size(),
            finalState.identifiedAnonymousResources.size() );
    }

    private State writeAnonymousResources( final State state ) {
        State currentState = state;
        for ( final Resource resource : state.identifiedAnonymousResources.keySet() ) {
            if ( !resource.listProperties().hasNext() ) {
                continue;
            }
            currentState = writeSubject( resource, currentState.withIndentationLevel( 0 ) );
        }
        return currentState;
    }

    private State writeNamedResources( final State state, final List<Statement> statements ) {
        State currentState = state;
        for ( final Statement statement : statements ) {
            final Resource resource = statement.getSubject();
            if ( !resource.listProperties().hasNext() || currentState.visitedResources.contains( resource ) ) {
                continue;
            }
            if ( resource.isURIResource() ) {
                currentState = writeSubject( resource, currentState.withIndentationLevel( 0 ) );
                continue;
            }
            final State resourceWritten = writeAnonymousResource( resource, currentState.withIndentationLevel( 0 ) );
            final boolean omitSpaceBeforeDelimiter = !currentState.identifiedAnonymousResources.containsKey( resource );
            currentState = writeDot( resourceWritten, omitSpaceBeforeDelimiter ).newLine();
        }
        return currentState;
    }

    private List<Statement> determineStatements( final Model model, final Comparator<Statement> subjectComparator ) {
        final Stream<Statement> wellKnownSubjects = style.subjectOrder.stream().flatMap( subjectType ->
            statements( model, RDF.type, subjectType ).stream().sorted( subjectComparator ) );

        final Stream<Statement> otherSubjects = statements( model ).stream()
            .filter( statement -> !( statement.getPredicate().equals( RDF.type )
                && statement.getObject().isResource()
                && style.subjectOrder.contains( statement.getObject().asResource() ) ) )
            .sorted( subjectComparator );

        return Stream.concat( wellKnownSubjects, otherSubjects )
            .filter( statement -> !( statement.getSubject().isAnon()
                && model.contains( null, null, statement.getSubject() ) ) )
            .toList();
    }

    private State buildInitialState( final Model model, final OutputStream outputStream,
        final PrefixMapping prefixMapping, final Comparator<Property> predicateOrder ) {

        State currentState = new State( outputStream, model, predicateOrder, prefixMapping );
        int i = 0;
        for ( final Resource r : anonymousResourcesThatNeedAnId( model ) ) {
            final String s = style.anonymousNodeIdGenerator.apply( r, i );
            currentState = currentState.withIdentifiedAnonymousResource( r, s );
            i++;
        }
        return currentState;
    }

    /**
     * Anonymous resources that are referred to more than once need to be given an internal id and
     * can not be serialized using [ ] notation.
     *
     * @param model the input model
     * @return the set of anonymous resources that are referred to more than once
     */
    private Set<Resource> anonymousResourcesThatNeedAnId( final Model model ) {
        return model.listObjects().toList().stream()
            .filter( RDFNode::isResource )
            .map( RDFNode::asResource )
            .filter( RDFNode::isAnon )
            .filter( object -> statements( model, null, object ).size() > 1 )
            .collect( Collectors.toSet() );
    }

    private PrefixMapping buildPrefixMapping( final Model model ) {
        final Map<String, String> prefixMap = style.knownPrefixes.stream()
            .filter( knownPrefix -> model.getNsPrefixURI( knownPrefix.prefix() ) == null )
            .collect( Collectors.toMap( FormattingStyle.KnownPrefix::prefix,
                knownPrefix -> knownPrefix.iri().toString() ) );
        return PrefixMapping.Factory.create().setNsPrefixes( model.getNsPrefixMap() )
            .setNsPrefixes( prefixMap );
    }

    private State writePrefixes( final State state ) {
        final Map<String, String> prefixes = state.prefixMapping.getNsPrefixMap();
        final int maxPrefixLength =
            prefixes.keySet().stream().map( String::length ).max( Integer::compareTo ).orElse( 0 );
        final String prefixFormat = switch ( style.alignPrefixes ) {
            case OFF -> "@prefix %s: <%s>" + beforeDot + "." + endOfLine;
            case LEFT -> "@prefix %-" + maxPrefixLength + "s: <%s>" + beforeDot + "." + endOfLine;
            case RIGHT -> "@prefix %" + maxPrefixLength + "s: <%s>" + beforeDot + "." + endOfLine;
        };

        final List<String> urisInModel = allUsedUris( state.model );

        final List<Map.Entry<String, String>> entries = prefixes.entrySet().stream().sorted( prefixOrder )
            .filter( entry -> style.keepUnusedPrefixes ||
                urisInModel.stream().anyMatch( resource -> resource.startsWith( entry.getValue() ) ) )
            .toList();
        State currentState = state;
        for ( final Map.Entry<String, String> entry : entries ) {
            currentState = currentState.write( String.format( prefixFormat, entry.getKey(), entry.getValue() ) );
        }
        currentState = currentState.newLine();
        return currentState;
    }

    private List<String> allUsedUris( final Model model ) {
        return model.listStatements().toList().stream()
            .flatMap( statement -> Stream.of( statement.getSubject(), statement.getPredicate(),
                statement.getObject() ) )
            .<Optional<String>>map( rdfNode -> {
                if ( rdfNode.isURIResource() ) {
                    return Optional.of( rdfNode.asResource().getURI() );
                }
                if ( rdfNode.isLiteral() ) {
                    return Optional.of( rdfNode.asLiteral().getDatatypeURI() );
                }
                return Optional.empty();
            } )
            .filter( Optional::isPresent )
            .map( Optional::get )
            .toList();
    }

    private String indent( final int level ) {
        final String singleIndent = switch ( style.indentStyle ) {
            case SPACE -> " ".repeat( style.indentSize );
            case TAB -> "\t";
        };
        return singleIndent.repeat( Math.max( level, 0 ) );
    }

    private String continuationIndent( final int level ) {
        final String continuation = switch ( style.indentStyle ) {
            case SPACE -> " ".repeat( style.continuationIndentSize );
            case TAB -> "\t".repeat( 2 );
        };
        return indent( level - 1 ) + continuation;
    }

    private State writeDelimiter( final String delimiter, final FormattingStyle.GapStyle before,
        final FormattingStyle.GapStyle after, final String indentation,
        final State state ) {
        final State beforeState = switch ( before ) {
            case SPACE -> state.lastCharacter.equals( " " ) ? state : state.write( " " );
            case NOTHING -> state;
            case NEWLINE -> state.newLine().write( indentation );
        };

        return switch ( after ) {
            case SPACE -> beforeState.write( delimiter + " " );
            case NOTHING -> beforeState.write( delimiter );
            case NEWLINE -> beforeState.write( delimiter ).newLine().write( indentation );
        };
    }

    private State writeComma( final State state ) {
        return writeDelimiter( ",", style.beforeComma, style.afterComma,
            continuationIndent( state.indentationLevel ), state );
    }

    private State writeSemicolon( final State state, final boolean omitLineBreak,
        final boolean omitSpaceBeforeSemicolon, final String nextLineIndentation ) {
        final FormattingStyle.GapStyle beforeSemicolon = omitSpaceBeforeSemicolon ? FormattingStyle.GapStyle.NOTHING :
            style.beforeSemicolon;
        final FormattingStyle.GapStyle afterSemicolon = omitLineBreak ? FormattingStyle.GapStyle.NOTHING :
            style.afterSemicolon;
        return writeDelimiter( ";", beforeSemicolon, afterSemicolon, nextLineIndentation, state );
    }

    private State writeDot( final State state, final boolean omitSpaceBeforeDot ) {
        final FormattingStyle.GapStyle beforeDot = omitSpaceBeforeDot ? FormattingStyle.GapStyle.NOTHING :
            style.beforeDot;
        return writeDelimiter( ".", beforeDot, style.afterDot, "", state );
    }

    private State writeOpeningSquareBracket( final State state ) {
        final FormattingStyle.GapStyle beforeBracket = state.indentationLevel > 0 ? style.beforeOpeningSquareBracket :
            FormattingStyle.GapStyle.NOTHING;
        return writeDelimiter( "[", beforeBracket, style.afterOpeningSquareBracket,
            indent( state.indentationLevel ), state );
    }

    private State writeClosingSquareBracket( final State state ) {
        return writeDelimiter( "]", style.beforeClosingSquareBracket, style.afterClosingSquareBracket,
            indent( state.indentationLevel ), state );
    }

    private boolean isList( final RDFNode node, final State state ) {
        return node.equals( RDF.nil ) ||
            ( node.isResource() && state.model.contains( node.asResource(), RDF.rest, (RDFNode) null ) );
    }

    private State writeResource( final Resource resource, final State state ) {
        if ( isList( resource, state ) ) {
            return writeList( resource, state );
        }
        if ( resource.isURIResource() ) {
            return writeUriResource( resource, state );
        }
        return writeAnonymousResource( resource, state );
    }

    private State writeList( final Resource resource, final State state ) {
        final FormattingStyle.GapStyle afterOpeningParenthesis =
            style.wrapListItems == FormattingStyle.WrappingStyle.ALWAYS ? FormattingStyle.GapStyle.NOTHING :
                style.afterOpeningParenthesis;
        final State opened = writeDelimiter( "(", style.beforeOpeningParenthesis, afterOpeningParenthesis,
            continuationIndent( state.indentationLevel ), state );
        final java.util.List<RDFNode> elementList = resource.as( RDFList.class ).asJavaList();

        int index = 0;
        State currentState = opened;
        for ( final RDFNode element : elementList ) {
            final boolean firstElement = index == 0;
            currentState = writeListElement( element, firstElement, currentState );
            index++;
        }

        final State finalLineBreakWritten = style.wrapListItems == FormattingStyle.WrappingStyle.ALWAYS ?
            currentState.newLine().write( indent( currentState.indentationLevel ) ) : currentState;

        return writeDelimiter( ")", style.beforeClosingParenthesis, style.afterClosingParenthesis,
            continuationIndent( state.indentationLevel ), finalLineBreakWritten );
    }

    private State writeListElement( final RDFNode element, final boolean firstElement, final State state ) {
        return switch ( style.wrapListItems ) {
            case NEVER:
                final State spaceWritten = firstElement ? state : state.write( " " );
                yield writeRdfNode( element, spaceWritten );
            case ALWAYS:
                yield writeRdfNode( element, state.newLine().write( continuationIndent( state.indentationLevel ) ) );
            case FOR_LONG_LINES:
                final int alignmentAfterElementIsWritten = writeRdfNode( element,
                    state.withOutputStream( OutputStream.nullOutputStream() ) ).alignment;
                final boolean wouldElementExceedLineLength =
                    ( alignmentAfterElementIsWritten + 1 ) > style.maxLineLength;
                yield writeRdfNode( element, wouldElementExceedLineLength ?
                    state.newLine().write( continuationIndent( state.indentationLevel ) ) :
                    ( firstElement || state.getLastCharacter().equals( " " ) ? state : state.write( " " ) ) );
        };
    }

    private State writeAnonymousResource( final Resource resource, final State state ) {
        if ( state.identifiedAnonymousResources.containsKey( resource ) ) {
            return state.write( state.identifiedAnonymousResources.getOrDefault( resource, "" ) );
        }

        if ( !state.model.contains( resource, null, (RDFNode) null ) ) {
            return state.write( " []" );

        }
        if ( state.visitedResources.contains( resource ) ) {
            return state;
        }
        final State afterOpeningSquareBracket = writeOpeningSquareBracket( state );
        final State afterContent = writeSubject( resource, afterOpeningSquareBracket );
        return writeClosingSquareBracket( afterContent ).withVisitedResource( resource );
    }

    private String uriResource( final Resource resource, final State state ) {
        final String uri = resource.getURI();
        // Workaround to force writing out URIs without a base that is "automatically determined" by Jena:
        // when calling model.read(inputStream, base, language) and passing an empty String as base, Jena will
        // replace that with something "smart" such as the current directory.
        final String uriWithoutEmptyBase = uri.startsWith( style.emptyRdfBase ) ?
            uri.substring( style.emptyRdfBase.length() ) : uri;
        final String shortForm = state.prefixMapping.shortForm( uriWithoutEmptyBase );
        if ( shortForm.equals( uriWithoutEmptyBase ) ) {
            return "<" + uriWithoutEmptyBase + ">";
        }
        // All other cases are delegated to Jena RIOT
        final NodeFormatterTTL formatter = new NodeFormatterTTL( "", new CustomPrefixMap( state.prefixMapping ) );
        final NodeFormatterSink sink = new NodeFormatterSink();
        try {
            formatter.formatURI( sink, uri );
        } catch ( final IRIException exception ) {
            // The formatter encountered an invalid IRI. This should not have happend in the first place, i.e.,
            // it should not be present in the model. Since this should have been fixed by the parser, we handle
            // it the same was as Jena Core: Still print it out.
            return "<" + uri + ">";
        }
        return sink.buffer.toString();
    }

    /**
     * Unfortunately, the logic in {@link PrefixMapAdapter#abbrev(String)} is broken and won't return a prefix
     * even if one exists in the wrapped map; and the class is final, so we can't overwrite the method.
     */
    static class CustomPrefixMap extends PrefixMapBase implements PrefixMap {
        private final PrefixMapping mapping;

        public CustomPrefixMap( final PrefixMapping mapping ) {
            this.mapping = mapping;
        }

        @Override
        public String get( final String prefix ) {
            return mapping.getNsPrefixURI( prefix );
        }

        @Override
        public Map<String, String> getMapping() {
            return mapping.getNsPrefixMap();
        }

        @Override
        public void add( final String prefix, final String iriString ) {
        }

        @Override
        public void delete( final String prefix ) {
        }

        @Override
        public void clear() {
        }

        @Override
        public boolean containsPrefix( final String prefix ) {
            return mapping.getNsPrefixMap().containsKey( prefix );
        }

        @Override
        public boolean isEmpty() {
            return mapping.getNsPrefixMap().isEmpty();
        }

        @Override
        public int size() {
            return mapping.getNsPrefixMap().size();
        }

        @Override
        public Pair<String, String> abbrev( final String uriStr ) {
            return PrefixLib.abbrev( this, uriStr );
        }
    }

    private State writeUriResource( final Resource resource, final State state ) {
        return state.write( uriResource( resource, state ) );
    }

    private State writeLiteral( final Literal literal, final State state ) {
        if ( literal.getDatatypeURI().equals( XSD.xboolean.getURI() ) ) {
            return state.write( literal.getBoolean() ? "true" : "false" );
        }
        if ( literal.getDatatypeURI().equals( XSD.xstring.getURI() ) ) {
            return state.write( quoteAndEscape( literal ) );
        }
        if ( literal.getDatatypeURI().equals( XSD.decimal.getURI() ) ) {
            return state.write( literal.getLexicalForm() );
        }
        if ( literal.getDatatypeURI().equals( XSD.integer.getURI() ) ) {
            return state.write( literal.getValue().toString() );
        }
        if ( literal.getDatatypeURI().equals( XSD.xdouble.getURI() ) ) {
            return state.write( style.doubleFormat.format( literal.getDouble() ) );
        }
        if ( literal.getDatatypeURI().equals( RDF.langString.getURI() ) ) {
            return state.write( quoteAndEscape( literal ) + "@" + literal.getLanguage() );
        }

        final Resource typeResource = ResourceFactory.createResource( literal.getDatatypeURI() );
        final State literalWritten = state.write( quoteAndEscape( literal ) + "^^" );
        return writeUriResource( typeResource, literalWritten );
    }

    private String quoteAndEscape( final RDFNode node ) {
        final String value = node.asNode().getLiteralLexicalForm();
        final String quote = switch ( style.quoteStyle ) {
            case ALWAYS_SINGE_QUOTES -> "\"";
            case ALWAYS_TRIPLE_QUOTES -> "\"\"\"";
            case TRIPLE_QUOTES_FOR_MULTILINE -> value.contains( "\n" ) ? "\"\"\"" : "\"";
        };

        final Map<String, String> characterReplacements = Map.of(
            "\t", "\\\\t",
            "\b", "\\\\b",
            "\r", "\\\\r",
            "\f", "\\\\f",
            "\n", quote.equals( "\"" ) ? "\\\\n" : "\n", // Don't escape line breaks in triple-quoted strings
            "\"", quote.equals( "\"" ) ? "\\\\\"" : "\"", // Don't escape quotes in triple-quoted strings
            "\\", "\\\\\\\\"
        );

        final String escapedValue = STRING_ESCAPE_SEQUENCES.matcher( value ).replaceAll( match ->
            characterReplacements.getOrDefault( match.group(), match.group() ) );

        // Special case: If the last character in the triple-quoted string is a quote, it must be escaped
        // See https://github.com/atextor/turtle-formatter/issues/9
        final String result = quote.equals( "\"\"\"" ) && escapedValue.endsWith( "\"" )
            ? escapedValue.substring( 0, escapedValue.length() - 1 ) + "\\\""
            : escapedValue;
        return quote + result + quote;
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
        if ( property.getURI().equals( RDF.type.getURI() ) && style.useAForRdfType ) {
            return state.write( "a" );
        }
        return writeUriResource( property, state );
    }

    private State writeSubject( final Resource resource, final State state ) {
        if ( state.visitedResources.contains( resource ) ) {
            return state;
        }

        // indent
        final boolean isIdentifiedAnon = state.identifiedAnonymousResources.containsKey( resource );
        final boolean subjectsNeedsIdentation = !resource.isAnon() || isIdentifiedAnon;
        final State indentedSubject = subjectsNeedsIdentation ? state.write( indent( state.indentationLevel ) ) : state;
        // subject
        final State stateWithSubject = resource.isURIResource() || isIdentifiedAnon ?
            writeResource( resource, indentedSubject ).withVisitedResource( resource ) :
            indentedSubject.withVisitedResource( resource );

        final State gapAfterSubject = style.firstPredicateInNewLine || ( resource.isAnon() && !isIdentifiedAnon ) ?
            stateWithSubject : stateWithSubject.write( " " );

        final int predicateAlignment = style.firstPredicateInNewLine ? style.indentSize : gapAfterSubject.alignment;

        // predicates and objects
        final Set<Property> properties = resource.listProperties().mapWith( Statement::getPredicate ).toSet();

        final int maxPropertyWidth = properties.stream().map( property ->
            uriResource( property, state ) ).map( String::length ).max( Integer::compareTo ).orElse( 0 );

        int index = 0;
        State currentState = gapAfterSubject.addIndentationLevel();
        for ( final Property property : properties.stream().sorted( state.predicateOrder ).toList() ) {
            final boolean firstProperty = index == 0;
            final boolean lastProperty = index == properties.size() - 1;
            final int propertyWidth = uriResource( property, currentState ).length();
            final String gapAfterPredicate = style.alignObjects ?
                " ".repeat( maxPropertyWidth - propertyWidth + 1 ) : " ";
            currentState = writeProperty( resource, property, firstProperty, lastProperty, predicateAlignment,
                gapAfterPredicate, currentState );
            index++;
        }
        return currentState;
    }

    private State writeProperty( final Resource subject, final Property predicate, final boolean firstProperty,
        final boolean lastProperty, final int alignment,
        final String gapAfterPredicate, final State state ) {
        final Set<RDFNode> objects =
            subject.listProperties( predicate ).mapWith( Statement::getObject ).toSet();

        final boolean useComma = ( style.useCommaByDefault && !style.noCommaForPredicate.contains( predicate ) )
            || ( !style.useCommaByDefault && style.commaForPredicate.contains( predicate ) );

        final State wrappedPredicate = firstProperty && style.firstPredicateInNewLine && !subject.isAnon() ?
            state.newLine() : state;

        final boolean isNamedAnon = state.identifiedAnonymousResources.containsKey( subject );
        final boolean inBrackets = subject.isAnon() && !isNamedAnon;

        final boolean shouldIndentFirstPropertyByLevel = firstProperty &&
            ( ( style.firstPredicateInNewLine && !inBrackets )
                || ( inBrackets && state.indentationLevel <= 1 ) );
        final boolean shouldIndentOtherPropertyByLevel = !firstProperty &&
            ( inBrackets || isNamedAnon );

        final State indentedPredicateByLevel = shouldIndentFirstPropertyByLevel || shouldIndentOtherPropertyByLevel ?
            wrappedPredicate.write( indent( state.indentationLevel ) ) : wrappedPredicate;

        final boolean shouldIndentFirstPropertyOnce = firstProperty &&
            ( inBrackets && state.indentationLevel > 1 );

        final State indentedPredicate = shouldIndentFirstPropertyOnce ?
            indentedPredicateByLevel.write( indent( 1 ) ) : indentedPredicateByLevel;

        final State predicateAlignment = !firstProperty && style.alignPredicates && !subject.isAnon() ?
            indentedPredicate.write( " ".repeat( alignment ) ) : indentedPredicate;

        final State predicateWrittenOnce = useComma ? writeProperty( predicate, predicateAlignment )
            .write( gapAfterPredicate ) : predicateAlignment;

        int index = 0;
        State currentState = predicateWrittenOnce;
        for ( final RDFNode object : objects.stream().sorted( objectOrder ).toList() ) {
            final boolean lastObject = index == objects.size() - 1;
            final State predicateWritten = useComma ? currentState :
                writeProperty( predicate, currentState );

            final boolean isAnonWithBrackets = object.isAnon()
                && !predicateWritten.identifiedAnonymousResources.containsKey( object.asResource() );
            final boolean isList = isList( object, predicateWritten );
            final State spaceWritten = !isAnonWithBrackets && !isList && !useComma ?
                predicateWritten.write( gapAfterPredicate ) :
                predicateWritten;

            final State objectWritten = writeRdfNode( object, spaceWritten );
            if ( useComma && !lastObject ) {
                currentState = writeComma( objectWritten );
                index++;
                continue;
            }

            final boolean listWritten = isList && style.afterClosingParenthesis == FormattingStyle.GapStyle.NOTHING;
            final boolean omitSpaceBeforeDelimiter =
                object.isResource()
                    && object.isAnon()
                    && !listWritten
                    && !currentState.identifiedAnonymousResources.containsKey( object.asResource() );
            if ( lastProperty && lastObject && objectWritten.indentationLevel == 1 && !inBrackets ) {
                currentState = writeDot( objectWritten, omitSpaceBeforeDelimiter ).newLine();
                index++;
                continue;
            }
            final boolean doAlign = style.alignPredicates || subject.isAnon();
            final boolean moreIdenticalPredicatesRemaining =
                subject.listProperties( predicate ).toList().size() > 1 && !lastObject;
            final boolean isAnonOrLastObject =
                ( subject.isAnon() || lastObject ) && !moreIdenticalPredicatesRemaining;
            final String nextLineIndentation = doAlign && isAnonOrLastObject ? ""
                : indent( objectWritten.indentationLevel );
            final State semicolonWritten = writeSemicolon( objectWritten, lastProperty && lastObject,
                omitSpaceBeforeDelimiter, nextLineIndentation );
            currentState = subject.isAnon() && lastProperty && !moreIdenticalPredicatesRemaining ?
                semicolonWritten.removeIndentationLevel() :
                semicolonWritten;
            index++;
        }
        return currentState;
    }

    class NodeFormatterSink implements AWriter {
        StringBuffer buffer = new StringBuffer();

        @Override
        public void write( final char ch ) {
            buffer.append( ch );
        }

        @Override
        public void write( final char[] cbuf ) {
            buffer.append( cbuf );
        }

        @Override
        public void write( final String string ) {
            buffer.append( string );
        }

        @Override
        public void print( final char ch ) {
            write( ch );
        }

        @Override
        public void print( final char[] cbuf ) {
            write( cbuf );
        }

        @Override
        public void print( final String string ) {
            write( string );
        }

        @Override
        public void printf( final String fmt, final Object... arg ) {
            write( String.format( fmt, arg ) );
        }

        @Override
        public void println( final String object ) {
            write( object + endOfLine );
        }

        @Override
        public void println() {
            write( endOfLine );
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }
    }

    @Value
    @With
    @AllArgsConstructor
    private class State {
        OutputStream outputStream;

        Model model;

        Set<Resource> visitedResources;

        Map<Resource, String> identifiedAnonymousResources;

        Comparator<Property> predicateOrder;

        PrefixMapping prefixMapping;

        int indentationLevel;

        int alignment;

        String lastCharacter;


        public State( final OutputStream outputStream, final Model model, final Comparator<Property> predicateOrder,
            final PrefixMapping prefixMapping ) {
            this( outputStream, model, Set.of(), Map.of(), predicateOrder, prefixMapping, 0, 0, "" );
        }

        public State withIdentifiedAnonymousResource( final Resource anonymousResource, final String id ) {
            final Map<Resource, String> newMap = new HashMap<>( identifiedAnonymousResources );
            newMap.put( anonymousResource, id );
            return withIdentifiedAnonymousResources( newMap );
        }

        public State withVisitedResource( final Resource visitedResource ) {
            final Set<Resource> newSet = new HashSet<>( visitedResources );
            newSet.add( visitedResource );
            return withVisitedResources( newSet );
        }

        public State addIndentationLevel() {
            return withIndentationLevel( indentationLevel + 1 );
        }

        public State removeIndentationLevel() {
            return withIndentationLevel( indentationLevel - 1 );
        }

        public State newLine() {
            return write( endOfLine ).withAlignment( 0 );
        }

        public State write( final String content ) {
            final String end = content.length() > 0 ? content.substring( content.length() - 1 ) : "";
            try {
                outputStream.write( content.getBytes( encoding ) );
            } catch ( final IOException e ) {
                LOG.error( OUTPUT_ERROR_MESSAGE, e );
            }
            return withLastCharacter( end ).withAlignment( alignment + content.length() );
        }
    }
}
