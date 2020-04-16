package de.atextor.owlcli.diagram;

import de.atextor.owlcli.diagram.graph.DecoratedEdge;
import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.NodeType;
import de.atextor.owlcli.diagram.mappers.MappingConfiguration;
import de.atextor.owlcli.diagram.mappers.OWLOntologyMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class OWLOntologyMapperTest extends MapperTestBase {
    private final MappingConfiguration mappingConfiguration = createTestMappingConfiguration();
    private final OWLOntologyMapper mapper = new OWLOntologyMapper( mappingConfiguration );

    @Test
    public void testOWLAnnotationPropertyDomainAxiom() {
        final String ontology = """
            :foo a owl:Class .
            :bar a owl:AnnotationProperty ;
               rdfs:domain :foo .
            """;

        final Node.Id newClassNodeId = new Node.Id( "classNode", iri( "foo" ) );
        testIdentifierMapper.pushAnonId( newClassNodeId );

        final String iriReferenceId = "iriReference";
        testIdentifierMapper.pushAnonId( new Node.Id( iriReferenceId ) );

        final Set<GraphElement> result = mapper.apply( createOntology( ontology ) ).collect( Collectors.toSet() );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( newClassNodeId ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 1 );

        final Edge propertyToDomain = edges.iterator().next();
        assertThat( propertyToDomain.getType() ).isEqualTo( Edge.Type.DEFAULT_ARROW );
        assertThat( propertyToDomain.getClass() ).isEqualTo( DecoratedEdge.class );
        assertThat( ( (DecoratedEdge) propertyToDomain ).getDecoration() ).isEqualTo( DecoratedEdge.DOMAIN );
    }

    @Test
    public void testOWLAnnotationPropertyDomainAxiomWithPunning() {
        final String ontology = """
            :foo a owl:Class .
            :foo a owl:NamedIndividual .
            :bar a owl:AnnotationProperty ;
               rdfs:domain :foo .
            """;

        final Node.Id newIndividualId = new Node.Id( "individualNode", iri( "foo" ) );
        testIdentifierMapper.pushAnonId( newIndividualId );

        final Node.Id newClassNodeId = new Node.Id( "classNode", iri( "foo" ) );
        testIdentifierMapper.pushAnonId( newClassNodeId );

        final String iriReferenceId = "iriReference";
        testIdentifierMapper.pushAnonId( new Node.Id( iriReferenceId ) );

        final Set<GraphElement> result = mapper.apply( createOntology( ontology ) ).collect( Collectors.toSet() );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 3 );
        assertThat( nodes ).anyMatch( isNodeWithId( newClassNodeId ).and( node -> node.is( NodeType.Class.class ) ) );
        assertThat( nodes ).anyMatch( isNodeWithId( newIndividualId ).and( node -> node.is( NodeType.Individual.class ) ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 2 );

        final Predicate<Edge> hasDefaultArrow = edge -> edge.getType().equals( Edge.Type.DEFAULT_ARROW );
        final Predicate<Edge> hasDomainDecoration = edge -> edge.view( DecoratedEdge.class )
            .map( decoratedEdge -> decoratedEdge.getDecoration().equals( DecoratedEdge.DOMAIN ) )
            .findFirst()
            .orElse( false );

        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( "bar", "classNode" ).and( hasDefaultArrow ).and( hasDomainDecoration ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( "bar", "individualNode" ).and( hasDefaultArrow ).and( hasDomainDecoration ) );
    }
}
