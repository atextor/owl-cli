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

package cool.rdf.diagram.owl.graph.node;

import cool.rdf.diagram.owl.graph.Node;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;

import java.util.Set;

/**
 * Represents a property marker node in the graph, i.e. a node that contains the list of attributes that a given
 * OWL Object Property or OWL Data Property has.
 */
@Value
@EqualsAndHashCode( callSuper = true )
@With
public class PropertyMarker extends Node {
    /**
     * The various kinds of markers
     */
    public enum Kind {
        /**
         * The marked property is functional
         */
        FUNCTIONAL,

        /**
         * The marked property is inverse functional
         */
        INVERSE_FUNCTIONAL,

        /**
         * The marked property is transitive
         */
        TRANSITIVE,

        /**
         * The marked property is symmetric
         */
        SYMMETRIC,

        /**
         * The marked property is asymmetric
         */
        ASYMMETRIC,

        /**
         * The marked property is reflexive
         */
        REFLEXIVE,

        /**
         * The marked property is irreflexive
         */
        IRREFLEXIVE
    }

    Id id;

    Set<Kind> kind;

    @Override
    public <T> T accept( final Visitor<T> visitor ) {
        return visitor.visit( this );
    }
}
