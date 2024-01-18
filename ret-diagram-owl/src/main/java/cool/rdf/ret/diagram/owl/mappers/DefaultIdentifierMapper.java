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

package cool.rdf.ret.diagram.owl.mappers;

import cool.rdf.ret.diagram.owl.graph.Node;
import org.semanticweb.owlapi.model.IRI;

import java.util.UUID;

/**
 * Default implementation for the {@link IdentifierMapper}
 */
public class DefaultIdentifierMapper implements IdentifierMapper {
    private String getRandomIdString() {
        return "_" + UUID.randomUUID().toString().replace( "-", "" );
    }

    @Override
    public Node.Id getIdForIri( final IRI iri ) {
        return new Node.Id( iri.getFragment(), iri );
    }

    @Override
    public Node.Id getSyntheticId() {
        return new Node.Id( getRandomIdString() );
    }

    @Override
    public Node.Id getSyntheticIdForIri( final IRI iri ) {
        return new Node.Id( getRandomIdString(), iri );
    }
}
