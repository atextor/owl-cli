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

package de.atextor.ret.diagram.owl.graph.node;

import de.atextor.ret.diagram.owl.graph.Node;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;
import org.semanticweb.owlapi.model.IRI;

/**
 * Represents a reference to some yet unknown other graph that has a {@link Node.Id} with a given {@link IRI}.
 * This type of node should never end up in the final graph, as it is resolved by the
 * {@link de.atextor.ret.diagram.owl.graph.transformer.IriReferenceResolver} after the Axiom -> Graph Elements mapping
 * is done.
 */
@Value
@EqualsAndHashCode( callSuper = true )
@With
public class IRIReference extends Node.InvisibleNode {
    Id id;

    IRI iri;

    @Override
    public <T> T accept( final Visitor<T> visitor ) {
        return visitor.visit( this );
    }
}
