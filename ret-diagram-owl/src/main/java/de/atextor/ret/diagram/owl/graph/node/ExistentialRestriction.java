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

package de.atextor.ret.diagram.owl.graph.node;

import de.atextor.ret.diagram.owl.graph.Node;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;

/**
 * Represents an existential restriction ("some") node in the graph.
 */
@Value
@EqualsAndHashCode( callSuper = true )
@With
public class ExistentialRestriction extends Node {
    Id id;

    @Override
    public <T> T accept( final Visitor<T> visitor ) {
        return visitor.visit( this );
    }
}