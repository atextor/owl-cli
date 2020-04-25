package de.atextor.owlcli.diagram.graph.transformer;

import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.mappers.MappingConfiguration;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PunningRemover extends GraphTransformer {
    private final MappingConfiguration mappingConfiguration;

    public PunningRemover( final MappingConfiguration mappingConfiguration ) {
        this.mappingConfiguration = mappingConfiguration;
    }

    @Override
    public Set<GraphElement> apply( final Set<GraphElement> graph ) {
        return graph.stream()
            .filter( GraphElement::isNode )
            .map( GraphElement::asNode )
            .filter( node -> node.getId().getIri().isPresent() )
            .collect( Collectors.groupingBy( node -> node.getId().getIri().get(), Collectors.counting() ) )
            .entrySet().stream()
            .filter( entry -> entry.getValue() > 1 )
            .map( Map.Entry::getKey )
            .flatMap( iri -> findNodesWithIri( graph, iri ).flatMap( node -> updateNode( graph, node ) ) )
            .reduce( ChangeSet.EMPTY, ChangeSet::merge )
            .applyTo( graph );
    }

    private Stream<ChangeSet> updateNode( final Set<GraphElement> graph, final Node node ) {
        final Node newNode = node.clone( buildNewNodeId( node.getId() ) );
        final ChangeSet updatedToEdges = updateEdgesTo( graph, node, newNode );
        final ChangeSet updatedNode = new ChangeSet( Set.of( newNode ), Set.of( node ) );
        return Stream.of( updatedNode, updatedToEdges );
    }

    private Node.Id buildNewNodeId( final Node.Id original ) {
        return original.getIri().map( iri ->
            mappingConfiguration.getIdentifierMapper().getSyntheticIdForIri( iri ) )
            .orElseGet( () -> mappingConfiguration.getIdentifierMapper().getSyntheticId() );
    }
}
