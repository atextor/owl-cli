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

/**
 * Implements a graph transformer that resolves
 * <a href="https://www.w3.org/TR/owl2-new-features/#F12:_Punning">Punning</a> in a graph: An input ontology that
 * uses punning for e.g. an individual and a class results in a graph that contains both the individual and the class
 * as nodes, but both share the same {@link Node.Id}, as it is derived from the the element's
 * {@link org.semanticweb.owlapi.model.IRI}. This transformer replaces the nodes with new, uniquely identified nodes
 * (that keep the original IRI in their IDs) and updates all edges in the graph accordingly.
 */
public class PunningRemover extends GraphTransformer {
    private final MappingConfiguration mappingConfiguration;

    /**
     * Initialize the transformer
     *
     * @param mappingConfiguration the context mapping configuration
     */
    public PunningRemover( final MappingConfiguration mappingConfiguration ) {
        this.mappingConfiguration = mappingConfiguration;
    }

    /**
     * Apply this transformer to the given input graph
     *
     * @param graph the input graph
     * @return the resulting graph that has no more nodes with duplicate {@link Node.Id}s due to punning
     */
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
