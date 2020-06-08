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
            Combinators.combine( anyId(), anyName() ).as( Node.Class::new ),
            Combinators.combine( anyId(), anyName() ).as( Node.DataProperty::new ),
            Combinators.combine( anyId(), anyName() ).as( Node.ObjectProperty::new ),
            Combinators.combine( anyId(), anyName() ).as( Node.AnnotationProperty::new ),
            Combinators.combine( anyId(), anyName() ).as( Node.Individual::new ),
            Combinators.combine( anyId(), anyName() ).as( Node.Datatype::new ),
            Combinators.combine( anyId(), anyName() ).as( Node.Literal::new )
        );
    }

    @Provide
    Arbitrary<Node> anyCardinalityNode() {
        return Arbitraries.oneOf(
            Combinators.combine( anyId(), Arbitraries.integers() ).as( Node.DataExactCardinality::new ),
            Combinators.combine( anyId(), Arbitraries.integers() ).as( Node.DataMaximalCardinality::new ),
            Combinators.combine( anyId(), Arbitraries.integers() ).as( Node.DataMinimalCardinality::new ),
            Combinators.combine( anyId(), Arbitraries.integers() ).as( Node.ObjectExactCardinality::new ),
            Combinators.combine( anyId(), Arbitraries.integers() ).as( Node.ObjectMaximalCardinality::new ),
            Combinators.combine( anyId(), Arbitraries.integers() ).as( Node.ObjectMinimalCardinality::new ),
            Combinators.combine( anyId(), Arbitraries.integers() ).as( Node.ObjectQualifiedExactCardinality::new ),
            Combinators.combine( anyId(), Arbitraries.integers() ).as( Node.ObjectQualifiedMaximalCardinality::new ),
            Combinators.combine( anyId(), Arbitraries.integers() ).as( Node.ObjectQualifiedMinimalCardinality::new ) );
    }

    Arbitrary<Node> anyOnlyIdentifiedNode() {
        return Arbitraries.oneOf(
            anyId().map( Node.ExistentialRestriction::new ),
            anyId().map( Node.ValueRestriction::new ),
            anyId().map( Node.UniversalRestriction::new ),
            anyId().map( Node.Intersection::new ),
            anyId().map( Node.Union::new ),
            anyId().map( Node.Disjointness::new ),
            anyId().map( Node.DisjointUnion::new ),
            anyId().map( Node.Equality::new ),
            anyId().map( Node.Inverse::new ),
            anyId().map( Node.Inequality::new ),
            anyId().map( Node.ClosedClass::new ),
            anyId().map( Node.Complement::new ),
            anyId().map( Node.Self::new ),
            anyId().map( Node.Invisible::new ) );
    }

    @Provide
    Arbitrary<Node> anyPropertyChain() {
        final Arbitrary<String> anyValue = anyName().stream().reduce( "",
            ( s1, s2 ) -> s1 + " " + Node.PropertyChain.OPERATOR_SYMBOL + " " + s2 );
        return Combinators.combine( anyId(), anyValue ).as( Node.PropertyChain::new );
    }

    @Provide
    Arbitrary<Node> anyPropertyMarker() {
        return Combinators.combine( anyId(), Arbitraries.of( Node.PropertyMarker.Kind.class ).set() )
            .as( Node.PropertyMarker::new );
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
