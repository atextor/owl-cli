package de.atextor.owlcli.diagram;

import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.NodeType;
import de.atextor.owlcli.diagram.mappers.OWLAnnotationObjectMapper;
import de.atextor.owlcli.diagram.mappers.Result;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class OWLAnnotationObjectMapperTest extends MapperTestBase {
    private final OWLAnnotationObjectMapper mapper = new OWLAnnotationObjectMapper( createTestMappingConfiguration() );

    @Test
    public void testOWLAnnotation() {
        final String ontology = """
                :comment a owl:AnnotationProperty .
                :Dog a owl:Class ;
                    :comment :Foo .
                """;

        final OWLAnnotationAssertionAxiom axiom = getAxiom( ontology, AxiomType.ANNOTATION_ASSERTION );
        final Result result = mapper.visit( axiom.getAnnotation() );

        assertThat( result.getNode().getClass() ).isEqualTo( NodeType.AnnotationRole.class );

        final Set<GraphElement> remainingElements = result.getRemainingElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 1 );
        final Node theNode = nodes.get( 0 );
        assertThat( theNode.getClass() ).isEqualTo( NodeType.Literal.class );
        final NodeType.Literal literal = (NodeType.Literal) theNode;

        assertThat( literal.getId().getId() ).isEqualTo( "Foo" );
        assertThat( literal.getValue() ).isEqualTo( "http://test.de/Foo" );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 1 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( result.getNode().getId(), literal.getId() ) );
    }

    @Test
    public void testOWLAnnotationPropertyDomainAxiom() {
    }

    @Test
    public void testOWLAnnotationPropertyRangeAxiom() {
    }

    @Test
    public void testIRI() {
        final String ontology = """
                :comment a owl:AnnotationProperty .
                :Dog a owl:Class ;
                    :comment :Foo .
                """;

        final OWLAnnotationAssertionAxiom axiom = getAxiom( ontology, AxiomType.ANNOTATION_ASSERTION );
        final Result result = mapper.visit( axiom.getValue().asIRI().get() );

        assertThat( result.getNode() ).matches( isNodeWithId( "Foo" ) );
        assertThat( result.getRemainingElements() ).isEmpty();
    }

    @Test
    public void testOWLAnonymousIndividual() {
        new OWLIndividualMapperTest().testOWLAnonymousIndividual();
    }

    @Test
    public void testOWLLiteral() {
        new OWLDataMapperTest().testOWLLiteral();
    }
}
