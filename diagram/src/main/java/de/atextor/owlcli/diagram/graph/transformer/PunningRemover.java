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
