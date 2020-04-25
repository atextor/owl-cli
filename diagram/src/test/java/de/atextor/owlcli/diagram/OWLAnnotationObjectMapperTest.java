package de.atextor.owlcli.diagram;

import de.atextor.owlcli.diagram.graph.Graph;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.NodeType;
import de.atextor.owlcli.diagram.mappers.OWLAnnotationObjectMapper;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;

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

        final String fooId = "Foo";
        testIdentifierMapper.pushAnonId( new Node.Id( fooId, iri( "Foo" ) ) );

        final OWLAnnotationAssertionAxiom axiom = getAxiom( ontology, AxiomType.ANNOTATION_ASSERTION );
        final Graph graph = mapper.visit( axiom.getAnnotation() );

        assertThat( graph.getNode().getClass() ).isEqualTo( NodeType.AnnotationProperty.class );

        final Set<GraphElement> remainingElements = graph.getOtherElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isEmpty();
    }

    @Test
    public void testIRI() {
        final String ontology = """
            :comment a owl:AnnotationProperty .
            :Dog a owl:Class ;
                :comment :Foo .
            """;

        final String fooId = "Foo";
        testIdentifierMapper.pushAnonId( new Node.Id( fooId, iri( "Foo" ) ) );

        final OWLAnnotationAssertionAxiom axiom = getAxiom( ontology, AxiomType.ANNOTATION_ASSERTION );
        final Graph graph = mapper.visit( axiom.getValue().asIRI().get() );

        assertThat( graph.getNode() ).matches( isNodeWithId( fooId ) );
        assertThat( graph.getOtherElements() ).isEmpty();
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
