package de.atextor.owlcli.diagram.mappers;

import de.atextor.owlcli.diagram.graph.Graph;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
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
}
