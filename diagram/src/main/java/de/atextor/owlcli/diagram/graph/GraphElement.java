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
