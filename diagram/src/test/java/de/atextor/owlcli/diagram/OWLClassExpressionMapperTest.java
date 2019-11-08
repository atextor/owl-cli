package de.atextor.owlcli.diagram;

import de.atextor.owlcli.diagram.graph.DecoratedEdge;
import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.NodeType;
import de.atextor.owlcli.diagram.mappers.OWLClassExpressionMapper;
import de.atextor.owlcli.diagram.mappers.Result;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
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
        final String ontology = "" +
            ":Dog a owl:Class ." +
            ":CanTalk a owl:Class ." +
            ":TalkingDog a owl:Class ;" +
            "   owl:equivalentClass [" +
            "      a owl:Class ;" +
            "      owl:intersectionOf ( :Dog :CanTalk )" +
            "   ] .";
        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLObjectIntersectionOf intersection = (OWLObjectIntersectionOf) axiom.getOperandsAsList().get( 1 );

        final String intersectionId = "intersectionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( intersectionId ) );

        final Result result = mapper.visit( intersection );
        assertThat( result.getNode().getClass() ).isEqualTo( NodeType.Intersection.class );
        final Set<GraphElement> remainingElements = result.getRemainingElements().collect( Collectors.toSet() );
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
        final String ontology = "" +
            ":Dog a owl:Class ." +
            ":CanTalk a owl:Class ." +
            ":TalkingDog a owl:Class ;" +
            "   owl:equivalentClass [" +
            "      a owl:Class ;" +
            "      owl:unionOf ( :Dog :CanTalk )" +
            "   ] .";
        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLObjectUnionOf union = (OWLObjectUnionOf) axiom.getOperandsAsList().get( 1 );

        final String unionId = "unionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( unionId ) );

        final Result result = mapper.visit( union );
        assertThat( result.getNode().getClass() ).isEqualTo( NodeType.Union.class );
        final Set<GraphElement> remainingElements = result.getRemainingElements().collect( Collectors.toSet() );
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
        final String ontology = "" +
            ":Dog a owl:Class ." +
            ":TalkingDog a owl:Class ;" +
            "   owl:equivalentClass [" +
            "      a owl:Class ;" +
            "      owl:complementOf :Dog" +
            "   ] .";
        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLObjectComplementOf union = (OWLObjectComplementOf) axiom.getOperandsAsList().get( 1 );

        final String complementId = "complementNode";
        testIdentifierMapper.pushAnonId( new Node.Id( complementId ) );

        final Result result = mapper.visit( union );
        assertThat( result.getNode().getClass() ).isEqualTo( NodeType.Complement.class );
        final Set<GraphElement> remainingElements = result.getRemainingElements().collect( Collectors.toSet() );
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
        final String ontology = "" +
            ":Dog a owl:Class ." +
            ":hasDog a owl:ObjectProperty ." +
            ":DogOwner a owl:Class ;" +
            "   owl:equivalentClass [" +
            "      a owl:Restriction ;" +
            "      owl:onProperty :hasDog ;" +
            "      owl:someValuesFrom :Dog" +
            "   ] .";
        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLObjectSomeValuesFrom someValuesFrom = (OWLObjectSomeValuesFrom) axiom.getOperandsAsList().get( 1 );

        final String restrictionNode = "restrictionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( restrictionNode ) );

        final Result result = mapper.visit( someValuesFrom );
        assertThat( result.getNode().getClass() ).isEqualTo( NodeType.ExistentialRestriction.class );
        final Set<GraphElement> remainingElements = result.getRemainingElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "hasDog" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "Dog" ) );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 2 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndDecoration( restrictionNode, "Dog", DecoratedEdge.CLASS ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndDecoration( restrictionNode, "hasDog",
            DecoratedEdge.ABSTRACT_ROLE ) );
    }

    @Test
    public void testOWLObjectAllValuesFrom() {
        final String ontology = "" +
            ":Dog a owl:Class ." +
            ":hasDog a owl:ObjectProperty ." +
            ":DogOwner a owl:Class ;" +
            "   owl:equivalentClass [" +
            "      a owl:Restriction ;" +
            "      owl:onProperty :hasDog ;" +
            "      owl:allValuesFrom :Dog" +
            "   ] .";
        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLObjectAllValuesFrom allValuesFrom = (OWLObjectAllValuesFrom) axiom.getOperandsAsList().get( 1 );

        final String restrictionNode = "restrictionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( restrictionNode ) );

        final Result result = mapper.visit( allValuesFrom );
        assertThat( result.getNode().getClass() ).isEqualTo( NodeType.UniversalRestriction.class );
        final Set<GraphElement> remainingElements = result.getRemainingElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "hasDog" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "Dog" ) );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 2 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndDecoration( restrictionNode, "Dog", DecoratedEdge.CLASS ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndDecoration( restrictionNode, "hasDog",
            DecoratedEdge.ABSTRACT_ROLE ) );
    }

    @Test
    public void testOWLObjectHasValue() {
        final String ontology = "" +
            ":bar a owl:ObjectProperty ." +
            ":baz a owl:NamedIndividual ." +
            ":Foo a owl:Class ;" +
            "   owl:equivalentClass [" +
            "      a owl:Restriction ;" +
            "      owl:onProperty :bar ;" +
            "      owl:hasValue :baz" +
            "   ] .";
        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLObjectHasValue hasValue = (OWLObjectHasValue) axiom.getOperandsAsList().get( 1 );

        final String restrictionNode = "restrictionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( restrictionNode ) );

        final Result result = mapper.visit( hasValue );
        assertThat( result.getNode().getClass() ).isEqualTo( NodeType.ValueRestriction.class );
        final Set<GraphElement> remainingElements = result.getRemainingElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 3 );
        assertThat( nodes ).anyMatch( isNodeWithId( restrictionNode ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "baz" ) );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 2 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndDecoration( restrictionNode, "baz",
            DecoratedEdge.INDIVIDUAL ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndDecoration( restrictionNode, "bar",
            DecoratedEdge.ABSTRACT_ROLE ) );
    }

    @Test
    public void testOWLObjectUnqualifiedMinCardinality() {
        final String ontology = "" +
            ":hasDog a owl:ObjectProperty ." +
            ":DogOwner a owl:Class ;" +
            "   owl:equivalentClass [" +
            "      a owl:Restriction ;" +
            "      owl:onProperty :hasDog ;" +
            "      owl:minCardinality \"1\"^^xsd:nonNegativeInteger" +
            "   ] .";
        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );
        final OWLObjectMinCardinality minCardinality = (OWLObjectMinCardinality) axiom.getOperandsAsList().get( 1 );

        final String restrictionNodeId = "restrictionNode";
        testIdentifierMapper.pushAnonId( new Node.Id( restrictionNodeId ) );

        final Result result = mapper.visit( minCardinality );
        final Node restrictionNode = result.getNode();
        assertThat( restrictionNode ).isInstanceOf( NodeType.AbstractMinimalCardinality.class );
        final Set<GraphElement> remainingElements = result.getRemainingElements().collect( Collectors.toSet() );
        assertThat( remainingElements ).isNotEmpty();

        final List<Node> nodes = nodes( remainingElements );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( restrictionNodeId ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "hasDog" ) );

        assertThat( ( (NodeType.AbstractMinimalCardinality) restrictionNode ).getCardinality() ).isEqualTo( 1 );

        final List<Edge> edges = edges( remainingElements );
        assertThat( edges ).hasSize( 1 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndToAndDecoration( restrictionNodeId, "hasDog",
            DecoratedEdge.ABSTRACT_ROLE ) );
    }

    @Test
    public void testOWLObjectQualifiedMinCardinality() {

    }

    @Test
    public void testOWLObjectExactCardinality() {

    }

    @Test
    public void testOWLObjectMaxCardinality() {

    }

    @Test
    public void testOWLObjectHasSelf() {

    }

    @Test
    public void testOWLObjectOneOf() {

    }

    @Test
    public void testOWLDataSomeValuesFrom() {

    }

    @Test
    public void testOWLDataAllValuesFrom() {

    }

    @Test
    public void testOWLDataHasValue() {

    }

    @Test
    public void testOWLDataMinCardinality() {

    }

    @Test
    public void testOWLDataExactCardinality() {

    }

    @Test
    public void testOWLDataMaxCardinality() {

    }

    @Test
    public void testOWLClass() {
        final OWLDeclarationAxiom axiom = getAxiom( ":Foo a owl:Class ." );
        final OWLClass class_ = (OWLClass) axiom.getEntity();

        final Result result = mapper.visit( class_ );
        assertThat( result.getNode().getClass() ).isEqualTo( NodeType.Class.class );
        assertThat( result.getRemainingElements() ).isEmpty();
    }
}
