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

import com.google.common.collect.Sets;
import de.atextor.owlcli.diagram.graph.GraphElement;
import lombok.Value;

import java.util.Set;

/**
 * Represents additions and deletions to perform on a set of {@link GraphElement}s
 */
@Value
class ChangeSet {
    public static final ChangeSet EMPTY = new ChangeSet( Set.of(), Set.of() );

    Set<GraphElement> additions;
    Set<GraphElement> deletions;

    /**
     * Create a new ChangeSet from this and another given ChangeSet
     *
     * @param other the other ChangeSet
     * @return the merged ChangeSet
     */
    ChangeSet merge( final ChangeSet other ) {
        return new ChangeSet( Sets.union( additions, other.additions ), Sets.union( deletions, other.deletions ) );
    }

    /**
     * Apply this ChangeSet to a graph
     *
     * @param graph the input graph
     * @return the resulting graph
     */
    Set<GraphElement> applyTo( final Set<GraphElement> graph ) {
        return Sets.union( Sets.difference( graph, deletions ), additions );
    }
}
