package de.atextor.owlcli.diagram;

import de.atextor.owlcli.diagram.mappers.OWLAnnotationObjectMapper;
import org.junit.jupiter.api.Test;

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
