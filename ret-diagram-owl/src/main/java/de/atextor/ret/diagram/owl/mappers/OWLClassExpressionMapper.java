/*
 * Copyright 2024 Andreas Textor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.atextor.ret.diagram.owl.mappers;

import de.atextor.ret.diagram.owl.graph.Edge;
import de.atextor.ret.diagram.owl.graph.Graph;
import de.atextor.ret.diagram.owl.graph.GraphElement;
import de.atextor.ret.diagram.owl.graph.Node;
import de.atextor.ret.diagram.owl.graph.node.Class;
import de.atextor.ret.diagram.owl.graph.node.ClosedClass;
import de.atextor.ret.diagram.owl.graph.node.Complement;
import de.atextor.ret.diagram.owl.graph.node.DataExactCardinality;
import de.atextor.ret.diagram.owl.graph.node.DataMaximalCardinality;
import de.atextor.ret.diagram.owl.graph.node.DataMinimalCardinality;
import de.atextor.ret.diagram.owl.graph.node.ExistentialRestriction;
import de.atextor.ret.diagram.owl.graph.node.Intersection;
import de.atextor.ret.diagram.owl.graph.node.ObjectExactCardinality;
import de.atextor.ret.diagram.owl.graph.node.ObjectMaximalCardinality;
import de.atextor.ret.diagram.owl.graph.node.ObjectMinimalCardinality;
import de.atextor.ret.diagram.owl.graph.node.ObjectQualifiedExactCardinality;
import de.atextor.ret.diagram.owl.graph.node.ObjectQualifiedMaximalCardinality;
import de.atextor.ret.diagram.owl.graph.node.ObjectQualifiedMinimalCardinality;
import de.atextor.ret.diagram.owl.graph.node.Self;
import de.atextor.ret.diagram.owl.graph.node.Union;
import de.atextor.ret.diagram.owl.graph.node.UniversalRestriction;
import de.atextor.ret.diagram.owl.graph.node.ValueRestriction;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLQuantifiedDataRestriction;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;
import org.semanticweb.owlapi.model.OWLRestriction;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

/**
 * Maps {@link OWLClassExpression}s to {@link Graph}s
 */
public class OWLClassExpressionMapper implements OWLClassExpressionVisitorEx<Graph> {
    private final MappingConfiguration mappingConfig;

