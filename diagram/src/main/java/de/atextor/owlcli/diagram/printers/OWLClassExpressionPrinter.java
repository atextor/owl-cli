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
