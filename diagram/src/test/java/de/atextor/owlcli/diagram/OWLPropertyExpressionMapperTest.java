package de.atextor.owlcli.diagram;

import de.atextor.owlcli.diagram.graph.Graph;
import de.atextor.owlcli.diagram.graph.NodeType;
import de.atextor.owlcli.diagram.mappers.OWLPropertyExpressionMapper;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
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

        final Graph graph = property.accept( mapper );

        assertThat( graph.getNode().getClass() ).isEqualTo( NodeType.ObjectProperty.class );

        assertThat( ( (NodeType.ObjectProperty) graph.getNode() ).getName() ).isEqualTo( "foo" );
        assertThat( graph.getOtherElements() ).isEmpty();
    }

    @Test
    public void testOWLDataProperty() {
        final String ontology = """
            :foo a owl:DatatypeProperty .
            """;
        final OWLDeclarationAxiom axiom = getAxiom( ontology, AxiomType.DECLARATION );
        final OWLDataProperty property = axiom.getEntity().asOWLDataProperty();

        final Graph graph = property.accept( mapper );

        assertThat( graph.getNode().getClass() ).isEqualTo( NodeType.DataProperty.class );

        assertThat( ( (NodeType.DataProperty) graph.getNode() ).getName() ).isEqualTo( "foo" );
        assertThat( graph.getOtherElements() ).isEmpty();
    }

    @Test
    public void testOWLAnnotationProperty() {
        final String ontology = """
            :foo a owl:AnnotationProperty .
            """;
        final OWLDeclarationAxiom axiom = getAxiom( ontology, AxiomType.DECLARATION );
        final OWLAnnotationProperty property = axiom.getEntity().asOWLAnnotationProperty();

        final Graph graph = property.accept( mapper );

        assertThat( graph.getNode().getClass() ).isEqualTo( NodeType.AnnotationProperty.class );

        assertThat( ( (NodeType.AnnotationProperty) graph.getNode() ).getName() ).isEqualTo( "foo" );
        assertThat( graph.getOtherElements() ).isEmpty();
    }
}
