package de.atextor.owlcli.diagram;

import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.Graph;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.mappers.OWLClassExpressionMapper;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class OWLClassExpressionMapperTest extends MapperTestBase {
    private final OWLClassExpressionMapper mapper = new OWLClassExpressionMapper( createTestMappingConfiguration() );

    @Test
    public void testOWLObjectIntersectionOf() {
        final String ontology = """
            :Dog a owl:Class .
            :CanTalk a owl:Class .
            :TalkingDog a owl:Class ;
               owl:equivalentClass [
                  a owl:Class ;
                  owl:intersectionOf ( :Dog :CanTalk )
               ] .
            """;
        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLObjectIntersectionOf intersection = (OWLObjectIntersectionOf) axiom.getOperandsAsList().get( 1 );

        final String intersectionId = "intersectionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( intersectionId ) );

        final Graph graph = mapper.visit( intersection );
        assertThat( graph.getNode().getClass() ).isEqualTo( Node.Intersection.class );
        final Set<GraphElement> remainingElements = graph.getOtherElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 3 );
        assertThat( nodes ).anyMatch( isNodeWithId( "CanTalk" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "Dog" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( intersectionId ) );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 2 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( intersectionId, "Dog" ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( intersectionId, "CanTalk" ) );
    }

    @Test
    public void testOWLObjectUnionOf() {
        final String ontology = """
            :Dog a owl:Class .
            :CanTalk a owl:Class .
            :TalkingDog a owl:Class ;
               owl:equivalentClass [
                  a owl:Class ;
                  owl:unionOf ( :Dog :CanTalk )
               ] .
            """;
        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLObjectUnionOf union = (OWLObjectUnionOf) axiom.getOperandsAsList().get( 1 );

        final String unionId = "unionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( unionId ) );

        final Graph graph = mapper.visit( union );
        assertThat( graph.getNode().getClass() ).isEqualTo( Node.Union.class );
        final Set<GraphElement> remainingElements = graph.getOtherElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 3 );
        assertThat( nodes ).anyMatch( isNodeWithId( "CanTalk" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "Dog" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( unionId ) );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 2 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( unionId, "Dog" ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( unionId, "CanTalk" ) );
    }

    @Test
    public void testOWLObjectComplementOf() {
        final String ontology = """
            :Dog a owl:Class .
            :TalkingDog a owl:Class ;
               owl:equivalentClass [
                  a owl:Class ;
                  owl:complementOf :Dog
               ] .
            """;
        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLObjectComplementOf union = (OWLObjectComplementOf) axiom.getOperandsAsList().get( 1 );

        final String complementId = "complementNode";
        testIdentifierMapper.pushAnonId( new Node.Id( complementId ) );

        final Graph graph = mapper.visit( union );
        assertThat( graph.getNode().getClass() ).isEqualTo( Node.Complement.class );
        final Set<GraphElement> remainingElements = graph.getOtherElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "Dog" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( complementId ) );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 1 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( complementId, "Dog" ) );
    }

    @Test
    public void testOWLObjectSomeValuesFrom() {
        final String ontology = """
            :Dog a owl:Class .
            :hasDog a owl:ObjectProperty .
            :DogOwner a owl:Class ;
               owl:equivalentClass [
                  a owl:Restriction ;
                  owl:onProperty :hasDog ;
                  owl:someValuesFrom :Dog
               ] .
            """;
        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLObjectSomeValuesFrom someValuesFrom = (OWLObjectSomeValuesFrom) axiom.getOperandsAsList().get( 1 );

        final String restrictionNode = "restrictionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( restrictionNode ) );

        final Graph graph = mapper.visit( someValuesFrom );
        assertThat( graph.getNode().getClass() ).isEqualTo( Node.ExistentialRestriction.class );
        final Set<GraphElement> remainingElements = graph.getOtherElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "hasDog" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "Dog" ) );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 2 );
        assertThat( edges )
            .anyMatch( isEdgeWithFromAndToAndLabel( restrictionNode, "Dog", Edge.Decorated.CLASS_LABEL ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndLabel( restrictionNode, "hasDog",
            Edge.Decorated.OBJECT_PROPERTY_LABEL ) );
    }

    @Test
    public void testOWLObjectAllValuesFrom() {
        final String ontology = """
            :Dog a owl:Class .
            :hasDog a owl:ObjectProperty .
            :DogOwner a owl:Class ;
               owl:equivalentClass [
                  a owl:Restriction ;
                  owl:onProperty :hasDog ;
                  owl:allValuesFrom :Dog
               ] .
            """;
        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLObjectAllValuesFrom allValuesFrom = (OWLObjectAllValuesFrom) axiom.getOperandsAsList().get( 1 );

        final String restrictionNode = "restrictionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( restrictionNode ) );

        final Graph graph = mapper.visit( allValuesFrom );
        assertThat( graph.getNode().getClass() ).isEqualTo( Node.UniversalRestriction.class );
        final Set<GraphElement> remainingElements = graph.getOtherElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "hasDog" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "Dog" ) );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 2 );
        assertThat( edges )
            .anyMatch( isEdgeWithFromAndToAndLabel( restrictionNode, "Dog", Edge.Decorated.CLASS_LABEL ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndLabel( restrictionNode, "hasDog",
            Edge.Decorated.OBJECT_PROPERTY_LABEL ) );
    }

    @Test
    public void testOWLObjectHasValue() {
        final String ontology = """
            :bar a owl:ObjectProperty .
            :baz a owl:NamedIndividual .
            :Foo a owl:Class ;
               owl:equivalentClass [
                  a owl:Restriction ;
                  owl:onProperty :bar ;
                  owl:hasValue :baz
               ] .
            """;
        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLObjectHasValue hasValue = (OWLObjectHasValue) axiom.getOperandsAsList().get( 1 );

        final String restrictionNode = "restrictionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( restrictionNode ) );

        final Graph graph = mapper.visit( hasValue );
        assertThat( graph.getNode().getClass() ).isEqualTo( Node.ValueRestriction.class );
        final Set<GraphElement> remainingElements = graph.getOtherElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 3 );
        assertThat( nodes ).anyMatch( isNodeWithId( restrictionNode ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "baz" ) );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 2 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndLabel( restrictionNode, "baz",
            Edge.Decorated.INDIVIDUAL_LABEL ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndLabel( restrictionNode, "bar",
            Edge.Decorated.OBJECT_PROPERTY_LABEL ) );
    }

    @Test
    public void testOWLObjectUnqualifiedMinCardinality() {
        final String ontology = """
            :hasDog a owl:ObjectProperty .
            :DogOwner a owl:Class ;
               owl:equivalentClass [
                  a owl:Restriction ;
                  owl:onProperty :hasDog ;
                  owl:minCardinality "1"^^xsd:nonNegativeInteger
               ] .
            """;
        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLObjectMinCardinality minCardinality = (OWLObjectMinCardinality) axiom.getOperandsAsList().get( 1 );

        final String restrictionNodeId = "restrictionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( restrictionNodeId ) );

        final Graph graph = mapper.visit( minCardinality );
        final Node restrictionNode = graph.getNode();
        assertThat( restrictionNode ).isInstanceOf( Node.ObjectMinimalCardinality.class );
        final Set<GraphElement> remainingElements = graph.getOtherElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 1 );
        assertThat( nodes ).anyMatch( isNodeWithId( "hasDog" ) );

        assertThat( ( (Node.ObjectMinimalCardinality) restrictionNode ).getCardinality() ).isEqualTo( 1 );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 1 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndLabel( restrictionNodeId, "hasDog",
            Edge.Decorated.OBJECT_PROPERTY_LABEL ) );
    }

    @Test
    public void testOWLObjectQualifiedMinCardinality() {
        final String ontology = """
            :Dog a owl:Class .
            :hasDog a owl:ObjectProperty .
            :DogOwner a owl:Class ;
               owl:equivalentClass [
                  a owl:Restriction ;
                  owl:onProperty :hasDog ;
                  owl:minQualifiedCardinality "1"^^xsd:nonNegativeInteger ;
                  owl:onClass :Dog
               ] .
            """;

        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLObjectMinCardinality minCardinality = (OWLObjectMinCardinality) axiom.getOperandsAsList().get( 1 );

        final String restrictionNodeId = "restrictionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( restrictionNodeId ) );

        final Graph graph = mapper.visit( minCardinality );
        final Node restrictionNode = graph.getNode();
        assertThat( restrictionNode ).isInstanceOf( Node.ObjectQualifiedMinimalCardinality.class );
        final Set<GraphElement> remainingElements = graph.getOtherElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "hasDog" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "Dog" ) );

        assertThat( ( (Node.ObjectQualifiedMinimalCardinality) restrictionNode ).getCardinality() )
            .isEqualTo( 1 );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 2 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndLabel( restrictionNodeId, "hasDog",
            Edge.Decorated.OBJECT_PROPERTY_LABEL ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndLabel( restrictionNodeId, "Dog",
            Edge.Decorated.CLASS_LABEL ) );
    }

    @Test
    public void testOWLObjectExactCardinality() {
        final String ontology = """
            :Dog a owl:Class .
            :hasDog a owl:ObjectProperty .
            :DogOwner a owl:Class ;
               owl:equivalentClass [
                  a owl:Restriction ;
                  owl:onProperty :hasDog ;
                  owl:qualifiedCardinality "1"^^xsd:nonNegativeInteger ;
                  owl:onClass :Dog
               ] .
            """;

        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLObjectExactCardinality cardinality = (OWLObjectExactCardinality) axiom.getOperandsAsList().get( 1 );

        final String restrictionNodeId = "restrictionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( restrictionNodeId ) );

        final Graph graph = mapper.visit( cardinality );
        final Node restrictionNode = graph.getNode();
        assertThat( restrictionNode ).isInstanceOf( Node.ObjectQualifiedExactCardinality.class );
        final Set<GraphElement> remainingElements = graph.getOtherElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "hasDog" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "Dog" ) );

        assertThat( ( (Node.ObjectQualifiedExactCardinality) restrictionNode ).getCardinality() ).isEqualTo( 1 );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 2 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndLabel( restrictionNodeId, "hasDog",
            Edge.Decorated.OBJECT_PROPERTY_LABEL ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndLabel( restrictionNodeId, "Dog",
            Edge.Decorated.CLASS_LABEL ) );
    }

    @Test
    public void testOWLObjectMaxCardinality() {
        final String ontology = """
            :Dog a owl:Class .
            :hasDog a owl:ObjectProperty .
            :DogOwner a owl:Class ;
               owl:equivalentClass [
                  a owl:Restriction ;
                  owl:onProperty :hasDog ;
                  owl:maxQualifiedCardinality "1"^^xsd:nonNegativeInteger ;
                  owl:onClass :Dog
               ] .
            """;

        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLObjectMaxCardinality maxCardinality = (OWLObjectMaxCardinality) axiom.getOperandsAsList().get( 1 );

        final String restrictionNodeId = "restrictionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( restrictionNodeId ) );

        final Graph graph = mapper.visit( maxCardinality );
        final Node restrictionNode = graph.getNode();
        assertThat( restrictionNode ).isInstanceOf( Node.ObjectQualifiedMaximalCardinality.class );
        final Set<GraphElement> remainingElements = graph.getOtherElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "hasDog" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "Dog" ) );

        assertThat( ( (Node.ObjectQualifiedMaximalCardinality) restrictionNode ).getCardinality() )
            .isEqualTo( 1 );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 2 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndLabel( restrictionNodeId, "hasDog",
            Edge.Decorated.OBJECT_PROPERTY_LABEL ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndLabel( restrictionNodeId, "Dog",
            Edge.Decorated.CLASS_LABEL ) );
    }

    @Test
    public void testOWLObjectHasSelf() {
        final String ontology = """
            :Dog a owl:Class .
            :hasDog a owl:ObjectProperty .
            :DogOwner a owl:Class ;
               owl:equivalentClass [
                  a owl:Restriction ;
                  owl:onProperty :hasDog ;
                  owl:hasSelf true
               ] .
            """;

        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLObjectHasSelf hasSelf = (OWLObjectHasSelf) axiom.getOperandsAsList().get( 1 );

        final String restrictionNodeId = "restrictionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( restrictionNodeId ) );

        final Graph graph = mapper.visit( hasSelf );
        final Node restrictionNode = graph.getNode();
        assertThat( restrictionNode ).isInstanceOf( Node.Self.class );
        final Set<GraphElement> remainingElements = graph.getOtherElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 1 );
        assertThat( nodes ).anyMatch( isNodeWithId( "hasDog" ) );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 1 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndLabel( restrictionNodeId, "hasDog",
            Edge.Decorated.OBJECT_PROPERTY_LABEL ) );
    }

    @Test
    public void testOWLObjectOneOf() {
        final String ontology = """
            :Dog a owl:Class ;
               owl:equivalentClass [
                  a owl:Class ;
                  owl:oneOf ( :Fido :Bello )
               ] .
            """;

        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLObjectOneOf hasSelf = (OWLObjectOneOf) axiom.getOperandsAsList().get( 1 );

        final String restrictionNodeId = "restrictionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( restrictionNodeId ) );

        final Graph graph = mapper.visit( hasSelf );
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
    public void testOWLDataSomeValuesFrom() {
        final String ontology = """
            :name a owl:DatatypeProperty .
            :Dog a owl:Class ;
               owl:equivalentClass [
                  a owl:Restriction ;
                  owl:onProperty :name ;
                  owl:someValuesFrom xsd:string
               ] .
            """;
        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLDataSomeValuesFrom someValuesFrom = (OWLDataSomeValuesFrom) axiom.getOperandsAsList().get( 1 );

        final String restrictionNode = "restrictionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( restrictionNode ) );

        final Graph graph = mapper.visit( someValuesFrom );
        assertThat( graph.getNode().getClass() ).isEqualTo( Node.ExistentialRestriction.class );
        final Set<GraphElement> remainingElements = graph.getOtherElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "name" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "string" ) );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 2 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndLabel( restrictionNode, "name",
            Edge.Decorated.DATA_PROPERTY_LABEL ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndLabel( restrictionNode, "string",
            Edge.Decorated.DATA_RANGE_LABEL ) );
    }

    @Test
    public void testOWLDataAllValuesFrom() {
        final String ontology = """
            :name a owl:DatatypeProperty .
            :Dog a owl:Class ;
               owl:equivalentClass [
                  a owl:Restriction ;
                  owl:onProperty :name ;
                  owl:allValuesFrom xsd:string
               ] .
            """;
        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLDataAllValuesFrom allValuesFrom = (OWLDataAllValuesFrom) axiom.getOperandsAsList().get( 1 );

        final String restrictionNode = "restrictionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( restrictionNode ) );

        final Graph graph = mapper.visit( allValuesFrom );
        assertThat( graph.getNode().getClass() ).isEqualTo( Node.UniversalRestriction.class );
        final Set<GraphElement> remainingElements = graph.getOtherElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "name" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "string" ) );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 2 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndLabel( restrictionNode, "name",
            Edge.Decorated.DATA_PROPERTY_LABEL ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndLabel( restrictionNode, "string",
            Edge.Decorated.DATA_RANGE_LABEL ) );
    }

    @Test
    public void testOWLDataHasValue() {
        final String ontology = """
            :bar a owl:DatatypeProperty .
            :Foo a owl:Class ;
               owl:equivalentClass [
                  a owl:Restriction ;
                  owl:onProperty :bar ;
                  owl:hasValue "baz"
               ] .
            """;
        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLDataHasValue hasValue = (OWLDataHasValue) axiom.getOperandsAsList().get( 1 );

        final String restrictionNode = "restrictionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( "baz", iri( "baz" ) ) );
        testIdentifierMapper.pushAnonId( new Node.Id( restrictionNode ) );

        final Graph graph = mapper.visit( hasValue );
        assertThat( graph.getNode().getClass() ).isEqualTo( Node.ValueRestriction.class );
        final Set<GraphElement> remainingElements = graph.getOtherElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 3 );
        assertThat( nodes ).anyMatch( isNodeWithId( restrictionNode ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "baz" ) );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 2 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndLabel( restrictionNode, "baz",
            Edge.Decorated.LITERAL_LABEL ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndLabel( restrictionNode, "bar",
            Edge.Decorated.DATA_PROPERTY_LABEL ) );
    }

    @Test
    public void testOWLDataMinCardinality() {
        final String ontology = """
            :hasName a owl:DatatypeProperty .
            :Dog a owl:Class ;
               owl:equivalentClass [
                  a owl:Restriction ;
                  owl:onProperty :hasName ;
                  owl:minCardinality "1"^^xsd:nonNegativeInteger
               ] .
            """;
        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLDataMinCardinality minCardinality = (OWLDataMinCardinality) axiom.getOperandsAsList().get( 1 );

        final String restrictionNodeId = "restrictionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( restrictionNodeId ) );

        final Graph graph = mapper.visit( minCardinality );
        final Node restrictionNode = graph.getNode();
        assertThat( restrictionNode ).isInstanceOf( Node.DataMinimalCardinality.class );
        final Set<GraphElement> remainingElements = graph.getOtherElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 1 );
        assertThat( nodes ).anyMatch( isNodeWithId( "hasName" ) );

        assertThat( ( (Node.DataMinimalCardinality) restrictionNode ).getCardinality() ).isEqualTo( 1 );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 1 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndLabel( restrictionNodeId, "hasName",
            Edge.Decorated.DATA_PROPERTY_LABEL ) );
    }

    @Test
    public void testOWLDataExactCardinality() {
        final String ontology = """
            :hasName a owl:DatatypeProperty .
            :Dog a owl:Class ;
               owl:equivalentClass [
                  a owl:Restriction ;
                  owl:onProperty :hasName ;
                  owl:qualifiedCardinality "1"^^xsd:nonNegativeInteger ;
                  owl:onDataRange xsd:string
               ] .
            """;

        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLDataExactCardinality cardinality = (OWLDataExactCardinality) axiom.getOperandsAsList().get( 1 );

        final String restrictionNodeId = "restrictionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( restrictionNodeId ) );

        final Graph graph = mapper.visit( cardinality );
        final Node restrictionNode = graph.getNode();
        assertThat( restrictionNode ).isInstanceOf( Node.DataExactCardinality.class );
        final Set<GraphElement> remainingElements = graph.getOtherElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 1 );
        assertThat( nodes ).anyMatch( isNodeWithId( "hasName" ) );

        assertThat( ( (Node.DataExactCardinality) restrictionNode ).getCardinality() ).isEqualTo( 1 );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 1 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndLabel( restrictionNodeId, "hasName",
            Edge.Decorated.DATA_PROPERTY_LABEL ) );
    }

    @Test
    public void testOWLDataMaxCardinality() {
        final String ontology = """
            :hasName a owl:DatatypeProperty .
            :Dog a owl:Class ;
               owl:equivalentClass [
                  a owl:Restriction ;
                  owl:onProperty :hasName ;
                  owl:maxCardinality "1"^^xsd:nonNegativeInteger
               ] .
            """;
        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLDataMaxCardinality minCardinality = (OWLDataMaxCardinality) axiom.getOperandsAsList().get( 1 );

        final String restrictionNodeId = "restrictionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( restrictionNodeId ) );

        final Graph graph = mapper.visit( minCardinality );
        final Node restrictionNode = graph.getNode();
        assertThat( restrictionNode ).isInstanceOf( Node.DataMaximalCardinality.class );
        final Set<GraphElement> remainingElements = graph.getOtherElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 1 );
        assertThat( nodes ).anyMatch( isNodeWithId( "hasName" ) );

        assertThat( ( (Node.DataMaximalCardinality) restrictionNode ).getCardinality() ).isEqualTo( 1 );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 1 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndLabel( restrictionNodeId, "hasName",
            Edge.Decorated.DATA_PROPERTY_LABEL ) );
    }

    @Test
    public void testOWLClass() {
        final OWLDeclarationAxiom axiom = getAxiom( ":Foo a owl:Class ." );
        final OWLClass class_ = (OWLClass) axiom.getEntity();

        final Graph graph = mapper.visit( class_ );
        assertThat( graph.getNode().getClass() ).isEqualTo( Node.Class.class );
        assertThat( graph.getOtherElements() ).isEmpty();
    }
}
