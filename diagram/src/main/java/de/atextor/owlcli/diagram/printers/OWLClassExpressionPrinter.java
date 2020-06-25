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
import org.semanticweb.owlapi.model.HasCardinality;
import org.semanticweb.owlapi.model.HasFiller;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLRestriction;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

/**
 * Serializes {@link OWLClassExpression}s into expressions
 */
public class OWLClassExpressionPrinter implements OWLClassExpressionVisitorEx<String> {
    private final MappingConfiguration mappingConfig;

    public OWLClassExpressionPrinter( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public String visit( final @Nonnull OWLObjectIntersectionOf classExpression ) {
        return String.format( "and(%s)",
            classExpression.operands().map( operand -> operand.accept( this ) )
                .collect( Collectors.joining( ", " ) ) );
    }

    @Override
    public String visit( final @Nonnull OWLObjectUnionOf classExpression ) {
        return String.format( "or(%s)",
            classExpression.operands().map( operand -> operand.accept( this ) )
                .collect( Collectors.joining( ", " ) ) );
    }

    @Override
    public String visit( final @Nonnull OWLObjectComplementOf classExpression ) {
        return String.format( "not(%s)", classExpression.getOperand().accept( this ) );
    }

    @Override
    public String visit( final @Nonnull OWLObjectSomeValuesFrom classExpression ) {
        return String.format( "%s some %s",
            classExpression.getProperty().accept( mappingConfig.getOwlPropertyExpressionPrinter() ),
            classExpression.getFiller().accept( this ) );
    }

    @Override
    public String visit( final @Nonnull OWLObjectAllValuesFrom classExpression ) {
        return String.format( "%s only %s",
            classExpression.getProperty().accept( mappingConfig.getOwlPropertyExpressionPrinter() ),
            classExpression.getFiller().accept( this ) );
    }

    @Override
    public String visit( final @Nonnull OWLObjectHasValue classExpression ) {
        return String.format( "%s value %s",
            classExpression.getProperty().accept( mappingConfig.getOwlPropertyExpressionPrinter() ),
            classExpression.getFiller().accept( mappingConfig.getOwlIndividualMapper() ) );
    }

    private <T extends HasCardinality & OWLRestriction & HasFiller<OWLClassExpression>>
    String printQualifiedCardinalityRestriction( final T classExpression, final String restrictionType ) {
        return String.format( "%s %s %d %s",
            classExpression.getProperty().accept( mappingConfig.getOwlPropertyExpressionPrinter() ),
            restrictionType,
            classExpression.getCardinality(),
            classExpression.getFiller().accept( this ) );
    }

    private <T extends HasCardinality & OWLRestriction>
    String printUnqualifiedCardinalityRestriction( final T classExpression, final String restrictionType ) {
        return String.format( "%s %s %d",
            classExpression.getProperty().accept( mappingConfig.getOwlPropertyExpressionPrinter() ),
            restrictionType,
            classExpression.getCardinality() );
    }

    @Override
    public String visit( final @Nonnull OWLObjectMinCardinality classExpression ) {
        if ( classExpression.isQualified() ) {
            return printQualifiedCardinalityRestriction( classExpression, "min" );
        }
        return printUnqualifiedCardinalityRestriction( classExpression, "min" );
    }

    @Override
    public String visit( final @Nonnull OWLObjectExactCardinality classExpression ) {
        if ( classExpression.isQualified() ) {
            return printQualifiedCardinalityRestriction( classExpression, "exactly" );
        }
        return printUnqualifiedCardinalityRestriction( classExpression, "exactly" );
    }

    @Override
    public String visit( final @Nonnull OWLObjectMaxCardinality classExpression ) {
        if ( classExpression.isQualified() ) {
            return printQualifiedCardinalityRestriction( classExpression, "max" );
        }
        return printUnqualifiedCardinalityRestriction( classExpression, "max" );
    }

    @Override
    public String visit( final @Nonnull OWLObjectHasSelf classExpression ) {
        return String.format( "%s Self",
            classExpression.getProperty().accept( mappingConfig.getOwlPropertyExpressionPrinter() ) );
    }

    @Override
    public String visit( final @Nonnull OWLObjectOneOf classExpression ) {
        return classExpression.individuals()
            .map( individual -> individual.accept( mappingConfig.getOwlIndividualPrinter() ) )
            .collect( Collectors.joining( ", ", "{", "}" ) );
    }

    @Override
    public String visit( final @Nonnull OWLDataSomeValuesFrom classExpression ) {
        return String.format( "%s some %s",
            classExpression.getProperty().accept( mappingConfig.getOwlPropertyExpressionPrinter() ),
            classExpression.getFiller().accept( mappingConfig.getOwlDataPrinter() ) );
    }

    @Override
    public String visit( final @Nonnull OWLDataAllValuesFrom classExpression ) {
        return String.format( "%s only %s",
            classExpression.getProperty().accept( mappingConfig.getOwlPropertyExpressionPrinter() ),
            classExpression.getFiller().accept( mappingConfig.getOwlDataPrinter() ) );
    }

    @Override
    public String visit( final @Nonnull OWLDataHasValue classExpression ) {
        return String.format( "%s value %s",
            classExpression.getProperty().accept( mappingConfig.getOwlPropertyExpressionPrinter() ),
            classExpression.getFiller().accept( mappingConfig.getOwlDataPrinter() ) );
    }

    @Override
    public String visit( final @Nonnull OWLDataMinCardinality classExpression ) {
        return printUnqualifiedCardinalityRestriction( classExpression, "min" );
    }

    @Override
    public String visit( final @Nonnull OWLDataExactCardinality classExpression ) {
        return printUnqualifiedCardinalityRestriction( classExpression, "exactly" );
    }

    @Override
    public String visit( final @Nonnull OWLDataMaxCardinality classExpression ) {
        return printUnqualifiedCardinalityRestriction( classExpression, "max" );
    }

    @Override
    public String visit( final OWLClass classExpression ) {
        return mappingConfig.getNameMapper().getName( classExpression );
    }
}
