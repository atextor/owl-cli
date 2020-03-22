package de.atextor.owlcli.diagram;

import de.atextor.owlcli.diagram.graph.Graph;
import de.atextor.owlcli.diagram.graph.NodeType;
import de.atextor.owlcli.diagram.mappers.DefaultMappingConfiguration;
import de.atextor.owlcli.diagram.mappers.OWLIndividualMapper;
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

        final Graph graph = mapper.visit( individual.asOWLAnonymousIndividual() );
        assertThat( graph.getNode().getClass() ).isEqualTo( NodeType.Individual.class );

        assertThat( ( (NodeType.Individual) graph.getNode() ).getName() ).isEqualTo( "[]" );
        assertThat( graph.getOtherElements() ).isEmpty();
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

        final Graph graph = mapper.visit( individual.asOWLNamedIndividual() );
        assertThat( graph.getNode().getClass() ).isEqualTo( NodeType.Individual.class );

        assertThat( (NodeType.Individual) graph.getNode() ).matches( isNodeWithId( "Max" ) );
        assertThat( graph.getOtherElements() ).isEmpty();
    }
}
