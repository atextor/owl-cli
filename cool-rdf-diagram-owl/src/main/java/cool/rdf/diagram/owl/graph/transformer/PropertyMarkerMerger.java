/*
 * Copyright 2024 Andreas Textor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cool.rdf.diagram.owl.graph.transformer;

import cool.rdf.diagram.owl.graph.Edge;
import cool.rdf.diagram.owl.graph.GraphElement;
import cool.rdf.diagram.owl.graph.Node;
import cool.rdf.diagram.owl.mappers.MappingConfiguration;
import cool.rdf.diagram.owl.graph.node.PropertyMarker;
import io.vavr.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implements a graph transformer that merges multiple {@link PropertyMarker}s on a given Object property
 * or Data Property into one Property Marker
 */
public class PropertyMarkerMerger extends GraphTransformer {
    private final MappingConfiguration mappingConfiguration;

    private static final Logger LOG = LoggerFactory.getLogger( PropertyMarkerMerger.class );

    /**
     * Initialize the transformer
     *
     * @param mappingConfiguration the context mapping configuration
     */
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

    private Optional<PropertyMarker> markerByEdge( final Set<GraphElement> graph, final Edge edge ) {
        return getNode( graph, edge.getTo().getId() ).stream()
            .flatMap( node -> node.view( PropertyMarker.class ) )
            .findFirst();
    }

    private ChangeSet mergePropertyMarkers( final Set<Tuple2<Edge, PropertyMarker>> propertyMarkers ) {
        final Set<PropertyMarker.Kind> mergedKindSet =
            propertyMarkers.stream().flatMap( marker -> marker._2().getKind().stream() ).collect( Collectors.toSet() );
        final PropertyMarker newMarker =
            new PropertyMarker( mappingConfiguration.getIdentifierMapper().getSyntheticId(), mergedKindSet );

        final Edge newEdge = propertyMarkers.iterator().next()._1().setTo( newMarker );

        final Set<GraphElement> additions = Set.of( newMarker, newEdge );
        final Set<GraphElement> deletions = propertyMarkers.stream()
            .flatMap( tuple -> Stream.of( tuple._1(), tuple._2() ) )
            .collect( Collectors.toSet() );

        return new ChangeSet( additions, deletions );
    }

    /**
     * Apply this transformer to the given input graph
     *
     * @param graph the input graph
     * @return the resulting graph that has at most one {@link PropertyMarker} for each property
     */
    @Override
    public Set<GraphElement> apply( final Set<GraphElement> graph ) {
        LOG.debug( "Merging Property Markers in {}", graph );
        final Set<GraphElement> result = graph.stream()
            .filter( GraphElement::isEdge )
            .map( GraphElement::asEdge )
            .collect( Collectors.groupingBy( Edge::getFrom ) )
            .values().stream().map( edges -> {
                final Set<Tuple2<Edge, PropertyMarker>> propertyMarkers = edges.stream()
                    .flatMap( edge -> markerByEdge( graph, edge )
                        .map( marker -> new Tuple2<>( edge, marker ) )
                        .stream() ).collect( Collectors.toSet() );
                if ( propertyMarkers.size() > 1 ) {
                    return mergePropertyMarkers( propertyMarkers );
                }
                return ChangeSet.EMPTY;
            } ).reduce( ChangeSet.EMPTY, ChangeSet::merge )
            .applyTo( graph );
        LOG.debug( "Processed graph: {}", result );
        return result;
    }
}