    /**
     * Creates a new OWL axiom mapper from a given mapping config
     *
     * @param mappingConfig the mapping config
     */
    public OWLClassExpressionMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    private Stream<GraphElement> createEdgeToClassExpression( final Node sourceNode,
        final OWLClassExpression classExpression ) {
        final Graph diagramPartsForClassExpression = classExpression.accept( this );
        final Node targetNode = diagramPartsForClassExpression.getNode();
        final Stream<GraphElement> remainingElements = diagramPartsForClassExpression.getOtherElements();
        final Edge operandEdge = new Edge.Plain( Edge.Type.DEFAULT_ARROW, sourceNode, targetNode );

        return Graph.of( sourceNode, remainingElements ).and( targetNode ).and( operandEdge ).toStream();
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectIntersectionOf classExpression ) {
        final Node intersectionNode = new Intersection( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final Stream<GraphElement> remainingElements = classExpression.operands()
            .flatMap( operand -> createEdgeToClassExpression( intersectionNode, operand ) );
        return Graph.of( intersectionNode, remainingElements );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectUnionOf classExpression ) {
        final Node unionNode = new Union( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final Stream<GraphElement> remainingElements = classExpression.operands()
            .flatMap( operand -> createEdgeToClassExpression( unionNode, operand ) );
        return Graph.of( unionNode, remainingElements );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectComplementOf classExpression ) {
        final Node complementNode = new Complement( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final Stream<GraphElement> remainingElements = createEdgeToClassExpression( complementNode, classExpression
            .getOperand() );
        return Graph.of( complementNode, remainingElements );
    }

    private Graph createPropertyAndObjectRangeEdges( final Node restrictionNode,
        final OWLQuantifiedObjectRestriction classExpression ) {
        final OWLClassExpression c = classExpression.getFiller();
        final OWLObjectPropertyExpression r = classExpression.getProperty();
        final Graph cNodeGraph = c.accept( mappingConfig.getOwlClassExpressionMapper() );
        final Graph rNodeGraph = r.accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Edge cEdge = new Edge.Decorated( Edge.Type.DEFAULT_ARROW, restrictionNode, cNodeGraph
            .getNode(), Edge.Decorated.Label.CLASS );
        final Edge rEdge = new Edge.Decorated( Edge.Type.DEFAULT_ARROW, restrictionNode, rNodeGraph
            .getNode(), Edge.Decorated.Label.OBJECT_PROPERTY );

        return Graph.of( restrictionNode ).and( cEdge ).and( rEdge ).and( cNodeGraph ).and( rNodeGraph );
    }

    private Graph createPropertyAndObjectRangeEdges( final Node restrictionNode,
        final OWLQuantifiedDataRestriction classExpression ) {
        final OWLDataRange u = classExpression.getFiller();
        final OWLDataPropertyExpression d = classExpression.getProperty();
        final Graph uNodeGraph = u.accept( mappingConfig.getOwlDataMapper() );
        final Graph dNodeGraph = d.accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Edge uEdge = new Edge.Decorated( Edge.Type.DEFAULT_ARROW, restrictionNode, uNodeGraph
            .getNode(), Edge.Decorated.Label.DATA_RANGE );
        final Edge dEdge = new Edge.Decorated( Edge.Type.DEFAULT_ARROW, restrictionNode, dNodeGraph
            .getNode(), Edge.Decorated.Label.DATA_PROPERTY );
        return Graph.of( restrictionNode ).and( uNodeGraph ).and( dNodeGraph ).and( uEdge ).and( dEdge );
    }

    private Graph createPropertyEdge( final Node restrictionNode, final OWLRestriction classExpression,
        final Edge.Decorated.Label edgeLabel ) {
        final OWLPropertyExpression property = classExpression.getProperty();
        final Graph rNodeGraph = property.accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Edge rEdge = new Edge.Decorated( Edge.Type.DEFAULT_ARROW, restrictionNode, rNodeGraph
            .getNode(), edgeLabel );
        return Graph.of( restrictionNode ).and( rNodeGraph ).and( rEdge );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectSomeValuesFrom classExpression ) {
        final Node restrictionNode = new ExistentialRestriction( mappingConfig.getIdentifierMapper()
            .getSyntheticId() );
        return createPropertyAndObjectRangeEdges( restrictionNode, classExpression );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectAllValuesFrom classExpression ) {
        final Node restrictionNode = new UniversalRestriction( mappingConfig.getIdentifierMapper()
            .getSyntheticId() );
        return createPropertyAndObjectRangeEdges( restrictionNode, classExpression );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectHasValue classExpression ) {
        final Node restrictionNode = new ValueRestriction( mappingConfig.getIdentifierMapper()
            .getSyntheticId() );
        final Graph rNodeGraph = createPropertyEdge( restrictionNode, classExpression,
            Edge.Decorated.Label.OBJECT_PROPERTY );
        final OWLIndividual individual = classExpression.getFiller();
        final Graph oNodeGraph = individual.accept( mappingConfig.getOwlIndividualMapper() );
        final Edge oEdge = new Edge.Decorated( Edge.Type.DEFAULT_ARROW, restrictionNode, oNodeGraph
            .getNode(), Edge.Decorated.Label.INDIVIDUAL );
        return Graph.of( restrictionNode ).and( rNodeGraph ).and( oNodeGraph ).and( oEdge );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectMinCardinality classExpression ) {
        if ( classExpression.isQualified() ) {
            final Node restrictionNode = new ObjectQualifiedMinimalCardinality( mappingConfig
                .getIdentifierMapper().getSyntheticId(), classExpression.getCardinality() );
            return createPropertyAndObjectRangeEdges( restrictionNode, classExpression );
        } else {
            final Node restrictionNode = new ObjectMinimalCardinality( mappingConfig.getIdentifierMapper()
                .getSyntheticId(), classExpression.getCardinality() );
            return createPropertyEdge( restrictionNode, classExpression, Edge.Decorated.Label.OBJECT_PROPERTY );
        }
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectExactCardinality classExpression ) {
        if ( classExpression.isQualified() ) {
            return createPropertyAndObjectRangeEdges( new ObjectQualifiedExactCardinality( mappingConfig
                .getIdentifierMapper().getSyntheticId(), classExpression.getCardinality() ), classExpression );
        } else {
            final Node restrictionNode = new ObjectExactCardinality( mappingConfig.getIdentifierMapper()
                .getSyntheticId(), classExpression.getCardinality() );
            return createPropertyEdge( restrictionNode, classExpression, Edge.Decorated.Label.OBJECT_PROPERTY );
        }
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectMaxCardinality classExpression ) {
        if ( classExpression.isQualified() ) {
            return createPropertyAndObjectRangeEdges( new ObjectQualifiedMaximalCardinality( mappingConfig
                .getIdentifierMapper().getSyntheticId(), classExpression.getCardinality() ), classExpression );
        } else {
            final Node restrictionNode = new ObjectMaximalCardinality( mappingConfig.getIdentifierMapper()
                .getSyntheticId(), classExpression.getCardinality() );
            return createPropertyEdge( restrictionNode, classExpression, Edge.Decorated.Label.OBJECT_PROPERTY );
        }
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectHasSelf classExpression ) {
        final Node restrictionNode = new Self( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return createPropertyEdge( restrictionNode, classExpression, Edge.Decorated.Label.OBJECT_PROPERTY );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectOneOf classExpression ) {
        final Node restrictionNode = new ClosedClass( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return classExpression.individuals().map( individual -> {
            final Graph individualGraph = individual.accept( mappingConfig.getOwlIndividualMapper() );
            final Edge iEdge = new Edge.Plain( Edge.Type.DEFAULT_ARROW, restrictionNode, individualGraph.getNode() );
            return individualGraph.and( iEdge );
        } ).reduce( Graph.of( restrictionNode ), Graph::and );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataSomeValuesFrom classExpression ) {
        final Node restrictionNode = new ExistentialRestriction( mappingConfig.getIdentifierMapper()
            .getSyntheticId() );
        return createPropertyAndObjectRangeEdges( restrictionNode, classExpression );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataAllValuesFrom classExpression ) {
        final Node restrictionNode = new UniversalRestriction( mappingConfig.getIdentifierMapper()
            .getSyntheticId() );
        return createPropertyAndObjectRangeEdges( restrictionNode, classExpression );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataHasValue classExpression ) {
        final Node restrictionNode = new ValueRestriction( mappingConfig.getIdentifierMapper()
            .getSyntheticId() );
        final Graph uNodeGraph = createPropertyEdge( restrictionNode, classExpression,
            Edge.Decorated.Label.DATA_PROPERTY );
        final OWLLiteral literal = classExpression.getFiller();
        final Graph vNodeGraph = literal.accept( mappingConfig.getOwlDataMapper() );
        final Edge vEdge = new Edge.Decorated( Edge.Type.DEFAULT_ARROW, restrictionNode, vNodeGraph
            .getNode(), Edge.Decorated.Label.LITERAL );
        return Graph.of( restrictionNode ).and( uNodeGraph ).and( vNodeGraph ).and( vEdge );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataMinCardinality classExpression ) {
        final Node restrictionNode = new DataMinimalCardinality( mappingConfig.getIdentifierMapper()
            .getSyntheticId(), classExpression.getCardinality() );
        return createPropertyEdge( restrictionNode, classExpression, Edge.Decorated.Label.DATA_PROPERTY );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataExactCardinality classExpression ) {
        final Node restrictionNode = new DataExactCardinality( mappingConfig.getIdentifierMapper()
            .getSyntheticId(), classExpression.getCardinality() );
        return createPropertyEdge( restrictionNode, classExpression, Edge.Decorated.Label.DATA_PROPERTY );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataMaxCardinality classExpression ) {
        final Node restrictionNode = new DataMaximalCardinality( mappingConfig.getIdentifierMapper()
            .getSyntheticId(), classExpression.getCardinality() );
        return createPropertyEdge( restrictionNode, classExpression, Edge.Decorated.Label.DATA_PROPERTY );
    }

    @Override
    public Graph visit( final @Nonnull OWLClass classExpression ) {
        final Node classNode = new Class( mappingConfig.getIdentifierMapper()
            .getIdForIri( classExpression.getIRI() ), mappingConfig.getNameMapper().getName( classExpression ) );
        return Graph.of( classNode );
    }
}
