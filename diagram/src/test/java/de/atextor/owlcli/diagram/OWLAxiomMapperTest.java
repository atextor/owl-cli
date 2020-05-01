package de.atextor.owlcli.diagram;

import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.mappers.IdentifierMapper;
import de.atextor.owlcli.diagram.mappers.MappingConfiguration;
import de.atextor.owlcli.diagram.mappers.OWLAxiomMapper;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class OWLAxiomMapperTest extends MapperTestBase {
    private final MappingConfiguration mappingConfiguration = createTestMappingConfiguration();
    private final OWLAxiomMapper mapper = new OWLAxiomMapper( mappingConfiguration );

    @Test
    public void testOWLSubClassOfAxiom() {
        final OWLSubClassOfAxiom axiom = getAxiom( ":Foo rdfs:subClassOf :Bar ." );
        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();
        assertThat( result ).hasSize( 3 );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "Foo" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "Bar" ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 1 );

        final Edge theEdge = edges.get( 0 );
        assertThat( theEdge ).matches( isEdgeWithFromAndTo( "Foo", "Bar" ) );
        assertThat( theEdge.getType() ).isEqualTo( Edge.Type.HOLLOW_ARROW );
    }

    @Test
    public void testOWLNegativeObjectPropertyAssertionAxiom() {
        final String ontology = """
            :foo a owl:NamedIndividual .
            :bar a owl:NamedIndividual .
            :property a owl:ObjectProperty .
            [
               a owl:NegativeObjectPropertyAssertion ;
               owl:sourceIndividual :foo ;
               owl:assertionProperty :property ;
               owl:targetIndividual :bar
            ] .
            """;
        final OWLNegativeObjectPropertyAssertionAxiom axiom = getAxiom( ontology,
            AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION );

        final String complementId = "complementNode";
        testIdentifierMapper.pushAnonId( new Node.Id( complementId ) );

        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();
        assertThat( result ).hasSize( 7 );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 4 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "property" ) );
        assertThat( nodes ).anyMatch( isComplement );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 3 );

        final Edge fooToComplement =
            edges.stream().filter( isEdgeWithFromAndTo( "foo", complementId ) ).findAny().get();
        final Edge complementToBar =
            edges.stream().filter( isEdgeWithFromAndTo( complementId, "bar" ) ).findAny().get();
        final Edge complementToProp =
            edges.stream().filter( isEdgeWithFromAndTo( complementId, "property" ) ).findAny().get();
        assertThat( fooToComplement.getType() ).isEqualTo( Edge.Type.NO_ARROW );
        assertThat( complementToBar.getType() ).isEqualTo( Edge.Type.DEFAULT_ARROW );
        assertThat( complementToProp.getType() ).isEqualTo( Edge.Type.DASHED_ARROW );
    }

    @Test
    public void testOWLAsymmetricObjectPropertyAxiom() {
        final String ontology = """
            :foo a owl:ObjectProperty, owl:AsymmetricProperty .
            """;
        final OWLAsymmetricObjectPropertyAxiom axiom = getAxiom( ontology, AxiomType.ASYMMETRIC_OBJECT_PROPERTY );

        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();
        assertThat( result ).hasSize( 3 );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( node -> node.view( Node.PropertyMarker.class ).map( propertyMarker ->
            propertyMarker.getKind().contains( Node.PropertyMarker.Kind.ASYMMETRIC ) ).findFirst()
            .orElse( false ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 1 );

        assertThat( edges ).anyMatch( isEdgeWithFrom( "foo" ).and( hasDashedArrow ) );
    }

    @Test
    public void testOWLReflexiveObjectPropertyAxiom() {
        final String ontology = """
            :foo a owl:ObjectProperty, owl:ReflexiveProperty .
            """;
        final OWLReflexiveObjectPropertyAxiom axiom = getAxiom( ontology, AxiomType.REFLEXIVE_OBJECT_PROPERTY );

        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();
        assertThat( result ).hasSize( 3 );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( node -> node.view( Node.PropertyMarker.class ).map( propertyMarker ->
            propertyMarker.getKind().contains( Node.PropertyMarker.Kind.REFLEXIVE ) ).findFirst()
            .orElse( false ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 1 );

        assertThat( edges ).anyMatch( isEdgeWithFrom( "foo" ).and( hasDashedArrow ) );
    }

    @Test
    public void testOWLDisjointClassesAxiom() {
        final String ontology = """
            :foo a owl:Class .
            :bar a owl:Class ;
               owl:disjointWith :foo .
            """;

        final OWLDisjointClassesAxiom axiom = getAxiom( ontology, AxiomType.DISJOINT_CLASSES );

        final String disjointnessNodeId = "disjointness";
        testIdentifierMapper.pushAnonId( new Node.Id( disjointnessNodeId ) );
        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 3 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( disjointnessNodeId ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 2 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( disjointnessNodeId, "foo" ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( disjointnessNodeId, "bar" ) );
    }

    @Test
    public void testOWLDataPropertyDomainAxiom() {
        final String ontology = """
            :foo a owl:Class .
            :bar a owl:DatatypeProperty ;
               rdfs:domain :foo .
            """;
        final OWLDataPropertyDomainAxiom axiom = getAxiom( ontology, AxiomType.DATA_PROPERTY_DOMAIN );

        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 1 );

        final Edge propertyToDomain = edges.iterator().next();
        assertThat( propertyToDomain.getType() ).isEqualTo( Edge.Type.DEFAULT_ARROW );
        assertThat( propertyToDomain.getClass() ).isEqualTo( Edge.Decorated.class );
        assertThat( ( (Edge.Decorated) propertyToDomain ).getLabel() ).isEqualTo( Edge.Decorated.DOMAIN_LABEL );
    }

    @Test
    public void testOWLObjectPropertyDomainAxiom() {
        final String ontology = """
            :foo a owl:Class .
            :bar a owl:ObjectProperty ;
               rdfs:domain :foo .
            """;
        final OWLObjectPropertyDomainAxiom axiom = getAxiom( ontology, AxiomType.OBJECT_PROPERTY_DOMAIN );

        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 1 );

        final Edge propertyToDomain = edges.iterator().next();
        assertThat( propertyToDomain.getType() ).isEqualTo( Edge.Type.DEFAULT_ARROW );
        assertThat( propertyToDomain.getClass() ).isEqualTo( Edge.Decorated.class );
        assertThat( ( (Edge.Decorated) propertyToDomain ).getLabel() ).isEqualTo( Edge.Decorated.DOMAIN_LABEL );
    }

    @Test
    public void testOWLEquivalentObjectPropertiesAxiomNAry() {
        // The axiom variant with >2 arguments can not be expressed in Turtle,
        // therefore we use Functional Syntax here.
        final String ontology = """
            Declaration(ObjectProperty(:bar))
            Declaration(ObjectProperty(:baz))
            Declaration(ObjectProperty(:foo))
            EquivalentObjectProperties(:foo :bar :baz)
            """;
        final OWLEquivalentObjectPropertiesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_OBJECT_PROPERTIES );

        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();
        assertEquivalentResult( result, iri( "foo" ), iri( "bar" ), iri( "baz" ) );
    }

    @Test
    public void testOWLEquivalentObjectPropertiesAxiom() {
        final String ontologyContent = """
            :foo a owl:ObjectProperty ;
               owl:equivalentProperty :bar .
            :bar a owl:ObjectProperty ;
               owl:equivalentProperty :baz .
            :baz a owl:ObjectProperty ;
               owl:equivalentProperty :foo .
            """;
        final OWLOntology ontology = createOntology( ontologyContent );
        final Set<GraphElement> result = ontology.axioms()
            .filter( axiom -> axiom.isOfType( AxiomType.EQUIVALENT_OBJECT_PROPERTIES ) )
            .map( axiom -> (OWLEquivalentObjectPropertiesAxiom) axiom )
            .flatMap( element -> mapper.visit( element ).toStream() )
            .collect( Collectors.toSet() );

        assertEquivalentResult( result, iri( "foo" ), iri( "bar" ), iri( "baz" ) );
    }

    @Test
    public void testOWLNegativeDataPropertyAssertionAxiom() {
        final String ontology = """
            :foo a owl:NamedIndividual .
            :property a owl:DatatypeProperty .
            [
               a owl:NegativePropertyAssertion ;
               owl:sourceIndividual :foo ;
               owl:assertionProperty :property ;
               owl:targetValue "Fido" ;
            ] .
            """;
        final OWLNegativeDataPropertyAssertionAxiom axiom = getAxiom( ontology,
            AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION );

        final String valueNodeId = "Fido";
        testIdentifierMapper.pushAnonId( new Node.Id( valueNodeId ) );

        final String complementId = "complementNode";
        testIdentifierMapper.pushAnonId( new Node.Id( complementId ) );

        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();
        assertThat( result ).hasSize( 7 );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 4 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "property" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( valueNodeId ) );
        assertThat( nodes ).anyMatch( isComplement );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 3 );

        final Edge fooToComplement =
            edges.stream().filter( isEdgeWithFromAndTo( "foo", complementId ) ).findAny().get();
        final Edge complementToValue =
            edges.stream().filter( isEdgeWithFromAndTo( complementId, valueNodeId ) ).findAny().get();
        final Edge complementToProp =
            edges.stream().filter( isEdgeWithFromAndTo( complementId, "property" ) ).findAny().get();
        assertThat( fooToComplement.getType() ).isEqualTo( Edge.Type.NO_ARROW );
        assertThat( complementToValue.getType() ).isEqualTo( Edge.Type.DEFAULT_ARROW );
        assertThat( complementToProp.getType() ).isEqualTo( Edge.Type.DASHED_ARROW );
    }

    @Test
    public void testOWLDifferentIndividualsAxiom() {
        final String ontology = """
            :foo a owl:NamedIndividual .
            :bar a owl:NamedIndividual .
            [
               a owl:AllDifferent ;
               owl:distinctMembers ( :foo :bar )
            ] .
            """;
        final OWLDifferentIndividualsAxiom axiom = getAxiom( ontology, AxiomType.DIFFERENT_INDIVIDUALS );

        final String inequalityId = "inequality";
        testIdentifierMapper.pushAnonId( new Node.Id( inequalityId ) );

        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 3 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );
        assertThat( nodes ).anyMatch( node -> node.is( Node.Inequality.class ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 2 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( inequalityId, "foo" ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( inequalityId, "bar" ) );
    }

    @Test
    public void testOWLDisjointDataPropertiesAxiom() {
        final String ontology = """
            :foo a owl:DatatypeProperty .
            :bar a owl:DatatypeProperty ;
               owl:propertyDisjointWith :foo .
            """;

        final OWLDisjointDataPropertiesAxiom axiom = getAxiom( ontology, AxiomType.DISJOINT_DATA_PROPERTIES );

        final String disjointnessNodeId = "disjointness";
        testIdentifierMapper.pushAnonId( new Node.Id( disjointnessNodeId ) );
        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 3 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( disjointnessNodeId ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 2 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( disjointnessNodeId, "foo" ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( disjointnessNodeId, "bar" ) );
    }

    @Test
    public void testOWLDisjointObjectPropertiesAxiom() {
        final String ontology = """
            :foo a owl:ObjectProperty .
            :bar a owl:ObjectProperty ;
               owl:propertyDisjointWith :foo .
            """;

        final OWLDisjointObjectPropertiesAxiom axiom = getAxiom( ontology, AxiomType.DISJOINT_OBJECT_PROPERTIES );

        final String disjointnessNodeId = "disjointness";
        testIdentifierMapper.pushAnonId( new Node.Id( disjointnessNodeId ) );
        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 3 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( disjointnessNodeId ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 2 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( disjointnessNodeId, "foo" ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( disjointnessNodeId, "bar" ) );
    }

    @Test
    public void testOWLObjectPropertyRangeAxiom() {
        final String ontology = """
            :foo a owl:Class .
            :bar a owl:ObjectProperty ;
               rdfs:range :foo .
            """;
        final OWLObjectPropertyRangeAxiom axiom = getAxiom( ontology, AxiomType.OBJECT_PROPERTY_RANGE );

        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 1 );

        final Edge propertyToRange = edges.iterator().next();
        assertThat( propertyToRange.getType() ).isEqualTo( Edge.Type.DEFAULT_ARROW );
        assertThat( propertyToRange.getClass() ).isEqualTo( Edge.Decorated.class );
        assertThat( ( (Edge.Decorated) propertyToRange ).getLabel() ).isEqualTo( Edge.Decorated.RANGE_LABEL );
    }

    @Test
    public void testOWLObjectPropertyAssertionAxiom() {
        final String ontology = """
            :foo a owl:NamedIndividual .
            :bar a owl:NamedIndividual .
            :property a owl:ObjectProperty .
            :foo :property :bar .
            """;
        final OWLObjectPropertyAssertionAxiom axiom = getAxiom( ontology, AxiomType.OBJECT_PROPERTY_ASSERTION );

        final String invisibleId = "invisibleNode";
        testIdentifierMapper.pushAnonId( new Node.Id( invisibleId ) );

        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();
        assertThat( result ).hasSize( 7 );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 4 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "property" ) );
        assertThat( nodes ).anyMatch( isInvisible );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 3 );

        final Edge fooToInvis = edges.stream().filter( isEdgeWithFromAndTo( "foo", invisibleId ) ).findAny().get();
        final Edge invisToBar = edges.stream().filter( isEdgeWithFromAndTo( invisibleId, "bar" ) ).findAny().get();
        final Edge invisToProp =
            edges.stream().filter( isEdgeWithFromAndTo( invisibleId, "property" ) ).findAny().get();
        assertThat( fooToInvis.getType() ).isEqualTo( Edge.Type.NO_ARROW );
        assertThat( invisToBar.getType() ).isEqualTo( Edge.Type.DEFAULT_ARROW );
        assertThat( invisToProp.getType() ).isEqualTo( Edge.Type.DASHED_ARROW );
    }

    @Test
    public void testOWLFunctionalObjectPropertyAxiom() {
        final String ontology = """
            :foo a owl:ObjectProperty, owl:FunctionalProperty .
            """;
        final OWLFunctionalObjectPropertyAxiom axiom = getAxiom( ontology, AxiomType.FUNCTIONAL_OBJECT_PROPERTY );

        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();
        assertThat( result ).hasSize( 3 );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( node -> node.view( Node.PropertyMarker.class ).map( propertyMarker ->
            propertyMarker.getKind().contains( Node.PropertyMarker.Kind.FUNCTIONAL ) ).findFirst()
            .orElse( false ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 1 );

        assertThat( edges ).anyMatch( isEdgeWithFrom( "foo" ).and( hasDashedArrow ) );
    }

    @Test
    public void testOWLSubObjectPropertyOfAxiom() {
        final String ontology = """
            :foo a owl:ObjectProperty .
            :bar a owl:ObjectProperty .
            :foo rdfs:subPropertyOf :bar .
            """;
        final OWLSubObjectPropertyOfAxiom axiom = getAxiom( ontology, AxiomType.SUB_OBJECT_PROPERTY );

        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();
        assertThat( result ).hasSize( 3 );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 1 );

        final Edge theEdge = edges.get( 0 );
        assertThat( theEdge ).matches( isEdgeWithFromAndTo( "foo", "bar" ) );
        assertThat( theEdge.getType() ).isEqualTo( Edge.Type.HOLLOW_ARROW );
    }

    @Test
    public void testOWLDisjointUnionAxiom() {
        final String ontology = """
            :foo a owl:Class .
            :bar a owl:Class .
            :baz a owl:Class ;
               owl:disjointUnionOf ( :foo :bar ) .
            """;

        final OWLDisjointUnionAxiom axiom = getAxiom( ontology, AxiomType.DISJOINT_UNION );

        final String disjointUnionNodeId = "disjointUnion";
        testIdentifierMapper.pushAnonId( new Node.Id( disjointUnionNodeId ) );
        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 4 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "baz" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( disjointUnionNodeId ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 3 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( "baz", disjointUnionNodeId ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( disjointUnionNodeId, "foo" ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( disjointUnionNodeId, "bar" ) );
    }

    @Test
    public void testOWLSymmetricObjectPropertyAxiom() {
        final String ontology = """
            :foo a owl:ObjectProperty, owl:SymmetricProperty .
            """;
        final OWLSymmetricObjectPropertyAxiom axiom = getAxiom( ontology, AxiomType.SYMMETRIC_OBJECT_PROPERTY );

        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();
        assertThat( result ).hasSize( 3 );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( node -> node.view( Node.PropertyMarker.class ).map( propertyMarker ->
            propertyMarker.getKind().contains( Node.PropertyMarker.Kind.SYMMETRIC ) ).findFirst()
            .orElse( false ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 1 );

        assertThat( edges ).anyMatch( isEdgeWithFrom( "foo" ).and( hasDashedArrow ) );
    }

    @Test
    public void testOWLDataPropertyRangeAxiom() {
        final String ontology = """
            :foo a owl:DatatypeProperty ;
               rdfs:range xsd:string .
            """;
        final OWLDataPropertyRangeAxiom axiom = getAxiom( ontology, AxiomType.DATA_PROPERTY_RANGE );

        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "string" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 1 );

        final Edge propertyToRange = edges.iterator().next();
        assertThat( propertyToRange.getType() ).isEqualTo( Edge.Type.DEFAULT_ARROW );
        assertThat( propertyToRange.getClass() ).isEqualTo( Edge.Decorated.class );
        assertThat( ( (Edge.Decorated) propertyToRange ).getLabel() ).isEqualTo( Edge.Decorated.RANGE_LABEL );
    }

    @Test
    public void testOWLFunctionalDataPropertyAxiom() {
        final String ontology = """
            :foo a owl:DatatypeProperty, owl:FunctionalProperty .
            """;
        final OWLFunctionalDataPropertyAxiom axiom = getAxiom( ontology, AxiomType.FUNCTIONAL_DATA_PROPERTY );

        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();
        assertThat( result ).hasSize( 3 );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( node -> node.view( Node.PropertyMarker.class ).map( propertyMarker ->
            propertyMarker.getKind().contains( Node.PropertyMarker.Kind.FUNCTIONAL ) ).findFirst()
            .orElse( false ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 1 );

        assertThat( edges ).anyMatch( isEdgeWithFrom( "foo" ).and( hasDashedArrow ) );
    }

    @Test
    public void testOWLEquivalentDataPropertiesAxiomNAry() {
        // The axiom variant with >2 arguments can not be expressed in Turtle,
        // therefore we use Functional Syntax here.
        final String ontology = """
            Declaration(DataProperty(:bar))
            Declaration(DataProperty(:baz))
            Declaration(DataProperty(:foo))
            EquivalentDataProperties(:foo :bar :baz)
            """;
        final OWLEquivalentDataPropertiesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_DATA_PROPERTIES );

        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();
        assertEquivalentResult( result, iri( "foo" ), iri( "bar" ), iri( "baz" ) );
    }

    @Test
    public void testOWLEquivalentDataPropertiesAxiom() {
        final String ontologyContent = """
            :foo a owl:DatatypeProperty ;
               owl:equivalentProperty :bar .
            :bar a owl:DatatypeProperty ;
               owl:equivalentProperty :baz .
            :baz a owl:DatatypeProperty ;
               owl:equivalentProperty :foo .
            """;
        final OWLOntology ontology = createOntology( ontologyContent );
        final Set<GraphElement> result = ontology.axioms()
            .filter( axiom -> axiom.isOfType( AxiomType.EQUIVALENT_DATA_PROPERTIES ) )
            .map( axiom -> (OWLEquivalentDataPropertiesAxiom) axiom )
            .flatMap( element -> mapper.visit( element ).toStream() )
            .collect( Collectors.toSet() );

        assertEquivalentResult( result, iri( "foo" ), iri( "bar" ), iri( "baz" ) );
    }

    @Test
    public void testOWLClassAssertionAxiom() {
        final OWLClassAssertionAxiom axiom = getAxiom( ":Foo a owl:Thing ." );
        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();
        assertThat( result ).hasSize( 3 );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "Foo" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "Thing" ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 1 );

        final Edge theEdge = edges.get( 0 );
        assertThat( theEdge ).matches( isEdgeWithFromAndTo( "Foo", "Thing" ) );
        assertThat( theEdge.getType() ).isEqualTo( Edge.Type.DEFAULT_ARROW );
    }

    @Test
    public void testOWLEquivalentClassesAxiomNAry() {
        // The axiom variant with >2 arguments can not be expressed in Turtle,
        // therefore we use Functional Syntax here.
        final String ontology = """
            Declaration(Class(:bar))
            Declaration(Class(:baz))
            Declaration(Class(:foo))
            EquivalentClasses(:foo :bar :baz)
            """;
        final OWLEquivalentClassesAxiom axiom = getAxiom( ontology, AxiomType.EQUIVALENT_CLASSES );

        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();
        assertEquivalentResult( result, iri( "foo" ), iri( "bar" ), iri( "baz" ) );
    }

    @Test
    public void testOWLEquivalentClassesAxiom() {
        final String ontologyContent = """
            :foo a owl:Class ;
               owl:equivalentClass :bar .
            :bar a owl:Class ;
               owl:equivalentClass :baz .
            :baz a owl:Class ;
               owl:equivalentClass :foo .
            """;
        final OWLOntology ontology = createOntology( ontologyContent );
        final Set<GraphElement> result = ontology.axioms()
            .filter( axiom -> axiom.isOfType( AxiomType.EQUIVALENT_CLASSES ) )
            .map( axiom -> (OWLEquivalentClassesAxiom) axiom )
            .flatMap( element -> mapper.visit( element ).toStream() )
            .collect( Collectors.toSet() );

        assertEquivalentResult( result, iri( "foo" ), iri( "bar" ), iri( "baz" ) );
    }

    @Test
    public void testOWLDataPropertyAssertionAxiom() {
        final String ontology = """
            :foo a owl:DatatypeProperty .
            :bar a owl:NamedIndividual .
            :bar :foo "hello" .
            """;
        final OWLDataPropertyAssertionAxiom axiom = getAxiom( ontology, AxiomType.DATA_PROPERTY_ASSERTION );

        final String literalNodeId = "hello";
        testIdentifierMapper.pushAnonId( new Node.Id( literalNodeId ) );

        final String helperNodeId = "helper";
        testIdentifierMapper.pushAnonId( new Node.Id( helperNodeId ) );

        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();
        assertThat( result ).isNotEmpty();

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 4 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "hello" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "helper" ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 3 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( "bar", "helper" ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( "helper", "hello" ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( "helper", "foo" ) );
    }

    @Test
    public void testOWLTransitiveObjectPropertyAxiom() {
        final String ontology = """
            :foo a owl:ObjectProperty, owl:TransitiveProperty .
            """;
        final OWLTransitiveObjectPropertyAxiom axiom = getAxiom( ontology, AxiomType.TRANSITIVE_OBJECT_PROPERTY );

        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();
        assertThat( result ).hasSize( 3 );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( node -> node.view( Node.PropertyMarker.class ).map( propertyMarker ->
            propertyMarker.getKind().contains( Node.PropertyMarker.Kind.TRANSITIVE ) ).findFirst()
            .orElse( false ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 1 );

        assertThat( edges ).anyMatch( isEdgeWithFrom( "foo" ).and( hasDashedArrow ) );
    }

    @Test
    public void testOWLIrreflexiveObjectPropertyAxiom() {
        final String ontology = """
            :foo a owl:ObjectProperty, owl:IrreflexiveProperty .
            """;
        final OWLIrreflexiveObjectPropertyAxiom axiom = getAxiom( ontology, AxiomType.IRREFLEXIVE_OBJECT_PROPERTY );

        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();
        assertThat( result ).hasSize( 3 );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( node -> node.view( Node.PropertyMarker.class ).map( propertyMarker ->
            propertyMarker.getKind().contains( Node.PropertyMarker.Kind.IRREFLEXIVE ) ).findFirst()
            .orElse( false ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 1 );

        assertThat( edges ).anyMatch( isEdgeWithFrom( "foo" ).and( hasDashedArrow ) );
    }

    @Test
    public void testOWLSubDataPropertyOfAxiom() {
        final String ontology = """
            :foo a owl:DatatypeProperty .
            :bar a owl:DatatypeProperty .
            :foo rdfs:subPropertyOf :bar .
            """;
        final OWLSubDataPropertyOfAxiom axiom = getAxiom( ontology, AxiomType.SUB_DATA_PROPERTY );
        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();
        assertThat( result ).hasSize( 3 );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 1 );

        final Edge theEdge = edges.get( 0 );
        assertThat( theEdge ).matches( isEdgeWithFromAndTo( "foo", "bar" ) );
        assertThat( theEdge.getType() ).isEqualTo( Edge.Type.HOLLOW_ARROW );
    }

    @Test
    public void testOWLInverseFunctionalObjectPropertyAxiom() {
        final String ontology = """
            :foo a owl:ObjectProperty, owl:InverseFunctionalProperty .
            """;
        final OWLInverseFunctionalObjectPropertyAxiom axiom = getAxiom( ontology,
            AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY );

        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();
        assertThat( result ).hasSize( 3 );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( node -> node.view( Node.PropertyMarker.class ).map( propertyMarker ->
            propertyMarker.getKind().contains( Node.PropertyMarker.Kind.INVERSE_FUNCTIONAL ) ).findFirst()
            .orElse( false ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 1 );

        assertThat( edges ).anyMatch( isEdgeWithFrom( "foo" ).and( hasDashedArrow ) );
    }

    @Test
    public void testOWLSameIndividualAxiom() {
        final String ontology = """
            :foo a owl:NamedIndividual .
            :bar a owl:NamedIndividual ;
               owl:sameAs :foo .
            """;
        final OWLSameIndividualAxiom axiom = getAxiom( ontology, AxiomType.SAME_INDIVIDUAL );

        final String equalityId = "equality";
        testIdentifierMapper.pushAnonId( new Node.Id( equalityId ) );

        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 3 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );
        assertThat( nodes ).anyMatch( node -> node.is( Node.Equality.class ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 2 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( equalityId, "foo" ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( equalityId, "bar" ) );
    }

    @Test
    public void testOWLSubPropertyChainOfAxiom() {
        final String ontology = """
            :bar a owl:ObjectProperty .
            :baz a owl:ObjectProperty .
            :foo a owl:ObjectProperty ;
               owl:propertyChainAxiom ( :bar :baz ) .
            """;
        final OWLSubPropertyChainOfAxiom axiom = getAxiom( ontology, AxiomType.SUB_PROPERTY_CHAIN_OF );

        final String chainId = "chainId";
        testIdentifierMapper.pushAnonId( new Node.Id( chainId ) );

        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 4 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "baz" ) );
        assertThat( nodes ).anyMatch( node -> node.is( Node.PropertyChain.class )
            && node.getId().getId().equals( chainId ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 3 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( "foo", chainId ).and( hasHollowArrow ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( chainId, "bar" ).and( hasDefaultArrow ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( chainId, "baz" ).and( hasDefaultArrow ) );
    }

    @Test
    public void testOWLInverseObjectPropertiesAxiom() {
        final String ontology = """
            :foo a owl:ObjectProperty .
            :bar a owl:ObjectProperty ;
               owl:inverseOf :foo .
            """;
        final OWLInverseObjectPropertiesAxiom axiom = getAxiom( ontology, AxiomType.INVERSE_OBJECT_PROPERTIES );

        final String inverseId = "inverse";
        testIdentifierMapper.pushAnonId( new Node.Id( inverseId ) );

        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 3 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );
        assertThat( nodes ).anyMatch( node -> node.is( Node.Inverse.class ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 2 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( inverseId, "foo" ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( inverseId, "bar" ) );
    }

    @Test
    public void testOWLHasKeyAxiom() {
    }

    @Test
    public void testOWLDeclarationAxiom() {
        final OWLDeclarationAxiom axiom = getAxiom( ":Foo a owl:Class ." );
        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();
        assertThat( result ).hasSize( 1 );

        final Node theNode = nodes( result ).get( 0 );
        assertThat( theNode ).matches( isNodeWithId( "Foo" ) );
    }

    @Test
    public void testOWLDatatypeDefinitionAxiom() {
    }

    @Test
    public void testOWLSubAnnotationPropertyOfAxiom() {
        final String ontology = """
            :foo a owl:AnnotationProperty .
            :bar a owl:AnnotationProperty ;
                 rdfs:subPropertyOf :foo .
            """;
        final OWLSubAnnotationPropertyOfAxiom axiom = getAxiom( ontology, AxiomType.SUB_ANNOTATION_PROPERTY_OF );
        final Set<GraphElement> result = mapper.visit( axiom ).getElementSet();
        assertThat( result ).isNotEmpty();

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 1 );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( "bar", "foo" ) );
    }

    @Test
    public void testOWLAnnotationPropertyRangeAxiom() {
    }

    @Test
    public void testSWRLRule() {
    }

    private void assertEquivalentResult( final Set<GraphElement> result, final IRI fooIri, final IRI barIri,
                                         final IRI bazIri ) {
        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 3 );

        final IdentifierMapper identifierMapper = mappingConfiguration.getIdentifierMapper();
        final Node.Id foo = identifierMapper.getIdForIri( fooIri );
        final Node.Id bar = identifierMapper.getIdForIri( barIri );
        final Node.Id baz = identifierMapper.getIdForIri( bazIri );

        assertThat( nodes ).anyMatch( isNodeWithId( foo ) );
        assertThat( nodes ).anyMatch( isNodeWithId( bar ) );
        assertThat( nodes ).anyMatch( isNodeWithId( baz ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 3 );

        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( bar, foo ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( baz, foo ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( bar, baz ) );

        assertThat( edges ).noneMatch( isEdgeWithFromAndTo( foo, bar ) );
        assertThat( edges ).noneMatch( isEdgeWithFromAndTo( foo, baz ) );
        assertThat( edges ).noneMatch( isEdgeWithFromAndTo( baz, bar ) );
        assertThat( edges ).noneMatch( isEdgeWithFromAndTo( foo, foo ) );
        assertThat( edges ).noneMatch( isEdgeWithFromAndTo( bar, bar ) );
        assertThat( edges ).noneMatch( isEdgeWithFromAndTo( baz, baz ) );
    }
}
