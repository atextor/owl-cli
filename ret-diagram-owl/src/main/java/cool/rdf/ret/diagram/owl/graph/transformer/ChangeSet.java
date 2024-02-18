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

package cool.rdf.ret.diagram.owl.graph.transformer;

import com.google.common.collect.Sets;
import cool.rdf.ret.diagram.owl.graph.GraphElement;

import java.util.Set;

/**
 * Represents additions and deletions to perform on a set of {@link GraphElement}s
 */
record ChangeSet( Set<GraphElement> additions, Set<GraphElement> deletions ) {
    public static final ChangeSet EMPTY = new ChangeSet( Set.of(), Set.of() );

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
