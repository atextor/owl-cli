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

package de.atextor.owlcli.diagram.mappers;

import de.atextor.owlcli.diagram.graph.Node;
import org.semanticweb.owlapi.model.IRI;

import java.util.UUID;

public class DefaultIdentifierMapper implements IdentifierMapper {
    private final MappingConfiguration mappingConfig;

    public DefaultIdentifierMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

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
