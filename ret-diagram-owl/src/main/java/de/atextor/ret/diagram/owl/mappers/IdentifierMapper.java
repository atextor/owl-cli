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

package de.atextor.ret.diagram.owl.mappers;

import de.atextor.ret.diagram.owl.graph.Node;
import org.semanticweb.owlapi.model.IRI;

/**
 * Creates {@link Node.Id}s for named nodes (i.e. for ontology elements that are identified by {@link IRI}s)
 * or anonymous nodes
 */
public interface IdentifierMapper {
    Node.Id getIdForIri( final IRI iri );

    Node.Id getSyntheticId();

    Node.Id getSyntheticIdForIri( final IRI iri );
}
