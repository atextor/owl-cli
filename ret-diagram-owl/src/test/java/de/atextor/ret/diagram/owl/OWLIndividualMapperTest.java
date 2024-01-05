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

package de.atextor.ret.diagram.owl;

import de.atextor.ret.diagram.owl.graph.Graph;
import de.atextor.ret.diagram.owl.graph.node.Individual;
import de.atextor.ret.diagram.owl.mappers.DefaultMappingConfiguration;
import de.atextor.ret.diagram.owl.mappers.OWLIndividualMapper;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;

import static org.assertj.core.api.Assertions.assertThat;

public class OWLIndividualMapperTest extends MapperTestBase {
    private final OWLIndividualMapper mapper =
        new OWLIndividualMapper( DefaultMappingConfiguration.builder().nameMapper( testNameMapper ).build() );

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
        assertThat( graph.getNode().getClass() ).isEqualTo( Individual.class );

        assertThat( ( (Individual) graph.getNode() ).getName() ).isEqualTo( "[]" );
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
        assertThat( graph.getNode().getClass() ).isEqualTo( Individual.class );

        assertThat( (Individual) graph.getNode() ).matches( isNodeWithId( "Max" ) );
        assertThat( graph.getOtherElements() ).isEmpty();
    }
}
