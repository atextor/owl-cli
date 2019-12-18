package de.atextor.owlcli.diagram;

import de.atextor.owlcli.diagram.graph.NodeType;
import de.atextor.owlcli.diagram.mappers.DefaultMappingConfiguration;
import de.atextor.owlcli.diagram.mappers.OWLIndividualMapper;
import de.atextor.owlcli.diagram.mappers.Result;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;

import static org.assertj.core.api.Assertions.assertThat;

public class OWLIndividualMapperTest extends MapperTestBase {
    private final OWLIndividualMapper mapper = new OWLIndividualMapper( DefaultMappingConfiguration
        .builder()
        .nameMapper( () -> testNameMapper )
        .build() );

    @Test
    public void testOWLAnonymousIndividual() {
        final String ontology = """
            :Dog a owl:Class .
            [
              a :Dog
            ] .
            """;
        final OWLClassAssertionAxiom axiom = getAxiom( ontology, AxiomType.CLASS_ASSERTION );
        final OWLIndividual individual = axiom.getIndividual();
        assertThat( individual.isAnonymous() ).isTrue();

        final Result result = mapper.visit( individual.asOWLAnonymousIndividual() );
        assertThat( result.getNode().getClass() ).isEqualTo( NodeType.Individual.class );

        assertThat( ( (NodeType.Individual) result.getNode() ).getName() ).isEqualTo( "[]" );
        assertThat( result.getRemainingElements() ).isEmpty();
    }

    @Test
    public void testOWLNamedIndividual() {
        final String ontology = """
            :Dog a owl:Class .
            :Max a owl:NamedIndividual, :Dog .
            """;
        final OWLClassAssertionAxiom axiom = getAxiom( ontology, AxiomType.CLASS_ASSERTION );
        final OWLIndividual individual = axiom.getIndividual();
        assertThat( individual.isAnonymous() ).isFalse();

        final Result result = mapper.visit( individual.asOWLNamedIndividual() );
        assertThat( result.getNode().getClass() ).isEqualTo( NodeType.Individual.class );

        assertThat( (NodeType.Individual) result.getNode() ).matches( isNodeWithId( "Max" ) );
        assertThat( result.getRemainingElements() ).isEmpty();
    }
}
