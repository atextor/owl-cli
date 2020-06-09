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

package de.atextor.owlcli.diagram.diagram;

import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.node.AnnotationProperty;
import de.atextor.owlcli.diagram.graph.node.Class;
import de.atextor.owlcli.diagram.graph.node.ClosedClass;
import de.atextor.owlcli.diagram.graph.node.Complement;
import de.atextor.owlcli.diagram.graph.node.DataExactCardinality;
import de.atextor.owlcli.diagram.graph.node.DataMaximalCardinality;
import de.atextor.owlcli.diagram.graph.node.DataMinimalCardinality;
import de.atextor.owlcli.diagram.graph.node.DataProperty;
import de.atextor.owlcli.diagram.graph.node.Datatype;
import de.atextor.owlcli.diagram.graph.node.DisjointUnion;
import de.atextor.owlcli.diagram.graph.node.Disjointness;
import de.atextor.owlcli.diagram.graph.node.Equality;
import de.atextor.owlcli.diagram.graph.node.ExistentialRestriction;
import de.atextor.owlcli.diagram.graph.node.Individual;
import de.atextor.owlcli.diagram.graph.node.Inequality;
import de.atextor.owlcli.diagram.graph.node.Intersection;
import de.atextor.owlcli.diagram.graph.node.Inverse;
import de.atextor.owlcli.diagram.graph.node.Invisible;
import de.atextor.owlcli.diagram.graph.node.Literal;
import de.atextor.owlcli.diagram.graph.node.ObjectExactCardinality;
import de.atextor.owlcli.diagram.graph.node.ObjectMaximalCardinality;
import de.atextor.owlcli.diagram.graph.node.ObjectMinimalCardinality;
import de.atextor.owlcli.diagram.graph.node.ObjectProperty;
import de.atextor.owlcli.diagram.graph.node.ObjectQualifiedExactCardinality;
import de.atextor.owlcli.diagram.graph.node.ObjectQualifiedMaximalCardinality;
import de.atextor.owlcli.diagram.graph.node.ObjectQualifiedMinimalCardinality;
import de.atextor.owlcli.diagram.graph.node.PropertyChain;
import de.atextor.owlcli.diagram.graph.node.PropertyMarker;
import de.atextor.owlcli.diagram.graph.node.Self;
import de.atextor.owlcli.diagram.graph.node.Union;
import de.atextor.owlcli.diagram.graph.node.UniversalRestriction;
import de.atextor.owlcli.diagram.graph.node.ValueRestriction;
import de.atextor.owlcli.diagram.mappers.DefaultIdentifierMapper;
import de.atextor.owlcli.diagram.mappers.DefaultMappingConfiguration;
import de.atextor.owlcli.diagram.mappers.IdentifierMapper;
import de.atextor.owlcli.diagram.mappers.MappingConfiguration;
import io.vavr.control.Try;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import org.semanticweb.owlapi.model.IRI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

public class DiagramGeneratorTest {
    File workingDir = new File( System.getProperty( "user.dir" ) );
    final Configuration configuration = Configuration.builder().build();
    final MappingConfiguration mappingConfiguration = DefaultMappingConfiguration.builder().build();
    final DiagramGenerator diagramGenerator = new DiagramGenerator( configuration, mappingConfiguration );
    final GraphvizGenerator graphvizGenerator = new GraphvizGenerator( configuration );
    final IdentifierMapper identifierMapper = new DefaultIdentifierMapper();

    @Provide
    Arbitrary<IRI> anyIRI() {
        return Arbitraries.of( IRI.create( "http://test.de#foo" ) );
    }

    @Provide
    Arbitrary<Node.Id> anyId() {
        return Arbitraries.oneOf(
            Arbitraries.of( identifierMapper.getSyntheticId() ),
            anyIRI().map( identifierMapper::getSyntheticIdForIri ),
            anyIRI().map( identifierMapper::getIdForIri ) );
    }

    @Provide
    Arbitrary<String> anyName() {
        return Arbitraries.strings().ofMinLength( 1 ).ofMaxLength( 5 ).alpha().numeric();
    }

    @Provide
    Arbitrary<Node> anyNamedNode() {
        return Arbitraries.oneOf(
            Combinators.combine( anyId(), anyName() ).as( Class::new ),
            Combinators.combine( anyId(), anyName() ).as( DataProperty::new ),
            Combinators.combine( anyId(), anyName() ).as( ObjectProperty::new ),
            Combinators.combine( anyId(), anyName() ).as( AnnotationProperty::new ),
            Combinators.combine( anyId(), anyName() ).as( Individual::new ),
            Combinators.combine( anyId(), anyName() ).as( Datatype::new ),
            Combinators.combine( anyId(), anyName() ).as( Literal::new )
        );
    }

