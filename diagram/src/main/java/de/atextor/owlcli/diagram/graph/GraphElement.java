/*
 * Copyright 2021 Andreas Textor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.atextor.owlcli.diagram.graph;

import java.util.stream.Stream;

/**
 * An element of the graph
 */
public interface GraphElement {
    interface Visitor<T> {
        T visit( Edge.Plain edge );

        T visit( Edge.Decorated decoratedEdge );

        T visit( Node nodeType );
    }

    default boolean isEdge() {
        return false;
    }

    default boolean isNode() {
        return false;
    }

    default <T extends GraphElement> boolean is( final Class<T> class_ ) {
        return class_.isAssignableFrom( getClass() );
    }

    default <T extends GraphElement> T as( final Class<T> class_ ) {
        return class_.cast( this );
    }

    default <T extends GraphElement> Stream<T> view( final Class<T> class_ ) {
        return is( class_ ) ? Stream.of( as( class_ ) ) : Stream.empty();
    }

    default Node asNode() {
        throw new UnsupportedOperationException();
    }

    default Edge asEdge() {
        throw new UnsupportedOperationException();
    }

    <T> T accept( Visitor<T> visitor );
}
