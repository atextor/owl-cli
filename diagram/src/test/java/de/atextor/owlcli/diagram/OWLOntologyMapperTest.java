package de.atextor.owlcli.diagram;

import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.mappers.DefaultMappingConfiguration;
import de.atextor.owlcli.diagram.mappers.MappingConfiguration;
import de.atextor.owlcli.diagram.mappers.OWLOntologyMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class OWLOntologyMapperTest extends MapperTestBase {
    private final MappingConfiguration mappingConfiguration = DefaultMappingConfiguration.builder().build();
    private final OWLOntologyMapper mapper = new OWLOntologyMapper( mappingConfiguration );

    @Test
    public void testOWLAnnotationPropertyDomainAxiom() {
        final String ontology = """
            :foo a owl:Class .
            :bar a owl:AnnotationProperty ;
               rdfs:domain :foo .
            """;

        final Set<GraphElement> result = mapper.apply( createOntology( ontology ) );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( node -> node.is( Node.Class.class ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 1 );

        final Edge propertyToDomain = edges.iterator().next();
        assertThat( propertyToDomain.getType() ).isEqualTo( Edge.Type.DEFAULT_ARROW );
        assertThat( propertyToDomain.getClass() ).isEqualTo( Edge.Decorated.class );
        assertThat( ( (Edge.Decorated) propertyToDomain ).getDecoration() ).isEqualTo( Edge.Decorated.DOMAIN );
        assertThat( propertyToDomain.getTo().getId() ).isEqualTo( nodes.stream()
            .filter( node -> node.is( Node.Class.class ) ).map( Node::getId ).findFirst().get().getId() );
    }

    @Test
    public void testOWLAnnotationPropertyDomainAxiomWithPunning() {
        final String ontology = """
            :foo a owl:Class .
            :foo a owl:NamedIndividual .
            :bar a owl:AnnotationProperty ;
               rdfs:domain :foo .
            """;

        final Set<GraphElement> result = mapper.apply( createOntology( ontology ) );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 3 );
        assertThat( nodes ).anyMatch( node -> node.is( Node.Class.class ) );
        assertThat( nodes ).anyMatch( node -> node.is( Node.Individual.class ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 2 );

        assertThat( edges ).anyMatch( hasDefaultArrow.and( hasDomainDecoration ).and( hasFromBar ).and( edge ->
            nodes.stream().anyMatch( node ->
                node.is( Node.Class.class ) && node.getId().equals( edge.getTo() ) ) ) );

        assertThat( edges ).anyMatch( hasDefaultArrow.and( hasDomainDecoration ).and( hasFromBar ).and( edge ->
            nodes.stream().anyMatch( node ->
                node.is( Node.Individual.class ) && node.getId().equals( edge.getTo() ) ) ) );
    }

    @Test
    public void testOWLAnnotationPropertyRangeAxiom() {
        final String ontology = """
            :foo a owl:Class .
            :bar a owl:AnnotationProperty ;
               rdfs:range :foo .
            """;

        final Set<GraphElement> result = mapper.apply( createOntology( ontology ) );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( node -> node.is( Node.Class.class ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 1 );

        final Edge propertyToRange = edges.iterator().next();
        assertThat( propertyToRange.getType() ).isEqualTo( Edge.Type.DEFAULT_ARROW );
        assertThat( propertyToRange.getClass() ).isEqualTo( Edge.Decorated.class );
        assertThat( ( (Edge.Decorated) propertyToRange ).getDecoration() ).isEqualTo( Edge.Decorated.RANGE );
        assertThat( propertyToRange.getTo().getId() ).isEqualTo( nodes.stream()
            .filter( node -> node.is( Node.Class.class ) ).map( Node::getId ).findFirst().get().getId() );
    }

    @Test
    public void testOWLAnnotationPropertyRangeAxiomWithPunning() {
        final String ontology = """
            :foo a owl:Class .
            :foo a owl:NamedIndividual .
            :bar a owl:AnnotationProperty ;
               rdfs:range :foo .
            """;

        final Set<GraphElement> result = mapper.apply( createOntology( ontology ) );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 3 );
        assertThat( nodes ).anyMatch( node -> node.is( Node.Class.class ) );
        assertThat( nodes ).anyMatch( node -> node.is( Node.Individual.class ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 2 );

        assertThat( edges ).anyMatch( hasDefaultArrow.and( hasRangeDecoration ).and( hasFromBar ).and( edge ->
            nodes.stream().anyMatch( node ->
                node.is( Node.Class.class ) && node.getId().equals( edge.getTo() ) ) ) );

        assertThat( edges ).anyMatch( hasDefaultArrow.and( hasRangeDecoration ).and( hasFromBar ).and( edge ->
            nodes.stream().anyMatch( node ->
                node.is( Node.Individual.class ) && node.getId().equals( edge.getTo() ) ) ) );
    }

    @Test
    public void testOWLAnnotationAssertionAxiom() {
        final String ontology = """
            :foo a owl:AnnotationProperty .
            :Baz a owl:Class .
            :bar a owl:NamedIndividual ;
               :foo :Baz .
            """;

        final Set<GraphElement> result = mapper.apply( createOntology( ontology ) );
        assertThat( result ).isNotEmpty();

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 4 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( node -> node.is( Node.Class.class ) );
        assertThat( nodes ).anyMatch( node -> node.is( Node.Individual.class ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 3 );
        assertThat( edges ).anyMatch( hasNoArrow.and( isEdgeWithFrom( "bar" ) ) );
        assertThat( edges ).anyMatch( hasDashedArrow.and( isEdgeWithTo( "foo" ) ) );
        assertThat( edges ).anyMatch( hasDefaultArrow.and( isEdgeWithTo( "Baz" ) ) );
    }

    @Test
    public void testMultipleObjectPropertyCharacteristics() {
        final String ontology = """
            :foo a owl:ObjectProperty,
                   owl:FunctionalProperty,
                   owl:TransitiveProperty,
                   owl:IrreflexiveProperty .
            """;

        final Set<GraphElement> result = mapper.apply( createOntology( ontology ) );
        assertThat( result ).hasSize( 3 );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( node -> node.view( Node.PropertyMarker.class ).map( propertyMarker ->
            propertyMarker.getKind().contains( Node.PropertyMarker.Kind.FUNCTIONAL ) &&
                propertyMarker.getKind().contains( Node.PropertyMarker.Kind.TRANSITIVE ) &&
                propertyMarker.getKind().contains( Node.PropertyMarker.Kind.IRREFLEXIVE )
        ).findFirst().orElse( false ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 1 );

        assertThat( edges ).anyMatch( isEdgeWithFrom( "foo" ).and( hasDashedArrow ) );
    }

}