    @Provide
    Arbitrary<Node> anyCardinalityNode() {
        return Arbitraries.oneOf(
            Combinators.combine( anyId(), Arbitraries.integers() ).as( DataExactCardinality::new ),
            Combinators.combine( anyId(), Arbitraries.integers() ).as( DataMaximalCardinality::new ),
            Combinators.combine( anyId(), Arbitraries.integers() ).as( DataMinimalCardinality::new ),
            Combinators.combine( anyId(), Arbitraries.integers() ).as( ObjectExactCardinality::new ),
            Combinators.combine( anyId(), Arbitraries.integers() ).as( ObjectMaximalCardinality::new ),
            Combinators.combine( anyId(), Arbitraries.integers() ).as( ObjectMinimalCardinality::new ),
            Combinators.combine( anyId(), Arbitraries.integers() ).as( ObjectQualifiedExactCardinality::new ),
            Combinators.combine( anyId(), Arbitraries.integers() ).as( ObjectQualifiedMaximalCardinality::new ),
            Combinators.combine( anyId(), Arbitraries.integers() ).as( ObjectQualifiedMinimalCardinality::new ) );
    }

    Arbitrary<Node> anyOnlyIdentifiedNode() {
        return Arbitraries.oneOf(
            anyId().map( ExistentialRestriction::new ),
            anyId().map( ValueRestriction::new ),
            anyId().map( UniversalRestriction::new ),
            anyId().map( Intersection::new ),
            anyId().map( Union::new ),
            anyId().map( Disjointness::new ),
            anyId().map( DisjointUnion::new ),
            anyId().map( Equality::new ),
            anyId().map( Inverse::new ),
            anyId().map( Inequality::new ),
            anyId().map( ClosedClass::new ),
            anyId().map( Complement::new ),
            anyId().map( Self::new ),
            anyId().map( Invisible::new ) );
    }

    @Provide
    Arbitrary<Node> anyPropertyChain() {
        final Arbitrary<String> anyValue = anyName().stream().reduce( "",
            ( s1, s2 ) -> s1 + " " + PropertyChain.OPERATOR_SYMBOL + " " + s2 );
        return Combinators.combine( anyId(), anyValue ).as( PropertyChain::new );
    }

    @Provide
    Arbitrary<Node> anyPropertyMarker() {
        return Combinators.combine( anyId(), Arbitraries.of( PropertyMarker.Kind.class ).set() )
            .as( PropertyMarker::new );
    }

    @Provide
    Arbitrary<Node> anyNode() {
        return Arbitraries.oneOf(
            anyNamedNode(),
            anyCardinalityNode(),
            anyOnlyIdentifiedNode(),
            anyPropertyChain(),
            anyPropertyMarker() );
    }

    @Provide
    Arbitrary<Edge> anyPlainEdge() {
        final Arbitrary<Edge.Type> anyType = Arbitraries.of( Edge.Type.class );
        return Combinators.combine( anyType, anyNode(), anyNode() )
            .as( ( type, fromNode, toNode ) -> new Edge.Plain( type, fromNode.getId(), toNode.getId() ) );
    }

    @Provide
    Arbitrary<Edge.Decorated.Label> anyEdgeLabel() {
        return Arbitraries.of( Edge.Decorated.Label.class );
    }

    @Provide
    Arbitrary<Edge> anyDecoratedEdge() {
        final Arbitrary<Edge.Type> anyType = Arbitraries.of( Edge.Type.class );
        return Combinators.combine( anyType, anyNode(), anyNode(), anyEdgeLabel() )
            .as( ( type, fromNode, toNode, label ) ->
                new Edge.Decorated( type, fromNode.getId(), toNode.getId(), label ) );
    }

    @Provide
    Arbitrary<Edge> anyEdge() {
        return Arbitraries.oneOf( anyPlainEdge(), anyDecoratedEdge() );
    }

    @Provide
    Arbitrary<Set<GraphElement>> anyGraph() {
        // Create singleton sets of elements to reduce size of value space
        return Arbitraries.oneOf( anyNode(), anyEdge() ).map( Set::of );
    }

    @Property
    public boolean everyGeneratedDiagramIsSyntacticallyValid( @ForAll( "anyGraph" ) final Set<GraphElement> graph ) {
        final String graphvizDocument = graphvizGenerator.apply( graph.stream() ).apply( configuration );
        final ThrowingConsumer<OutputStream, IOException> contentProvider = outputStream -> {
            outputStream.write( graphvizDocument.getBytes() );
            outputStream.flush();
            outputStream.close();
        };
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final Try<Void> executionResult = diagramGenerator
            .executeDot( contentProvider, output, workingDir, configuration );
        return executionResult.isSuccess();
    }
}
