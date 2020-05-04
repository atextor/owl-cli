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

import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.model.IRI;

class DefaultNameMapper implements NameMapper {
    private final MappingConfiguration mappingConfig;

    public DefaultNameMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }


    @Override
    public String getName( final HasIRI object ) {
        return getName( object.getIRI() );
    }

    @Override
    public String getName( final IRI object ) {
        return object.getFragment();
    }
}
