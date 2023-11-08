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

package de.atextor.ret.diagram.owl.graph;

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

    protected Graph( final Node node, final Stream<GraphElement> otherElements ) {
        this.node = node;
        this.otherElements = otherElements;
    }

    public static Graph of( final Node node, final Stream<GraphElement> otherElements ) {
        return new Graph( node, otherElements );
    }

    public static Graph of( final Node node ) {
        return new Graph( node, Stream.empty() );
    }

    public Graph and( final Graph other ) {
        return new Graph( node, Stream.concat( otherElements, other.toStream() ) );
    }

    public Graph and( final GraphElement element ) {
        return new Graph( node, Stream.concat( Stream.of( element ), otherElements ) );
    }

    public Graph and( final Stream<GraphElement> elements ) {
        return new Graph( node, Stream.concat( otherElements, elements ) );
    }

    public Stream<GraphElement> toStream() {
        return Stream.concat( Stream.of( node ), otherElements );
    }

    public Set<GraphElement> getElementSet() {
        return toStream().collect( Collectors.toSet() );
    }
}
