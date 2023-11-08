/*
 * Copyright 2021 Andreas Textor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.atextor.ret.diagram.owl;

import de.atextor.ret.diagram.owl.graph.Graph;
import de.atextor.ret.diagram.owl.graph.node.AnnotationProperty;
import de.atextor.ret.diagram.owl.graph.node.DataProperty;
import de.atextor.ret.diagram.owl.graph.node.Inverse;
import de.atextor.ret.diagram.owl.graph.node.ObjectProperty;
import de.atextor.ret.diagram.owl.mappers.OWLPropertyExpressionMapper;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class OWLPropertyExpressionMapperTest extends MapperTestBase {
    private final OWLPropertyExpressionMapper mapper =
        new OWLPropertyExpressionMapper( createTestMappingConfiguration() );

    @Test
    public void testOWLObjectInverseOf() {
        final String ontology = """
            :foo a owl:ObjectProperty .
            :bar a owl:ObjectProperty ;
               owl:equivalentProperty [
                  owl:inverseOf :foo
               ] .
            """;
        final OWLEquivalentObjectPropertiesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_OBJECT_PROPERTIES );
        final Graph graph = axiom.operands().filter( operand -> !operand.isNamed() ).findFirst().get()
            .accept( mapper );

        assertThat( graph.getNode().getClass() ).isEqualTo( Inverse.class );
        assertThat( graph.getOtherElements().collect( Collectors.toSet() ) ).hasSize( 2 );
    }

    @Test
    public void testOWLObjectProperty() {
        final String ontology = """
            :foo a owl:ObjectProperty .
            """;
        final OWLDeclarationAxiom axiom = getAxiom( ontology, AxiomType.DECLARATION );
        final OWLObjectProperty property = axiom.getEntity().asOWLObjectProperty();

        final Graph graph = property.accept( mapper );

        assertThat( graph.getNode().getClass() ).isEqualTo( ObjectProperty.class );

        assertThat( ( (ObjectProperty) graph.getNode() ).getName() ).isEqualTo( "foo" );
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

        assertThat( graph.getNode().getClass() ).isEqualTo( DataProperty.class );

        assertThat( ( (DataProperty) graph.getNode() ).getName() ).isEqualTo( "foo" );
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

        assertThat( graph.getNode().getClass() ).isEqualTo( AnnotationProperty.class );

        assertThat( ( (AnnotationProperty) graph.getNode() ).getName() ).isEqualTo( "foo" );
        assertThat( graph.getOtherElements() ).isEmpty();
    }
}
