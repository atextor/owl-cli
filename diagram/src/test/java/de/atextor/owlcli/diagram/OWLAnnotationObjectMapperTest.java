package de.atextor.owlcli.diagram;

import de.atextor.owlcli.diagram.mappers.OWLAnnotationObjectMapper;
import de.atextor.owlcli.diagram.mappers.Result;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import static org.assertj.core.api.Assertions.assertThat;

public class OWLAnnotationObjectMapperTest extends MapperTestBase {
    private final OWLAnnotationObjectMapper mapper = new OWLAnnotationObjectMapper( createTestMappingConfiguration() );

    @Test
    public void testOWLAnnotation() {
    }

    @Test
    public void testOWLAnnotationAssertionAxiom() {
    }

    @Test
    public void testOWLSubAnnotationPropertyOfAxiom() {
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
