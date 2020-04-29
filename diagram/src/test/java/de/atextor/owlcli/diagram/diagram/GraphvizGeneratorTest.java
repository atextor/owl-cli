package de.atextor.owlcli.diagram.diagram;

import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.PlainEdge;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.IRI;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class GraphvizGeneratorTest {
    GraphvizGenerator generator = new GraphvizGenerator( Configuration.builder().build() );
    Node.Id from1 = new Node.Id( "foo" );
    Node.Id to1 = new Node.Id( "bar" );
    String name1 = "baz";
    int cardinality1 = 5;
    final String value1 = "theValue";

    Predicate<GraphvizDocument.Statement> contains( final String needle ) {
        return statement -> statement.getContent().contains( needle );
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
            final Stream<GraphElement> elements = Stream.of( new PlainEdge( edgeType, from1, to1 ) );
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
    void testNodeelementsTypeClass() {
        testNamedNode( new Node.Class( from1, name1 ) );
    }

    @Test
    void testNodeDataProperty() {
        testNamedNode( new Node.DataProperty( from1, name1 ) );
    }

    @Test
    void testNodeObjectProperty() {
        testNamedNode( new Node.ObjectProperty( from1, name1 ) );
    }

    @Test
    void testNodeAnnotationProperty() {
        testNamedNode( new Node.AnnotationProperty( from1, name1 ) );
    }

    @Test
    void testNodeIndividual() {
        testNamedNode( new Node.Individual( from1, name1 ) );
    }

    @Test
    void testNodeLiteral() {
        testValueNode( new Node.Literal( from1, value1 ) );
    }

    @Test
    void testNodePropertyChain() {
        testValueNode( new Node.PropertyChain( from1, value1 ) );
    }

    @Test
    void testNodeDatatype() {
        testNamedNode( new Node.Datatype( from1, name1 ) );
    }

    @Test
    void testNodeExistentialRestriction() {
        testNodeWithId( new Node.ExistentialRestriction( from1 ) );
    }

    @Test
    void testNodeValueRestriction() {
        testNodeWithId( new Node.ValueRestriction( from1 ) );
    }

    @Test
    void testNodeUniversalRestriction() {
        testNodeWithId( new Node.UniversalRestriction( from1 ) );
    }

    @Test
    void testNodeIntersection() {
        testNodeWithId( new Node.Intersection( from1 ) );
    }

    @Test
    void testNodeUnion() {
        testNodeWithId( new Node.Union( from1 ) );
    }

    @Test
    void testNodeDisjointness() {
        testNodeWithId( new Node.Disjointness( from1 ) );
    }

    @Test
    void testNodeDisjointUnion() {
        testNodeWithId( new Node.DisjointUnion( from1 ) );
    }

    @Test
    void testNodeClosedClass() {
        testNodeWithId( new Node.ClosedClass( from1 ) );
    }

    @Test
    void testNodeComplement() {
        testNodeWithId( new Node.Complement( from1 ) );
    }

    @Test
    void testNodeSelf() {
        testNodeWithId( new Node.Self( from1 ) );
    }

    @Test
    void testNodeObjectMinimalCardinality() {
        testCardinalityNode( new Node.ObjectMinimalCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeObjectQualifiedMinimalCardinality() {
        testCardinalityNode( new Node.ObjectQualifiedExactCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeObjectMaximalCardinality() {
        testCardinalityNode( new Node.ObjectMaximalCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeObjectQualifiedMaximalCardinality() {
        testCardinalityNode( new Node.ObjectQualifiedMaximalCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeObjectExactCardinality() {
        testCardinalityNode( new Node.ObjectExactCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeObjectQualifiedExactCardinality() {
        testCardinalityNode( new Node.ObjectQualifiedExactCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeDataMinimalCardinality() {
        testCardinalityNode( new Node.DataMinimalCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeDataMaximalCardinality() {
        testCardinalityNode( new Node.DataMaximalCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeDataExactCardinality() {
        testCardinalityNode( new Node.DataExactCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeInvisible() {
        testNodeWithId( new Node.Invisible( from1 ) );
    }

    @Test
    void testNodeIRIReference() {
        testValueNode( new Node.IRIReference( from1, IRI.create( "http://test.de#" + value1 ) ) );
    }
}
