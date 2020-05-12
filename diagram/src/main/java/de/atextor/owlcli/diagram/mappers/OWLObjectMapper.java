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

import de.atextor.owlcli.diagram.graph.Graph;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;

import javax.annotation.Nonnull;

/**
 * Dispatcher of multiple types of OWL objects; this is called in some generic mapping operations.
 * Maps {@link org.semanticweb.owlapi.model.OWLObject}s to {@link Graph}s.
 */
public class OWLObjectMapper implements OWLObjectVisitorEx<Graph> {
    private final MappingConfiguration mappingConfig;

    public OWLObjectMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectIntersectionOf classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectUnionOf classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectComplementOf classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectSomeValuesFrom classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectAllValuesFrom classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectHasValue classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectMinCardinality classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectExactCardinality classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectMaxCardinality classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectHasSelf classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectOneOf classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataSomeValuesFrom classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataAllValuesFrom classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataHasValue classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataMinCardinality classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataExactCardinality classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataMaxCardinality classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectInverseOf property ) {
        return mappingConfig.getOwlPropertyExpressionMapper().visit( property );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectProperty property ) {
        return mappingConfig.getOwlPropertyExpressionMapper().visit( property );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataProperty property ) {
        return mappingConfig.getOwlPropertyExpressionMapper().visit( property );
    }

    @Override
    public Graph visit( final @Nonnull OWLAnnotationProperty property ) {
        return mappingConfig.getOwlPropertyExpressionMapper().visit( property );
    }

    @Override
    public Graph visit( final @Nonnull OWLClass classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Graph visit( final @Nonnull OWLNamedIndividual individual ) {
        return mappingConfig.getOwlIndividualMapper().visit( individual );
    }

    @Override
    public Graph visit( final OWLLiteral literal ) {
        return mappingConfig.getOwlDataMapper().visit( literal );
    }

    @Override
    public Graph visit( final OWLDatatype datatype ) {
        return mappingConfig.getOwlDataMapper().visit( datatype );
    }

    @Override
    public Graph visit( final OWLDataComplementOf complement ) {
        return mappingConfig.getOwlDataMapper().visit( complement );
    }

    @Override
    public Graph visit( final OWLDataOneOf oneOf ) {
        return mappingConfig.getOwlDataMapper().visit( oneOf );
    }

    @Override
    public Graph visit( final OWLDataIntersectionOf intersection ) {
        return mappingConfig.getOwlDataMapper().visit( intersection );
    }

    @Override
    public Graph visit( final OWLDataUnionOf union ) {
        return mappingConfig.getOwlDataMapper().visit( union );
    }

    @Override
    public Graph visit( final OWLDatatypeRestriction restriction ) {
        return mappingConfig.getOwlDataMapper().visit( restriction );
    }

    @Override
    public Graph visit( final OWLFacetRestriction restriction ) {
        return mappingConfig.getOwlDataMapper().visit( restriction );
    }
}
