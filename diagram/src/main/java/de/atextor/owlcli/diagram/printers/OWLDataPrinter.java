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
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDataVisitorEx;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLLiteral;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

public class OWLDataPrinter implements OWLDataVisitorEx<String> {
    private final MappingConfiguration mappingConfig;

    public OWLDataPrinter( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public String visit( final @Nonnull OWLDataComplementOf node ) {
        return String.format( "not(%s)", node.getDataRange().accept( this ) );
    }

    @Override
    public String visit( final @Nonnull OWLDataOneOf node ) {
        return node.values().map( literal -> literal.accept( this ) )
            .collect( Collectors.joining( ", ", "{", "}" ) );
    }

    @Override
    public String visit( final @Nonnull OWLDataIntersectionOf node ) {
        return node.operands().map( operand -> operand.accept( this ) )
            .collect( Collectors.joining( ", ", "and(", ")" ) );
    }

    @Override
    public String visit( final @Nonnull OWLDataUnionOf node ) {
        return node.operands().map( operand -> operand.accept( this ) )
            .collect( Collectors.joining( ", ", "or(", ")" ) );
    }

    @Override
    public String visit( final @Nonnull OWLDatatypeRestriction node ) {
        return node.facetRestrictions().map( owlFacetRestriction ->
            owlFacetRestriction.accept( this ) + " "
                + owlFacetRestriction.getFacetValue().getLiteral() ).collect( Collectors.joining( ", ", "[", "]" ) );
    }

    @Override
    public String visit( final @Nonnull OWLFacetRestriction node ) {
        return node.getFacet().getSymbolicForm();
    }

    @Override
    public String visit( final @Nonnull OWLDatatype node ) {
        return mappingConfig.getNameMapper().getName( node );
    }

    @Override
    public String visit( final @Nonnull OWLLiteral node ) {
        if ( node.isBoolean() || node.isDouble() || node.isFloat() || node.isInteger() ) {
            return node.getLiteral();
        }
        return "\"" + node.getLiteral() + "\"";
    }
}
