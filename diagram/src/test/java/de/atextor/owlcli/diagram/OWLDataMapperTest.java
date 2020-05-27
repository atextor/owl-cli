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

import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.Graph;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.mappers.OWLDataMapper;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class OWLDataMapperTest extends MapperTestBase {
    private final OWLDataMapper mapper = new OWLDataMapper( createTestMappingConfiguration() );

    @Test
    public void testOWLDataComplementOf() {
        final String ontology = """
            :name a owl:DatatypeProperty .
            :Dog a owl:Class ;
               owl:equivalentClass [
                  a owl:Restriction ;
                  owl:onProperty :name ;
                  owl:someValuesFrom [
                    a rdfs:Datatype ;
                    owl:datatypeComplementOf xsd:string
                  ]
               ] .
            """;

        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLDataSomeValuesFrom someValuesFrom = (OWLDataSomeValuesFrom) axiom.getOperandsAsList().get( 1 );
        final OWLDataComplementOf owlDataOneOf = (OWLDataComplementOf) someValuesFrom.getFiller();

        final String complementId = "complementNode";
        testIdentifierMapper.pushAnonId( new Node.Id( complementId ) );

        final Graph graph = mapper.visit( owlDataOneOf );
        assertThat( graph.getNode().getClass() ).isEqualTo( Node.Complement.class );
        final Set<GraphElement> remainingElements = graph.getOtherElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "string" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( complementId ) );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 1 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( complementId, "string" ) );
    }

    @Test
    public void testOWLDataOneOf() {
        final String ontology = """
            :name a owl:DatatypeProperty .
            :Dog a owl:Class ;
               owl:equivalentClass [
                  a owl:Restriction ;
                  owl:onProperty :name ;
                  owl:someValuesFrom [
                    a rdfs:Datatype ;
                    owl:oneOf ( "Fido" "Bello" )
                  ]
               ] .
            """;

        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLDataSomeValuesFrom someValuesFrom = (OWLDataSomeValuesFrom) axiom.getOperandsAsList().get( 1 );

        final OWLDataOneOf owlDataOneOf = (OWLDataOneOf) someValuesFrom.getFiller();
        final String restrictionNodeId = "restrictionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( "Fido", iri( "Fido" ) ) );
        testIdentifierMapper.pushAnonId( new Node.Id( "Bello", iri( "Bello" ) ) );
        testIdentifierMapper.pushAnonId( new Node.Id( restrictionNodeId ) );

        final Graph graph = mapper.visit( owlDataOneOf );
        final Node restrictionNode = graph.getNode();
        assertThat( restrictionNode ).isInstanceOf( Node.ClosedClass.class );
        final Set<GraphElement> remainingElements = graph.getOtherElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "Fido" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "Bello" ) );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 2 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( restrictionNodeId, "Fido" ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( restrictionNodeId, "Bello" ) );
    }

    @Test
    public void testOWLDataIntersectionOf() {
        final String ontology = """
            :numberOfWings a owl:DatatypeProperty .
            :Dog a owl:Class ;
               owl:equivalentClass [
                  a owl:Restriction ;
                  owl:onProperty :numberOfWings ;
                  owl:someValuesFrom [
                    a rdfs:Datatype ;
                    owl:intersectionOf ( xsd:nonNegativeInteger xsd:nonPositiveInteger )
                  ]
               ] .
            """;

        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLDataSomeValuesFrom someValuesFrom = (OWLDataSomeValuesFrom) axiom.getOperandsAsList().get( 1 );
        final OWLDataIntersectionOf intersection = (OWLDataIntersectionOf) someValuesFrom.getFiller();

        final String intersectionId = "intersectionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( intersectionId ) );

        final Graph graph = mapper.visit( intersection );
        assertThat( graph.getNode().getClass() ).isEqualTo( Node.Intersection.class );
        final Set<GraphElement> remainingElements = graph.getOtherElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 3 );
        assertThat( nodes ).anyMatch( isNodeWithId( "nonNegativeInteger" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "nonPositiveInteger" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( intersectionId ) );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 2 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( intersectionId, "nonNegativeInteger" ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( intersectionId, "nonPositiveInteger" ) );
    }

    @Test
    public void testOWLDataUnionOf() {
        final String ontology = """
            :numberOfWings a owl:DatatypeProperty .
            :Dog a owl:Class ;
               owl:equivalentClass [
                  a owl:Restriction ;
                  owl:onProperty :numberOfWings ;
                  owl:someValuesFrom [
                    a rdfs:Datatype ;
                    owl:unionOf ( xsd:nonNegativeInteger xsd:nonPositiveInteger )
                  ]
               ] .
            """;

        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLDataSomeValuesFrom someValuesFrom = (OWLDataSomeValuesFrom) axiom.getOperandsAsList().get( 1 );
        final OWLDataUnionOf intersection = (OWLDataUnionOf) someValuesFrom.getFiller();

        final String intersectionId = "intersectionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( intersectionId ) );

        final Graph graph = mapper.visit( intersection );
        assertThat( graph.getNode().getClass() ).isEqualTo( Node.Union.class );
        final Set<GraphElement> remainingElements = graph.getOtherElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 3 );
        assertThat( nodes ).anyMatch( isNodeWithId( "nonNegativeInteger" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "nonPositiveInteger" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( intersectionId ) );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 2 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( intersectionId, "nonNegativeInteger" ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( intersectionId, "nonPositiveInteger" ) );
    }

    @Test
    public void testOWLDatatypeRestriction() {
        final String ontology = """
            :foo a owl:DatatypeProperty ;
                rdfs:range [
                    a rdfs:Datatype ;
                    owl:onDatatype xsd:int ;
                    owl:withRestrictions (
                        [ xsd:minExclusive "4"^^xsd:int ] [ xsd:maxInclusive "10"^^xsd:int ]
                    )
                ] .
            """;

        final OWLDataPropertyRangeAxiom axiom = getAxiom( ontology, AxiomType.DATA_PROPERTY_RANGE );
        final OWLDataRange range = axiom.getRange();

        final Graph graph = range.accept( mapper );
        final Set<GraphElement> elements = graph.getElementSet();
        assertThat( edges( elements ) ).isEmpty();

        final List<Node> nodes = nodes( elements );
        assertThat( nodes ).hasSize( 1 );

        final Node node = nodes.get( 0 );
        assertThat( node.getClass() ).isEqualTo( Node.Datatype.class );
        assertThat( node.as( Node.Datatype.class ).getName() ).matches( ".*\\[> 4, <= 10]" );
    }

    @Test
    public void testOWLFacetRestriction() {
    }

    @Test
    public void testOWLDatatype() {
    }

    @Test
    public void testOWLLiteral() {
        final String ontology = """
            :Dog a owl:Class .
            :name a owl:DatatypeProperty .
            :Bello a :Dog ;
               :name "Bello" .
            """;

        final OWLDataPropertyAssertionAxiom axiom = getAxiom( ontology, AxiomType.DATA_PROPERTY_ASSERTION );
        final OWLLiteral object = axiom.getObject();

        final String literalId = "literalNode";
        testIdentifierMapper.pushAnonId( new Node.Id( literalId ) );

        final Graph graph = mapper.visit( object );

        assertThat( graph.getNode() ).matches( isNodeWithId( literalId ) );
        assertThat( graph.getOtherElements() ).isEmpty();
    }
}
