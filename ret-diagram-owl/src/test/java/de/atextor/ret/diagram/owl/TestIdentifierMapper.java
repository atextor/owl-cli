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

package de.atextor.ret.diagram.owl;

import de.atextor.ret.diagram.owl.graph.Node;
import de.atextor.ret.diagram.owl.mappers.IdentifierMapper;
import org.semanticweb.owlapi.model.IRI;

import java.util.Stack;
import java.util.UUID;

public class TestIdentifierMapper implements IdentifierMapper {
    private final Stack<Node.Id> preconfiguredAnonIds = new Stack<>();

    @Override
    public Node.Id getIdForIri( final IRI iri ) {
        return new Node.Id( iri.getFragment(), iri );
    }

    void pushAnonId( final Node.Id id ) {
        preconfiguredAnonIds.push( id );
    }

    private String getRandomIdString() {
        return "_" + UUID.randomUUID().toString().replace( "-", "" );
    }

    @Override
    public Node.Id getSyntheticId() {
        if ( preconfiguredAnonIds.empty() ) {
            return new Node.Id( getRandomIdString() );
        }
        return preconfiguredAnonIds.pop();
    }

    @Override
    public Node.Id getSyntheticIdForIri( final IRI iri ) {
        if ( preconfiguredAnonIds.empty() ) {
            return new Node.Id( getRandomIdString(), iri );
        }
        return preconfiguredAnonIds.pop();
    }
}
