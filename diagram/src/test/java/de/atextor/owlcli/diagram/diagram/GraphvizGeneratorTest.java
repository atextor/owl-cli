package de.atextor.owlcli.diagram.diagram;

import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.NodeType;
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

    private void testNamedNode( final NodeType.NamedNode node ) {
        final Stream<GraphElement> elements = Stream.of( node );
        final GraphvizDocument result = generator.apply( elements );
        assertThat( result.getEdgeStatements() ).isEmpty();

        assertThat( result.getNodeStatements() ).hasSize( 1 );
        assertThat( result.getNodeStatements() ).anyMatch( contains( from1.getId() ) );
        assertThat( result.getNodeStatements() ).anyMatch( contains( name1 ) );
    }

    private void testCardinalityNode( final NodeType.CardinalityNode node ) {
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
        testNamedNode( new NodeType.Class( from1, name1 ) );
    }

    @Test
    void testNodeTypeDataProperty() {
        testNamedNode( new NodeType.DataProperty( from1, name1 ) );
    }

    @Test
    void testNodeTypeObjectProperty() {
        testNamedNode( new NodeType.ObjectProperty( from1, name1 ) );
    }

    @Test
    void testNodeTypeAnnotationProperty() {
        testNamedNode( new NodeType.AnnotationProperty( from1, name1 ) );
    }

    @Test
    void testNodeTypeIndividual() {
        testNamedNode( new NodeType.Individual( from1, name1 ) );
    }

    @Test
    void testNodeTypeLiteral() {
        testValueNode( new NodeType.Literal( from1, value1 ) );
    }

    @Test
    void testNodeTypePropertyChain() {
        testValueNode( new NodeType.PropertyChain( from1, value1 ) );
    }

    @Test
    void testNodeTypeDatatype() {
        testNamedNode( new NodeType.Datatype( from1, name1 ) );
    }

    @Test
    void testNodeTypeExistentialRestriction() {
        testNodeWithId( new NodeType.ExistentialRestriction( from1 ) );
    }

    @Test
    void testNodeTypeValueRestriction() {
        testNodeWithId( new NodeType.ValueRestriction( from1 ) );
    }

    @Test
    void testNodeTypeUniversalRestriction() {
        testNodeWithId( new NodeType.UniversalRestriction( from1 ) );
    }

    @Test
    void testNodeTypeIntersection() {
        testNodeWithId( new NodeType.Intersection( from1 ) );
    }

    @Test
    void testNodeTypeUnion() {
        testNodeWithId( new NodeType.Union( from1 ) );
    }

    @Test
    void testNodeTypeDisjointness() {
        testNodeWithId( new NodeType.Disjointness( from1 ) );
    }

    @Test
    void testNodeTypeDisjointUnion() {
        testNodeWithId( new NodeType.DisjointUnion( from1 ) );
    }

    @Test
    void testNodeTypeClosedClass() {
        testNodeWithId( new NodeType.ClosedClass( from1 ) );
    }

    @Test
    void testNodeTypeComplement() {
        testNodeWithId( new NodeType.Complement( from1 ) );
    }

    @Test
    void testNodeTypeSelf() {
        testNodeWithId( new NodeType.Self( from1 ) );
    }

    @Test
    void testNodeTypeObjectMinimalCardinality() {
        testCardinalityNode( new NodeType.ObjectMinimalCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeTypeObjectQualifiedMinimalCardinality() {
        testCardinalityNode( new NodeType.ObjectQualifiedExactCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeTypeObjectMaximalCardinality() {
        testCardinalityNode( new NodeType.ObjectMaximalCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeTypeObjectQualifiedMaximalCardinality() {
        testCardinalityNode( new NodeType.ObjectQualifiedMaximalCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeTypeObjectExactCardinality() {
        testCardinalityNode( new NodeType.ObjectExactCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeTypeObjectQualifiedExactCardinality() {
        testCardinalityNode( new NodeType.ObjectQualifiedExactCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeTypeDataMinimalCardinality() {
        testCardinalityNode( new NodeType.DataMinimalCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeTypeDataMaximalCardinality() {
        testCardinalityNode( new NodeType.DataMaximalCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeTypeDataExactCardinality() {
        testCardinalityNode( new NodeType.DataExactCardinality( from1, cardinality1 ) );
    }

    @Test
    void testNodeTypeInvisible() {
        testNodeWithId( new NodeType.Invisible( from1 ) );
    }

    @Test
    void testNodeTypeIRIReference() {
        testValueNode( new NodeType.IRIReference( from1, IRI.create( "http://test.de#" + value1 ) ) );
    }
}

