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
import de.atextor.owlcli.diagram.graph.Node;
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

        assertThat( graph.getNode().getClass() ).isEqualTo( Node.ObjectProperty.class );

        assertThat( ( (Node.ObjectProperty) graph.getNode() ).getName() ).isEqualTo( "foo" );
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

        assertThat( graph.getNode().getClass() ).isEqualTo( Node.DataProperty.class );

        assertThat( ( (Node.DataProperty) graph.getNode() ).getName() ).isEqualTo( "foo" );
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

        assertThat( graph.getNode().getClass() ).isEqualTo( Node.AnnotationProperty.class );

        assertThat( ( (Node.AnnotationProperty) graph.getNode() ).getName() ).isEqualTo( "foo" );
        assertThat( graph.getOtherElements() ).isEmpty();
    }
}
