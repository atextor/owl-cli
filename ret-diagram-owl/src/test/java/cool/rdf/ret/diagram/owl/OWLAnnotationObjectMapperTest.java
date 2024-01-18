/*
 * Copyright 2024 Andreas Textor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cool.rdf.ret.diagram.owl;

import cool.rdf.ret.diagram.owl.graph.Graph;
import cool.rdf.ret.diagram.owl.graph.GraphElement;
import cool.rdf.ret.diagram.owl.graph.Node;
import cool.rdf.ret.diagram.owl.mappers.OWLAnnotationObjectMapper;
import cool.rdf.ret.diagram.owl.graph.node.AnnotationProperty;
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

        assertThat( graph.getNode().getClass() ).isEqualTo( AnnotationProperty.class );

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
