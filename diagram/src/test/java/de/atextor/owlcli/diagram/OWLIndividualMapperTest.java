/*
 * This file is part of OWL-CLI.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright (c) 2020, Andreas Textor.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www
 * .gnu.org/licenses/.
 */

package de.atextor.owlcli.diagram;

import de.atextor.owlcli.diagram.graph.Graph;
import de.atextor.owlcli.diagram.graph.node.Individual;
import de.atextor.owlcli.diagram.mappers.DefaultMappingConfiguration;
import de.atextor.owlcli.diagram.mappers.OWLIndividualMapper;
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
