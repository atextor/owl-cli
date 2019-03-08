package de.atextor.owldiagram.mappers;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
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

public class OWLObjectMapper implements OWLObjectVisitorEx<Result> {
    private MappingConfiguration mappingConfig;

    public OWLObjectMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public Result visit( final OWLObjectIntersectionOf classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLObjectUnionOf classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLObjectComplementOf classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLObjectSomeValuesFrom classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLObjectAllValuesFrom classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLObjectHasValue classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLObjectMinCardinality classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLObjectExactCardinality classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLObjectMaxCardinality classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLObjectHasSelf classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLObjectOneOf classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLDataSomeValuesFrom classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLDataAllValuesFrom classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLDataHasValue classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLDataMinCardinality classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLDataExactCardinality classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLDataMaxCardinality classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLObjectInverseOf property ) {
        return mappingConfig.getOwlPropertyExpressionMapper().visit( property );
    }

    @Override
    public Result visit( final OWLObjectProperty property ) {
        return mappingConfig.getOwlPropertyExpressionMapper().visit( property );
    }

    @Override
    public Result visit( final OWLDataProperty property ) {
        return mappingConfig.getOwlPropertyExpressionMapper().visit( property );
    }

    @Override
    public Result visit( final OWLAnnotationProperty property ) {
        return mappingConfig.getOwlPropertyExpressionMapper().visit( property );
    }

    @Override
    public Result visit( final OWLClass classExpression ) {
        return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
    }
}
