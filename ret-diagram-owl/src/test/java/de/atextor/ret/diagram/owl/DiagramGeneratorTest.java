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

package de.atextor.ret.diagram.owl;

import de.atextor.ret.diagram.owl.graph.Edge;
import de.atextor.ret.diagram.owl.graph.GraphElement;
import de.atextor.ret.diagram.owl.graph.Node;
import de.atextor.ret.diagram.owl.graph.node.AnnotationProperty;
import de.atextor.ret.diagram.owl.graph.node.Class;
import de.atextor.ret.diagram.owl.graph.node.ClosedClass;
import de.atextor.ret.diagram.owl.graph.node.Complement;
import de.atextor.ret.diagram.owl.graph.node.DataExactCardinality;
import de.atextor.ret.diagram.owl.graph.node.DataMaximalCardinality;
import de.atextor.ret.diagram.owl.graph.node.DataMinimalCardinality;
import de.atextor.ret.diagram.owl.graph.node.DataProperty;
import de.atextor.ret.diagram.owl.graph.node.Datatype;
import de.atextor.ret.diagram.owl.graph.node.DisjointUnion;
import de.atextor.ret.diagram.owl.graph.node.Disjointness;
import de.atextor.ret.diagram.owl.graph.node.Equality;
import de.atextor.ret.diagram.owl.graph.node.ExistentialRestriction;
import de.atextor.ret.diagram.owl.graph.node.Individual;
import de.atextor.ret.diagram.owl.graph.node.Inequality;
import de.atextor.ret.diagram.owl.graph.node.Intersection;
import de.atextor.ret.diagram.owl.graph.node.Inverse;
import de.atextor.ret.diagram.owl.graph.node.Invisible;
import de.atextor.ret.diagram.owl.graph.node.Literal;
import de.atextor.ret.diagram.owl.graph.node.ObjectExactCardinality;
import de.atextor.ret.diagram.owl.graph.node.ObjectMaximalCardinality;
import de.atextor.ret.diagram.owl.graph.node.ObjectMinimalCardinality;
import de.atextor.ret.diagram.owl.graph.node.ObjectProperty;
import de.atextor.ret.diagram.owl.graph.node.ObjectQualifiedExactCardinality;
import de.atextor.ret.diagram.owl.graph.node.ObjectQualifiedMaximalCardinality;
import de.atextor.ret.diagram.owl.graph.node.ObjectQualifiedMinimalCardinality;
import de.atextor.ret.diagram.owl.graph.node.PropertyChain;
import de.atextor.ret.diagram.owl.graph.node.PropertyMarker;
import de.atextor.ret.diagram.owl.graph.node.Self;
import de.atextor.ret.diagram.owl.graph.node.Union;
import de.atextor.ret.diagram.owl.graph.node.UniversalRestriction;
import de.atextor.ret.diagram.owl.graph.node.ValueRestriction;
import de.atextor.ret.diagram.owl.mappers.DefaultIdentifierMapper;
import de.atextor.ret.diagram.owl.mappers.DefaultMappingConfiguration;
import de.atextor.ret.diagram.owl.mappers.IdentifierMapper;
import de.atextor.ret.diagram.owl.mappers.MappingConfiguration;
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
import java.util.List;
import java.util.Set;

public class DiagramGeneratorTest {
    final File workingDir = new File( System.getProperty( "user.dir" ) );

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
        return Arbitraries.oneOf( List.of(
            Arbitraries.of( identifierMapper.getSyntheticId() ),
            anyIRI().map( identifierMapper::getSyntheticIdForIri ),
            anyIRI().map( identifierMapper::getIdForIri )
        ) );
    }

    @Provide
    Arbitrary<String> anyName() {
        return Arbitraries.strings().ofMinLength( 1 ).ofMaxLength( 5 ).alpha().numeric();
    }

    @Provide
    Arbitrary<Node> anyNamedNode() {
        return Arbitraries.oneOf( List.of(
            Combinators.combine( anyId(), anyName() ).as( Class::new ),
            Combinators.combine( anyId(), anyName() ).as( DataProperty::new ),
            Combinators.combine( anyId(), anyName() ).as( ObjectProperty::new ),
            Combinators.combine( anyId(), anyName() ).as( AnnotationProperty::new ),
            Combinators.combine( anyId(), anyName() ).as( Individual::new ),
            Combinators.combine( anyId(), anyName() ).as( Datatype::new ),
            Combinators.combine( anyId(), anyName() ).as( Literal::new )
        ) );
    }

    @Provide
    Arbitrary<Node> anyCardinalityNode() {
        return Arbitraries.oneOf( List.of(
            Combinators.combine( anyId(), Arbitraries.integers() ).as( DataExactCardinality::new ),
            Combinators.combine( anyId(), Arbitraries.integers() ).as( DataMaximalCardinality::new ),
            Combinators.combine( anyId(), Arbitraries.integers() ).as( DataMinimalCardinality::new ),
            Combinators.combine( anyId(), Arbitraries.integers() ).as( ObjectExactCardinality::new ),
            Combinators.combine( anyId(), Arbitraries.integers() ).as( ObjectMaximalCardinality::new ),
            Combinators.combine( anyId(), Arbitraries.integers() ).as( ObjectMinimalCardinality::new ),
            Combinators.combine( anyId(), Arbitraries.integers() ).as( ObjectQualifiedExactCardinality::new ),
            Combinators.combine( anyId(), Arbitraries.integers() ).as( ObjectQualifiedMaximalCardinality::new ),
            Combinators.combine( anyId(), Arbitraries.integers() ).as( ObjectQualifiedMinimalCardinality::new )
        ) );
    }

    Arbitrary<Node> anyOnlyIdentifiedNode() {
        return Arbitraries.oneOf( List.of(
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
            anyId().map( Invisible::new )
        ) );
    }

    @Provide
    Arbitrary<Node> anyPropertyChain() {
        final Arbitrary<String> anyValue = anyName().stream().ofMinSize( 2 ).reduce( "",
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
        return Arbitraries.oneOf( List.of(
            anyNamedNode(),
            anyCardinalityNode(),
            anyOnlyIdentifiedNode(),
            anyPropertyChain(),
            anyPropertyMarker()
        ) );
    }

    @Provide
    Arbitrary<Edge> anyPlainEdge() {
        final Arbitrary<Edge.Type> anyType = Arbitraries.of( Edge.Type.class );
        return Combinators.combine( anyType, anyNode(), anyNode() ).as( Edge.Plain::new );
    }

    @Provide
    Arbitrary<Edge.Decorated.Label> anyEdgeLabel() {
        return Arbitraries.of( Edge.Decorated.Label.class );
    }

    @Provide
    Arbitrary<Edge> anyDecoratedEdge() {
        final Arbitrary<Edge.Type> anyType = Arbitraries.of( Edge.Type.class );
        return Combinators.combine( anyType, anyNode(), anyNode(), anyEdgeLabel() ).as( Edge.Decorated::new );
    }

    @Provide
    Arbitrary<Edge> anyEdge() {
        return Arbitraries.oneOf( List.of( anyPlainEdge(), anyDecoratedEdge() ) );
    }

    @Provide
    Arbitrary<Set<GraphElement>> anyGraph() {
        // Create singleton sets of elements to reduce size of value space
        return Arbitraries.oneOf( List.of( anyNode(), anyEdge() ) ).map( Set::of );
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
        if ( executionResult.isFailure() ) {
            System.out.println( executionResult.getCause().getMessage() );
        }
        return executionResult.isSuccess();
    }


}
