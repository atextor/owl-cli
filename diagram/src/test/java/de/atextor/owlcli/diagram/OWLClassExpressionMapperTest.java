package de.atextor.owlcli.diagram;

import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.NodeType;
import de.atextor.owlcli.diagram.mappers.OWLClassExpressionMapper;
import de.atextor.owlcli.diagram.mappers.Result;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class OWLClassExpressionMapperTest extends MapperTestBase {
    private final OWLClassExpressionMapper mapper = new OWLClassExpressionMapper( createTestMappingConfiguration() );

    @Test
    public void testOWLObjectIntersectionOf() {
        final String ontology =
            ":Dog a owl:Class ." +
                ":CanTalk a owl:Class ." +
                ":TalkingDog a owl:Class ;" +
                "   owl:equivalentClass [" +
                "      a owl:Class ;" +
                "      owl:intersectionOf ( :Dog :CanTalk )" +
                "   ] .";
        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLObjectIntersectionOf intersection = (OWLObjectIntersectionOf) axiom.getOperandsAsList().get( 1 );

        final String complementId = "intersectionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( complementId ) );

        final Result result = mapper.visit( intersection );
        assertThat( result.getNode().getClass() ).isEqualTo( NodeType.Intersection.class );
        final Set<GraphElement> remainingElements = result.getRemainingElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 3 );
        assertThat( nodes ).anyMatch( isNodeWithId( "CanTalk" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "Dog" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "intersectionNode" ) );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 2 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( "intersectionNode", "Dog" ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( "intersectionNode", "CanTalk" ) );
    }

    @Test
    public void testOWLObjectUnionOf() {

    }

    @Test
    public void testOWLObjectComplementOf() {

    }

    @Test
    public void testOWLObjectSomeValuesFrom() {

    }

    @Test
    public void testOWLObjectAllValuesFrom() {

    }

    @Test
    public void testOWLObjectHasValue() {

    }

    @Test
    public void testOWLObjectMinCardinality() {

    }

    @Test
    public void testOWLObjectExactCardinality() {

    }

    @Test
    public void testOWLObjectMaxCardinality() {

    }

    @Test
    public void testOWLObjectHasSelf() {

    }

    @Test
    public void testOWLObjectOneOf() {

    }

    @Test
    public void testOWLDataSomeValuesFrom() {

    }

    @Test
    public void testOWLDataAllValuesFrom() {

    }

    @Test
    public void testOWLDataHasValue() {

    }

    @Test
    public void testOWLDataMinCardinality() {

    }

    @Test
    public void testOWLDataExactCardinality() {

    }

    @Test
    public void testOWLDataMaxCardinality() {

    }

    @Test
    public void testOWLClass() {
        final OWLDeclarationAxiom axiom = getAxiom( ":Foo a owl:Class ." );
        final OWLClass class_ = (OWLClass) axiom.getEntity();

        final Result result = mapper.visit( class_ );
        assertThat( result.getNode().getClass() ).isEqualTo( NodeType.Class.class );
        assertThat( result.getRemainingElements() ).isEmpty();
    }
}
