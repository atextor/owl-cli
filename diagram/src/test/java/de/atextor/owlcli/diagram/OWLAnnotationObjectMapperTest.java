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
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.node.AnnotationProperty;
import de.atextor.owlcli.diagram.mappers.OWLAnnotationObjectMapper;
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
