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
import org.semanticweb.owlapi.model.IRI;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Base class for graph transformations
 */
public abstract class GraphTransformer implements UnaryOperator<Set<GraphElement>> {
    protected Stream<Node> findNodesWithIri( final Set<GraphElement> graph, final IRI iri ) {
        return graph.stream()
            .filter( GraphElement::isNode )
            .map( GraphElement::asNode )
            .filter( node -> node.getId().iri().map( nodeIri -> nodeIri.equals( iri ) ).orElse( false ) );
    }

    protected ChangeSet updateEdgesTo( final Set<GraphElement> graph, final Node oldToNode, final Node newToNode ) {
        return updateEdge( graph, oldToNode, newToNode, Edge::getTo, Edge::setTo );
    }

    protected ChangeSet updateEdgesFrom( final Set<GraphElement> graph, final Node oldFromNode,
                                         final Node newFromNode ) {
        return updateEdge( graph, oldFromNode, newFromNode, Edge::getFrom, Edge::setFrom );
    }

    private ChangeSet updateEdge( final Set<GraphElement> graph, final Node oldNode, final Node newNode,
                                  final Function<Edge, Node> getter,
                                  final BiFunction<Edge, Node, Edge> setter ) {
        return graph.stream()
            .filter( GraphElement::isEdge )
            .map( GraphElement::asEdge )
            .filter( edge -> getter.apply( edge ).equals( oldNode ) )
            .map( edge -> new ChangeSet( Set.of( setter.apply( edge, newNode ) ), Set.of( edge ) ) )
            .reduce( ChangeSet.EMPTY, ChangeSet::merge );
    }
}
