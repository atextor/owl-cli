package de.atextor.owlcli.diagram;

import de.atextor.owlcli.diagram.graph.NodeType;
import de.atextor.owlcli.diagram.mappers.OWLClassExpressionMapper;
import de.atextor.owlcli.diagram.mappers.Result;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;

import static org.assertj.core.api.Assertions.assertThat;

public class OWLClassExpressionMapperTest extends MapperTestBase {
    OWLClassExpressionMapper mapper = new OWLClassExpressionMapper( createTestMappingConfiguration() );

    @Test
    public void testOWLClass() {
        final OWLDeclarationAxiom axiom = getAxiom( ":Foo a owl:Class ." );
        final OWLClass class_ = (OWLClass) axiom.getEntity();

        final Result result = mapper.visit( class_ );
        assertThat( result.getNode().getClass() ).isEqualTo( NodeType.Class.class );
        assertThat( result.getRemainingElements() ).isEmpty();
    }
}