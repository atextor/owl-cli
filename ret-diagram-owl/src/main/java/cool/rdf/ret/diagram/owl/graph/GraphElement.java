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

import java.util.stream.Stream;

/**
 * An element of the graph
 */
public interface GraphElement {
    /**
     * Visitor for graph elements
     *
     * @param <T> the visitor's return type
     */
    interface Visitor<T> {
        /**
         * Visit a plain edge
         *
         * @param edge the ege
         * @return the visitor's return value
         */
        T visit( Edge.Plain edge );

        /**
         * Visit a decorated edge
         *
         * @param decoratedEdge the ege
         * @return the visitor's return value
         */
        T visit( Edge.Decorated decoratedEdge );

        /**
         * Visit a node
         *
         * @param node the node
         * @return the visitor's return value
         */
        T visit( Node node );
    }

    /**
     * Returns whether this graph element is an edge
     *
     * @return true if it is an edge
     */
    default boolean isEdge() {
        return false;
    }

    /**
     * Returns whether this graph element is a node
     *
     * @return true if is a node
     */
    default boolean isNode() {
        return false;
    }

    /**
     * Checks if this graph element is a certain type of subclass
     *
     * @param class_ the subclass
     * @param <T> the type of the subclass
     * @return true if this is an instance of the class
     */
    default <T extends GraphElement> boolean is( final Class<T> class_ ) {
        return class_.isAssignableFrom( getClass() );
    }

    /**
     * Casts this graph element into a concrete subclass
     *
     * @param class_ the subclass
     * @param <T> the type of the subclass
     * @return the cast object
     */
    default <T extends GraphElement> T as( final Class<T> class_ ) {
        return class_.cast( this );
    }

    /**
     * Returns a view to this graph element: A stream containing this object cast to the given subclass if possible, empty stream otherwise
     *
     * @param class_ the subclass
     * @param <T> the type of the subclass
     * @return the stream
     */
    default <T extends GraphElement> Stream<T> view( final Class<T> class_ ) {
        return is( class_ ) ? Stream.of( as( class_ ) ) : Stream.empty();
    }

    /**
     * Returns this graph element as a node
     *
     * @return the node
     */
    default Node asNode() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns this graph element as an edge
     *
     * @return the edge
     */
    default Edge asEdge() {
        throw new UnsupportedOperationException();
    }

    /**
     * Accepts a graph element visitor
     *
     * @param visitor the visitor
     * @param <T> the visitor's return type
     * @return the visitor's return value
     */
    <T> T accept( Visitor<T> visitor );
}
