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
    /**
     * Finds the nodes in a graph that have a given IRI
     *
     * @param graph the graph
     * @param iri the IRI to look for
     * @return the resulting nodes
     */
    protected Stream<Node> findNodesWithIri( final Set<GraphElement> graph, final IRI iri ) {
        return graph.stream()
            .filter( GraphElement::isNode )
            .map( GraphElement::asNode )
            .filter( node -> node.getId().getIri().map( nodeIri -> nodeIri.equals( iri ) ).orElse( false ) );
    }

    /**
     * Calculates a {@link ChangeSet} for a given graph in which all edges pointing to a given node are instead now pointing to a diffent
     * node
     *
     * @param graph the graph
     * @param oldToNode the old node
     * @param newToNode the new node
     * @return the change set
     */
    protected ChangeSet updateEdgesTo( final Set<GraphElement> graph, final Node oldToNode, final Node newToNode ) {
        return updateEdge( graph, oldToNode, newToNode, Edge::getTo, Edge::setTo );
    }

    /**
     * Calculates a {@link ChangeSet} for a given graph in which all edges outgoing from a given node are instead now outgoing from a
     * different node
     *
     * @param graph the graph
     * @param oldFromNode the old node
     * @param newFromNode the new node
     * @return the change set
     */
    protected ChangeSet updateEdgesFrom( final Set<GraphElement> graph, final Node oldFromNode, final Node newFromNode ) {
        return updateEdge( graph, oldFromNode, newFromNode, Edge::getFrom, Edge::setFrom );
    }

    private ChangeSet updateEdge( final Set<GraphElement> graph, final Node oldNode, final Node newNode,
        final Function<Edge, Node> getter, final BiFunction<Edge, Node, Edge> setter ) {
        return graph.stream()
            .filter( GraphElement::isEdge )
            .map( GraphElement::asEdge )
            .filter( edge -> getter.apply( edge ).equals( oldNode ) )
            .map( edge -> new ChangeSet( Set.of( setter.apply( edge, newNode ) ), Set.of( edge ) ) )
            .reduce( ChangeSet.EMPTY, ChangeSet::merge );
    }
}
