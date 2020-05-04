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

package de.atextor.owlcli.diagram.graph;

import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A graph that consists of one node that can be separately retrieved and any number of additional {@link GraphElement}s
 */
@Getter
public class Graph {
    Node node;
    Stream<GraphElement> otherElements;

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
