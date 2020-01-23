package de.atextor.owlcli.diagram;

import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.NodeType;
import de.atextor.owlcli.diagram.mappers.OWLDataMapper;
import de.atextor.owlcli.diagram.mappers.Result;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;

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

        final Result result = mapper.visit( owlDataOneOf );
        assertThat( result.getNode().getClass() ).isEqualTo( NodeType.Complement.class );
        final Set<GraphElement> remainingElements = result.getRemainingElements().collect( Collectors.toSet() );
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
        testIdentifierMapper.pushAnonId( new Node.Id( "Fido" ) );
        testIdentifierMapper.pushAnonId( new Node.Id( "Bello" ) );
        testIdentifierMapper.pushAnonId( new Node.Id( restrictionNodeId ) );

        final Result result = mapper.visit( owlDataOneOf );
        final Node restrictionNode = result.getNode();
        assertThat( restrictionNode ).isInstanceOf( NodeType.ClosedClass.class );
        final Set<GraphElement> remainingElements = result.getRemainingElements().collect( Collectors.toSet() );
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

        final Result result = mapper.visit( intersection );
        assertThat( result.getNode().getClass() ).isEqualTo( NodeType.Intersection.class );
        final Set<GraphElement> remainingElements = result.getRemainingElements().collect( Collectors.toSet() );
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

        final Result result = mapper.visit( intersection );
        assertThat( result.getNode().getClass() ).isEqualTo( NodeType.Union.class );
        final Set<GraphElement> remainingElements = result.getRemainingElements().collect( Collectors.toSet() );
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
    }

    @Test
    public void testOWLFacetRestriction() {
    }

    @Test
    public void testOWLDatatype() {
    }

    @Test
    public void testOWLLiteral() {
    }
}
