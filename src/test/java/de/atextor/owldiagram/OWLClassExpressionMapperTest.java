package de.atextor.owldiagram;

import de.atextor.owldiagram.graph.NodeType;
import de.atextor.owldiagram.mappers.OWLClassExpressionMapper;
import de.atextor.owldiagram.mappers.Result;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;

import static org.assertj.core.api.Assertions.assertThat;

public class OWLClassExpressionMapperTest extends MapperTestBase {
    OWLClassExpressionMapper mapper = new OWLClassExpressionMapper();

    @Test
    public void testOWLClass() {
        final OWLDeclarationAxiom axiom = getAxiom( ":Foo a owl:Class ." );
        final OWLClass class_ = (OWLClass) axiom.getEntity();

        final Result result = mapper.visit( class_ );
        assertThat( result.getNode().getClass() ).isEqualTo( NodeType.Class.class );
        assertThat( result.getRemainingElements() ).isEmpty();
    }
}
