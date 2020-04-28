package de.atextor.owlcli.diagram.graph.transformer;

import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.NodeType;
import de.atextor.owlcli.diagram.mappers.MappingConfiguration;
import io.vavr.Tuple2;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PropertyMarkerMerger extends GraphTransformer {
    MappingConfiguration mappingConfiguration;

    public PropertyMarkerMerger( final MappingConfiguration mappingConfiguration ) {
        this.mappingConfiguration = mappingConfiguration;
    }

    private Optional<Node> getNode( final Set<GraphElement> graph, final Node.Id id ) {
        return graph.stream()
            .filter( GraphElement::isNode )
            .map( GraphElement::asNode )
            .filter( node -> node.getId().equals( id ) )
            .findFirst();
    }

    private Optional<NodeType.PropertyMarker> markerByEdge( final Set<GraphElement> graph, final Edge edge ) {
        return getNode( graph, edge.getTo() ).stream()
            .flatMap( node -> node.view( NodeType.PropertyMarker.class ) )
            .findFirst();
    }

    private ChangeSet mergePropertyMarkers( final Set<Tuple2<Edge, NodeType.PropertyMarker>> propertyMarkers ) {
        final Set<NodeType.PropertyMarker.Kind> mergedKindSet =
            propertyMarkers.stream().flatMap( marker -> marker._2().getKind().stream() ).collect( Collectors.toSet() );
        final NodeType.PropertyMarker newMarker =
            new NodeType.PropertyMarker( mappingConfiguration.getIdentifierMapper().getSyntheticId(), mergedKindSet );

        final Edge newEdge = propertyMarkers.iterator().next()._1().setTo( newMarker.getId() );

        final Set<GraphElement> additions = Set.of( newMarker, newEdge );
        final Set<GraphElement> deletions = propertyMarkers.stream()
            .flatMap( tuple -> Stream.of( tuple._1(), tuple._2() ) )
            .collect( Collectors.toSet() );

        return new ChangeSet( additions, deletions );
    }

    @Override
    public Set<GraphElement> apply( final Set<GraphElement> graph ) {
        return graph.stream()
            .filter( GraphElement::isEdge )
            .map( GraphElement::asEdge )
            .collect( Collectors.groupingBy( Edge::getFrom ) )
            .values().stream().map( edges -> {
                final Set<Tuple2<Edge, NodeType.PropertyMarker>> propertyMarkers = edges.stream()
                    .flatMap( edge -> markerByEdge( graph, edge )
                        .map( marker -> new Tuple2<>( edge, marker ) )
                        .stream() ).collect( Collectors.toSet() );
                if ( propertyMarkers.size() > 1 ) {
                    return mergePropertyMarkers( propertyMarkers );
                }
                return ChangeSet.EMPTY;
            } ).reduce( ChangeSet.EMPTY, ChangeSet::merge )
            .applyTo( graph );
    }
}
