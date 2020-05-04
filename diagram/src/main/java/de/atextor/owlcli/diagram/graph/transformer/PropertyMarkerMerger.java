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

package de.atextor.owlcli.diagram.graph.transformer;

import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.mappers.MappingConfiguration;
import io.vavr.Tuple2;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implements a graph transformer that merges multiple {@link Node.PropertyMarker}s on a given Object property
 * or Data Property into one Property Marker
 */
public class PropertyMarkerMerger extends GraphTransformer {
    MappingConfiguration mappingConfiguration;

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

    private Optional<Node.PropertyMarker> markerByEdge( final Set<GraphElement> graph, final Edge edge ) {
        return getNode( graph, edge.getTo() ).stream()
            .flatMap( node -> node.view( Node.PropertyMarker.class ) )
            .findFirst();
    }

    private ChangeSet mergePropertyMarkers( final Set<Tuple2<Edge, Node.PropertyMarker>> propertyMarkers ) {
        final Set<Node.PropertyMarker.Kind> mergedKindSet =
            propertyMarkers.stream().flatMap( marker -> marker._2().getKind().stream() ).collect( Collectors.toSet() );
        final Node.PropertyMarker newMarker =
            new Node.PropertyMarker( mappingConfiguration.getIdentifierMapper().getSyntheticId(), mergedKindSet );

        final Edge newEdge = propertyMarkers.iterator().next()._1().setTo( newMarker.getId() );

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
     * @return the resulting graph that has at most one {@link Node.PropertyMarker} for each property
     */
    @Override
    public Set<GraphElement> apply( final Set<GraphElement> graph ) {
        return graph.stream()
            .filter( GraphElement::isEdge )
            .map( GraphElement::asEdge )
            .collect( Collectors.groupingBy( Edge::getFrom ) )
            .values().stream().map( edges -> {
                final Set<Tuple2<Edge, Node.PropertyMarker>> propertyMarkers = edges.stream()
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
