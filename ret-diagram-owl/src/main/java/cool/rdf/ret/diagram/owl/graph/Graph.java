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

package cool.rdf.ret.diagram.owl.graph;

import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A graph that consists of one node that can be separately retrieved and any number of additional {@link GraphElement}s
 */
@Getter
public class Graph {
    final Node node;

    final Stream<GraphElement> otherElements;

    /**
     * Builds a new graph from a given focus node and a number of other graph elements
     *
     * @param node the focus node
     * @param otherElements the remaining graph elements
     */
    protected Graph( final Node node, final Stream<GraphElement> otherElements ) {
        this.node = node;
        this.otherElements = otherElements;
    }

    /**
     * Builds a new graph from a given focus node and a number of other graph elements
     *
     * @param node the focus node
     * @param otherElements the remaining graph elements
     * @return the new graph
     */
    public static Graph of( final Node node, final Stream<GraphElement> otherElements ) {
        return new Graph( node, otherElements );
    }

    /**
     * Builds a new graph from a given focus node
     *
     * @param node the focus node
     * @return the new graph
     */
    public static Graph of( final Node node ) {
        return new Graph( node, Stream.empty() );
    }

    /**
     * Builds a new graph by merging this graph with the other graph. This graph's focus node becomes the constructed graph's focus node.
     *
     * @param other the other graph
     * @return the new graph
     */
    public Graph and( final Graph other ) {
        return new Graph( node, Stream.concat( otherElements, other.toStream() ) );
    }

    /**
     * Builds a new graph by merging another element with this graph's other elements.
     *
     * @param element the new element
     * @return the new graph
     */
    public Graph and( final GraphElement element ) {
        return new Graph( node, Stream.concat( Stream.of( element ), otherElements ) );
    }

    /**
     * Builds a new graph by merging a number of other elements with this graph's other elements.
     *
     * @param elements the new elements
     * @return the new graph
     */
    public Graph and( final Stream<GraphElement> elements ) {
        return new Graph( node, Stream.concat( otherElements, elements ) );
    }

    /**
     * Turns this graph into a stream of graph elements
     *
     * @return the stream
     */
    public Stream<GraphElement> toStream() {
        return Stream.concat( Stream.of( node ), otherElements );
    }

    /**
     * Turns this graph into a set of graph elements
     *
     * @return the set
     */
    public Set<GraphElement> getElementSet() {
        return toStream().collect( Collectors.toSet() );
    }
}
