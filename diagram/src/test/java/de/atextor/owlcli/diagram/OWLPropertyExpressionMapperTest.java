package de.atextor.owlcli.diagram;

import de.atextor.owlcli.diagram.graph.NodeType;
import de.atextor.owlcli.diagram.mappers.OWLPropertyExpressionMapper;
import de.atextor.owlcli.diagram.mappers.Result;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import static org.assertj.core.api.Assertions.assertThat;

public class OWLPropertyExpressionMapperTest extends MapperTestBase {
    private final OWLPropertyExpressionMapper mapper =
        new OWLPropertyExpressionMapper( createTestMappingConfiguration() );

    @Test
    public void testOWLObjectInverseOf() {
    }

    @Test
    public void testOWLObjectProperty() {
        final String ontology = """
            :foo a owl:ObjectProperty .
            """;
        final OWLDeclarationAxiom axiom = getAxiom( ontology, AxiomType.DECLARATION );
        final OWLObjectProperty property = axiom.getEntity().asOWLObjectProperty();

        final Result result = property.accept( mapper );

        assertThat( result.getNode().getClass() ).isEqualTo( NodeType.AbstractRole.class );

        assertThat( ( (NodeType.AbstractRole) result.getNode() ).getName() ).isEqualTo( "foo" );
        assertThat( result.getRemainingElements() ).isEmpty();
    }

    @Test
    public void testOWLDataProperty() {
        final String ontology = """
            :foo a owl:DatatypeProperty .
            """;
        final OWLDeclarationAxiom axiom = getAxiom( ontology, AxiomType.DECLARATION );
        final OWLDataProperty property = axiom.getEntity().asOWLDataProperty();

        final Result result = property.accept( mapper );

        assertThat( result.getNode().getClass() ).isEqualTo( NodeType.ConcreteRole.class );

        assertThat( ( (NodeType.ConcreteRole) result.getNode() ).getName() ).isEqualTo( "foo" );
        assertThat( result.getRemainingElements() ).isEmpty();
    }

    @Test
    public void testOWLAnnotationProperty() {
    }
}
