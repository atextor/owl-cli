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
import de.atextor.ret.diagram.owl.graph.node.ExistentialRestriction;
import de.atextor.ret.diagram.owl.graph.node.IRIReference;
import de.atextor.ret.diagram.owl.graph.node.Individual;
import de.atextor.ret.diagram.owl.graph.node.Intersection;
import de.atextor.ret.diagram.owl.graph.node.Invisible;
import de.atextor.ret.diagram.owl.graph.node.Literal;
import de.atextor.ret.diagram.owl.graph.node.ObjectExactCardinality;
import de.atextor.ret.diagram.owl.graph.node.ObjectMaximalCardinality;
import de.atextor.ret.diagram.owl.graph.node.ObjectMinimalCardinality;
import de.atextor.ret.diagram.owl.graph.node.ObjectProperty;
import de.atextor.ret.diagram.owl.graph.node.ObjectQualifiedExactCardinality;
import de.atextor.ret.diagram.owl.graph.node.ObjectQualifiedMaximalCardinality;
import de.atextor.ret.diagram.owl.graph.node.PropertyChain;
import de.atextor.ret.diagram.owl.graph.node.Self;
import de.atextor.ret.diagram.owl.graph.node.Union;
import de.atextor.ret.diagram.owl.graph.node.UniversalRestriction;
import de.atextor.ret.diagram.owl.graph.node.ValueRestriction;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.IRI;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class GraphvizGeneratorTest {
    final GraphvizGenerator generator = new GraphvizGenerator( Configuration.builder().build() );

    final Node.Id from1 = new Node.Id( "foo" );

    final Node.Id to1 = new Node.Id( "bar" );

    final String name1 = "baz";

    final int cardinality1 = 5;

    final String value1 = "theValue";

    Predicate<GraphvizDocument.Statement> contains( final String needle ) {
        return statement -> statement.content().contains( needle );
    }

    @Test
    void testEmptyElements() {
        final Stream<GraphElement> elements = Stream.empty();
        final GraphvizDocument result = generator.apply( elements );

        assertThat( result.getNodeStatements() ).isEmpty();
        assertThat( result.getEdgeStatements() ).isEmpty();
    }

    @Test
    void testEdgeTypes() {
        final BiConsumer<Edge.Type, String> checkType = ( edgeType, needle ) -> {
            final Stream<GraphElement> elements = Stream.of( new Edge.Plain( edgeType, new Class( from1,
                "from1" ), new Class( to1, "to1" ) ) );
            final GraphvizDocument result = generator.apply( elements );
            assertThat( result.getNodeStatements() ).isEmpty();
            assertThat( result.getEdgeStatements() ).anyMatch( contains( needle ) );
        };

        checkType.accept( Edge.Type.DEFAULT_ARROW, "arrowhead = normal" );
        checkType.accept( Edge.Type.HOLLOW_ARROW, "arrowhead = empty" );
        checkType.accept( Edge.Type.DOUBLE_ENDED_HOLLOW_ARROW, "dir = both, arrowhead = empty, arrowtail = empty" );
        checkType.accept( Edge.Type.NO_ARROW, "arrowhead = none" );
    }

    private void testNodeWithId( final Node node ) {
        final Stream<GraphElement> elements = Stream.of( node );
        final GraphvizDocument result = generator.apply( elements );
        assertThat( result.getEdgeStatements() ).isEmpty();

        assertThat( result.getNodeStatements() ).hasSize( 1 );
        assertThat( result.getNodeStatements() ).anyMatch( contains( from1.getId() ) );
    }

    private void testNamedNode( final Node.NamedNode node ) {
        final Stream<GraphElement> elements = Stream.of( node );
        final GraphvizDocument result = generator.apply( elements );
        assertThat( result.getEdgeStatements() ).isEmpty();

        assertThat( result.getNodeStatements() ).hasSize( 1 );
        assertThat( result.getNodeStatements() ).anyMatch( contains( from1.getId() ) );
        assertThat( result.getNodeStatements() ).anyMatch( contains( name1 ) );
    }

    private void testCardinalityNode( final Node.CardinalityNode node ) {
        final Stream<GraphElement> elements = Stream.of( node );
        final GraphvizDocument result = generator.apply( elements );
        assertThat( result.getEdgeStatements() ).isEmpty();

        assertThat( result.getNodeStatements() ).hasSize( 1 );
        assertThat( result.getNodeStatements() ).anyMatch( contains( from1.getId() ) );
        assertThat( result.getNodeStatements() ).anyMatch( contains( "" + cardinality1 ) );
    }

    private void testValueNode( final Node node ) {
        final Stream<GraphElement> elements = Stream.of( node );
        final GraphvizDocument result = generator.apply( elements );
        assertThat( result.getEdgeStatements() ).isEmpty();

        assertThat( result.getNodeStatements() ).hasSize( 1 );
        assertThat( result.getNodeStatements() ).anyMatch( contains( from1.getId() ) );
        assertThat( result.getNodeStatements() ).anyMatch( contains( value1 ) );
    }

    @Test
    void testNodeElementsTypeClass() {
        testNamedNode( new Class( from1, name1 ) );
    }

    @Test
    void testNodeDataProperty() {
        testNamedNode( new DataProperty( from1, name1 ) );
    }

    @Test
    void testNodeObjectProperty() {
        testNamedNode( new ObjectProperty( from1, name1 ) );
    }

    @Test
    void testNodeAnnotationProperty() {
        testNamedNode( new AnnotationProperty( from1, name1 ) );
    }

    @Test
    void testNodeIndividual() {
        testNamedNode( new Individual( from1, name1 ) );
    }

    @Test
    void testNodeLiteral() {
        testValueNode( new Literal( from1, value1 ) );
    }

    @Test
    void testNodePropertyChain() {
        testValueNode( new PropertyChain( from1, value1 ) );
    }

    @Test
    void testNodeDatatype() {
        testNamedNode( new Datatype( from1, name1 ) );
        testNodeWithId( new Datatype( from1, "int[> 4, <= 10]" ) );
    }

    @Test
    void testNodeExistentialRestriction() {
        testNodeWithId( new ExistentialRestriction( from1 ) );
    }

    @Test
    void testNodeValueRestriction() {
        testNodeWithId( new ValueRestriction( from1 ) );
    }

    @Test
    void testNodeUniversalRestriction() {
        testNodeWithId( new UniversalRestriction( from1 ) );
    }

    @Test
    void testNodeIntersection() {
        testNodeWithId( new Intersection( from1 ) );
    }

    @Test
    void testNodeUnion() {
        testNodeWithId( new Union( from1 ) );
    }

    @Test
    void testNodeDisjointness() {
        testNodeWithId( new Disjointness( from1 ) );
    }

    @Test
    void testNodeDisjointUnion() {
        testNodeWithId( new DisjointUnion( from1 ) );
    }

    @Test
    void testNodeClosedClass() {
        testNodeWithId( new ClosedClass( from1 ) );
    }

    @Test
    void testNodeComplement() {
        testNodeWithId( new Complement( from1 ) );
    }

    @Test
    void testNodeSelf() {
        testNodeWithId( new Self( from1 ) );
    }

    @Test
    void testNodeObjectMinimalCardinality() {
        testCardinalityNode( new ObjectMinimalCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeObjectQualifiedMinimalCardinality() {
        testCardinalityNode( new ObjectQualifiedExactCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeObjectMaximalCardinality() {
        testCardinalityNode( new ObjectMaximalCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeObjectQualifiedMaximalCardinality() {
        testCardinalityNode( new ObjectQualifiedMaximalCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeObjectExactCardinality() {
        testCardinalityNode( new ObjectExactCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeObjectQualifiedExactCardinality() {
        testCardinalityNode( new ObjectQualifiedExactCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeDataMinimalCardinality() {
        testCardinalityNode( new DataMinimalCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeDataMaximalCardinality() {
        testCardinalityNode( new DataMaximalCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeDataExactCardinality() {
        testCardinalityNode( new DataExactCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeInvisible() {
        testNodeWithId( new Invisible( from1 ) );
    }

    @Test
    void testNodeIRIReference() {
        testValueNode( new IRIReference( from1, IRI.create( "http://test.de#" + value1 ) ) );
    }
}
