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

package de.atextor.owlcli.diagram.printers;

import de.atextor.owlcli.diagram.mappers.MappingConfiguration;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;

/**
 * Serializes {@link org.semanticweb.owlapi.model.OWLIndividual}s into expressions
 */
public class OWLIndividualPrinter implements OWLIndividualVisitorEx<String> {
    MappingConfiguration mappingConfiguration;

    public OWLIndividualPrinter( final MappingConfiguration mappingConfiguration ) {
        this.mappingConfiguration = mappingConfiguration;
    }

    @Override
    public String visit( final @Nonnull OWLAnonymousIndividual individual ) {
        return "[]";
    }

    @Override
    public String visit( final @Nonnull OWLNamedIndividual individual ) {
        return individual.getIRI().getFragment();
    }
}
