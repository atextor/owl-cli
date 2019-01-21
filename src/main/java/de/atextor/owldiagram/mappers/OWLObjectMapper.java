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
    @Override
    public Result visit( final OWLObjectIntersectionOf classExpression ) {
        return Mappers.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLObjectUnionOf classExpression ) {
        return Mappers.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLObjectComplementOf classExpression ) {
        return Mappers.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLObjectSomeValuesFrom classExpression ) {
        return Mappers.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLObjectAllValuesFrom classExpression ) {
        return Mappers.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLObjectHasValue classExpression ) {
        return Mappers.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLObjectMinCardinality classExpression ) {
        return Mappers.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLObjectExactCardinality classExpression ) {
        return Mappers.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLObjectMaxCardinality classExpression ) {
        return Mappers.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLObjectHasSelf classExpression ) {
        return Mappers.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLObjectOneOf classExpression ) {
        return Mappers.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLDataSomeValuesFrom classExpression ) {
        return Mappers.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLDataAllValuesFrom classExpression ) {
        return Mappers.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLDataHasValue classExpression ) {
        return Mappers.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLDataMinCardinality classExpression ) {
        return Mappers.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLDataExactCardinality classExpression ) {
        return Mappers.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLDataMaxCardinality classExpression ) {
        return Mappers.getOwlClassExpressionMapper().visit( classExpression );
    }

    @Override
    public Result visit( final OWLObjectInverseOf property ) {
        return Mappers.getOwlPropertyExpressionMapper().visit( property );
    }

    @Override
    public Result visit( final OWLObjectProperty property ) {
        return Mappers.getOwlPropertyExpressionMapper().visit( property );
    }

    @Override
    public Result visit( final OWLDataProperty property ) {
        return Mappers.getOwlPropertyExpressionMapper().visit( property );
    }

    @Override
    public Result visit( final OWLAnnotationProperty property ) {
        return Mappers.getOwlPropertyExpressionMapper().visit( property );
    }

    @Override
    public Result visit( final OWLClass classExpression ) {
        return Mappers.getOwlClassExpressionMapper().visit( classExpression );
    }
}
