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

import com.google.common.collect.Sets;
import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.Graph;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import org.semanticweb.owlapi.model.HasOperands;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNaryAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLPropertyAssertionObject;
import org.semanticweb.owlapi.model.OWLPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import org.semanticweb.owlapi.model.OWLPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vavr.API.TODO;

/**
 * Maps {@link org.semanticweb.owlapi.model.OWLAxiom}s to {@link Graph}s
 */
public class OWLAxiomMapper implements OWLAxiomVisitorEx<Graph> {
    private final MappingConfiguration mappingConfig;

    public OWLAxiomMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public Graph visit( final @Nonnull OWLSubClassOfAxiom axiom ) {
        final OWLClassExpressionVisitorEx<Graph> mapper = mappingConfig.getOwlClassExpressionMapper();

        final Graph superClassGraph = axiom.getSuperClass().accept( mapper );
        final Graph subClassGraph = axiom.getSubClass().accept( mapper );

        final Edge edge = new Edge.Plain( Edge.Type.HOLLOW_ARROW, subClassGraph.getNode().getId(),
            superClassGraph.getNode().getId() );

        return superClassGraph.and( subClassGraph ).and( edge );
    }

    private <P extends OWLPropertyExpression, O extends OWLPropertyAssertionObject> Stream<GraphElement>
    propertyStructure( final OWLPropertyAssertionAxiom<P, O> axiom, final Node thirdNode ) {

        final Graph subjectGraph = axiom.getSubject().accept( mappingConfig.getOwlIndividualMapper() );
        final Graph propertyGraph = axiom.getProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Graph objectGraph = axiom.getObject().accept( mappingConfig.getOwlObjectMapper() );

        final Edge subjectToThirdNode = new Edge.Plain( Edge.Type.NO_ARROW, subjectGraph.getNode().getId(),
            thirdNode.getId() );
        final Edge thirdNodeToObject = new Edge.Plain( Edge.Type.DEFAULT_ARROW, thirdNode.getId(),
            objectGraph.getNode().getId() );
        final Edge thirdNodeToProperty = new Edge.Plain( Edge.Type.DASHED_ARROW, thirdNode.getId(),
            propertyGraph.getNode().getId() );

        return subjectGraph
            .and( propertyGraph )
            .and( objectGraph )
            .and( thirdNode )
            .and( subjectToThirdNode )
            .and( thirdNodeToObject )
            .and( thirdNodeToProperty ).toStream();
    }

    @Override
    public Graph visit( final @Nonnull OWLNegativeObjectPropertyAssertionAxiom axiom ) {
        final Node complement = new Node.Complement( mappingConfig.getIdentifierMapper()
            .getSyntheticId() );
        return Graph.of( complement ).and( propertyStructure( axiom, complement ) );
    }

    @Override
    public Graph visit( final @Nonnull OWLAsymmetricObjectPropertyAxiom axiom ) {
        return propertyMarker( axiom.getProperty(), Node.PropertyMarker.Kind.ASYMMETRIC );
    }

    @Override
    public Graph visit( final @Nonnull OWLReflexiveObjectPropertyAxiom axiom ) {
        return propertyMarker( axiom.getProperty(), Node.PropertyMarker.Kind.REFLEXIVE );
    }

    @Override
    public Graph visit( final @Nonnull OWLDisjointClassesAxiom axiom ) {
        final Node disjointness = new Node.Disjointness( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return linkNodeToMultipleOthers( axiom, disjointness );
    }

    private <P extends OWLPropertyExpression, A extends OWLPropertyDomainAxiom<P>> Graph propertyDomain( final A axiom ) {
        final Graph domainGraph = axiom.getDomain().accept( mappingConfig.getOwlClassExpressionMapper() );
        final Graph propertyGraph = axiom.getProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Edge domainEdge = new Edge.Decorated( Edge.Type.DEFAULT_ARROW, propertyGraph.getNode()
            .getId(), domainGraph.getNode().getId(), Edge.Decorated.DOMAIN_LABEL );
        return domainGraph.and( propertyGraph ).and( domainEdge );
    }

    private <P extends OWLPropertyExpression, R extends OWLPropertyRange, A extends OWLPropertyRangeAxiom<P, R>> Graph propertyRange( final A axiom ) {
        final Graph propertyGraph = axiom.getProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Graph rangeGraph = axiom.getRange().accept( mappingConfig.getOwlObjectMapper() );
        final Edge rangeEdge = new Edge.Decorated( Edge.Type.DEFAULT_ARROW, propertyGraph.getNode()
            .getId(), rangeGraph.getNode().getId(), Edge.Decorated.RANGE_LABEL );
        return propertyGraph.and( rangeGraph ).and( rangeEdge );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataPropertyDomainAxiom axiom ) {
        return propertyDomain( axiom );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectPropertyDomainAxiom axiom ) {
        return propertyDomain( axiom );
    }

    @Override
    public Graph visit( final @Nonnull OWLEquivalentObjectPropertiesAxiom axiom ) {
        return pairwiseEquivalent( axiom, mappingConfig.getOwlObjectMapper() );
    }

    @Override
    public Graph visit( final @Nonnull OWLNegativeDataPropertyAssertionAxiom axiom ) {
        final Node complement = new Node.Complement( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return Graph.of( complement ).and( propertyStructure( axiom, complement ) );
    }

    @Override
    public Graph visit( final @Nonnull OWLDifferentIndividualsAxiom axiom ) {
        final Node inequality = new Node.Inequality( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return linkNodeToMultipleOthers( axiom, inequality );
    }

    @Override
    public Graph visit( final @Nonnull OWLDisjointDataPropertiesAxiom axiom ) {
        final Node disjointness = new Node.Disjointness( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return linkNodeToMultipleOthers( axiom, disjointness );
    }

    @Override
    public Graph visit( final @Nonnull OWLDisjointObjectPropertiesAxiom axiom ) {
        final Node disjointness = new Node.Disjointness( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return linkNodeToMultipleOthers( axiom, disjointness );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectPropertyRangeAxiom axiom ) {
        return propertyRange( axiom );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectPropertyAssertionAxiom axiom ) {
        final Node invisible = new Node.Invisible( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return Graph.of( invisible ).and( propertyStructure( axiom, invisible ) );
    }

    @Override
    public Graph visit( final @Nonnull OWLFunctionalObjectPropertyAxiom axiom ) {
        return propertyMarker( axiom.getProperty(), Node.PropertyMarker.Kind.FUNCTIONAL );
    }

    @Override
    public Graph visit( final @Nonnull OWLSubObjectPropertyOfAxiom axiom ) {
        final OWLPropertyExpressionVisitorEx<Graph> mapper = mappingConfig.getOwlPropertyExpressionMapper();

        final Graph superPropertyGraph = axiom.getSuperProperty().accept( mapper );
        final Graph subPropertyGraph = axiom.getSubProperty().accept( mapper );
        return subProperties( superPropertyGraph, subPropertyGraph );
    }

    private <T extends OWLObject> Graph linkNodeToMultipleOthers( final HasOperands<T> axiom,
                                                                  final Node fromNode ) {
        return axiom.operands().map( operand -> {
            final Graph operandGraph = operand.accept( mappingConfig.getOwlObjectMapper() );
            final Edge fromNodeToOperandEdge = new Edge.Plain( Edge.Type.DEFAULT_ARROW, fromNode.getId(),
                operandGraph.getNode().getId() );
            return operandGraph.and( fromNodeToOperandEdge );
        } ).reduce( Graph.of( fromNode ), Graph::and );
    }

    @Override
    public Graph visit( final @Nonnull OWLDisjointUnionAxiom axiom ) {
        final Node disjointUnion = new Node.DisjointUnion( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final Graph linksGraph = linkNodeToMultipleOthers( axiom, disjointUnion );
        final Graph classGraph = axiom.getOWLClass().accept( mappingConfig.getOwlClassExpressionMapper() );
        final Edge classToDisjointUnion = new Edge.Plain( Edge.Type.HOLLOW_ARROW, classGraph.getNode().getId(),
            disjointUnion.getId() );
        return classGraph.and( linksGraph ).and( classToDisjointUnion );
    }

    @Override
    public Graph visit( final @Nonnull OWLSymmetricObjectPropertyAxiom axiom ) {
        return propertyMarker( axiom.getProperty(), Node.PropertyMarker.Kind.SYMMETRIC );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataPropertyRangeAxiom axiom ) {
        return propertyRange( axiom );
    }

    private Graph propertyMarker( final OWLPropertyExpression propertyExpression,
                                  final Node.PropertyMarker.Kind markerKind ) {
        final Node marker = new Node.PropertyMarker( mappingConfig.getIdentifierMapper().getSyntheticId(),
            Set.of( markerKind ) );
        final Node propertyNode = propertyExpression.accept( mappingConfig.getOwlPropertyExpressionMapper() ).getNode();
        final Edge edge = new Edge.Plain( Edge.Type.DASHED_ARROW, propertyNode.getId(), marker.getId() );
        return Graph.of( marker ).and( propertyNode ).and( edge );
    }

    @Override
    public Graph visit( final @Nonnull OWLFunctionalDataPropertyAxiom axiom ) {
        return propertyMarker( axiom.getProperty(), Node.PropertyMarker.Kind.FUNCTIONAL );
    }

    /**
     * Shared logic for axioms that generate sets of nodes that are pairwise equivalent,
     * e.g. {@link org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom}s.
     *
     * @param axiom   The axiom to generate results for
     * @param visitor The visitor that handles the type of axiom
     * @param <O>     The type of object the axiom describes
     * @param <A>     The axiom type
     * @param <V>     The type of visitor that handles the axiom type
     * @return the graph representing the equivalency
     */
    private <O extends OWLObject, A extends OWLNaryAxiom<O>, V extends OWLObjectVisitorEx<Graph>>
    Graph pairwiseEquivalent( final A axiom, final V visitor ) {

        final Map<O, Graph> operands = axiom.operands().collect( Collectors.toMap( Function.identity(),
            object -> object.accept( visitor ) ) );

        // Create all combinations of operands, but (1) keep every combination only once,
        // regardless of direction and (2) remove those combinations where both elements are the same
        final Set<List<O>> combinations = Sets.cartesianProduct( Arrays.asList( operands.keySet(),
            operands.keySet() ) ).stream().map( expressionsList -> {
                final List<O> newList = new ArrayList<>( expressionsList );
                newList.sort( Comparator.comparing( o -> operands.get( o ).getNode().getId().getId() ) );
                return newList;
            }
        ).filter( expressionsList -> {
            final Iterator<O> iterator = expressionsList.iterator();
            return !iterator.next().equals( iterator.next() );
        } ).collect( Collectors.toSet() );

        // For each of the combinations, create a corresponding edge
        final Stream<GraphElement> edges = combinations.stream().map( expressionsList -> {
            final Iterator<O> iterator = expressionsList.iterator();
            final Graph graph1 = operands.get( iterator.next() );
            final Graph graph2 = operands.get( iterator.next() );
            return new Edge.Plain( Edge.Type.DOUBLE_ENDED_HOLLOW_ARROW, graph1.getNode().getId(),
                graph2.getNode().getId() );
        } );

        final Node firstOperand = operands.values().iterator().next().getNode();
        return operands.values().stream().reduce( Graph.of( firstOperand ), Graph::and ).and( edges );
    }

    @Override
    public Graph visit( final @Nonnull OWLEquivalentDataPropertiesAxiom axiom ) {
        return pairwiseEquivalent( axiom, mappingConfig.getOwlObjectMapper() );
    }

    @Override
    public Graph visit( final @Nonnull OWLClassAssertionAxiom axiom ) {
        final OWLIndividual individual = axiom.getIndividual();
        final OWLClassExpression classExpression = axiom.getClassExpression();
        final Graph individualGraph = individual.accept( mappingConfig.getOwlIndividualMapper() );
        final Graph classExpressionGraph =
            classExpression.accept( mappingConfig.getOwlClassExpressionMapper() );

        final Edge edge = new Edge.Plain( Edge.Type.DEFAULT_ARROW, individualGraph.getNode().getId(),
            classExpressionGraph.getNode().getId() );
        return individualGraph.and( classExpressionGraph ).and( edge );
    }

    @Override
    public Graph visit( final @Nonnull OWLEquivalentClassesAxiom axiom ) {
        return pairwiseEquivalent( axiom, mappingConfig.getOwlObjectMapper() );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataPropertyAssertionAxiom axiom ) {
        final Node invisible = new Node.Invisible( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return Graph.of( invisible ).and( propertyStructure( axiom, invisible ) );
    }

    @Override
    public Graph visit( final @Nonnull OWLTransitiveObjectPropertyAxiom axiom ) {
        return propertyMarker( axiom.getProperty(), Node.PropertyMarker.Kind.TRANSITIVE );
    }

    @Override
    public Graph visit( final @Nonnull OWLIrreflexiveObjectPropertyAxiom axiom ) {
        return propertyMarker( axiom.getProperty(), Node.PropertyMarker.Kind.IRREFLEXIVE );
    }

    private Graph subProperties( final Graph superPropertyGraph, final Graph subPropertyGraph ) {
        final Edge edge = new Edge.Plain( Edge.Type.HOLLOW_ARROW, subPropertyGraph.getNode().getId(),
            superPropertyGraph.getNode().getId() );
        return superPropertyGraph.and( subPropertyGraph ).and( edge );
    }

    @Override
    public Graph visit( final @Nonnull OWLSubDataPropertyOfAxiom axiom ) {
        final OWLPropertyExpressionVisitorEx<Graph> mapper = mappingConfig.getOwlPropertyExpressionMapper();

        final Graph superPropertyGraph = axiom.getSuperProperty().accept( mapper );
        final Graph subPropertyGraph = axiom.getSubProperty().accept( mapper );
        return subProperties( superPropertyGraph, subPropertyGraph );
    }

    @Override
    public Graph visit( final @Nonnull OWLInverseFunctionalObjectPropertyAxiom axiom ) {
        return propertyMarker( axiom.getProperty(), Node.PropertyMarker.Kind.INVERSE_FUNCTIONAL );
    }

    @Override
    public Graph visit( final @Nonnull OWLSameIndividualAxiom axiom ) {
        final Node equality = new Node.Equality( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return linkNodeToMultipleOthers( axiom, equality );
    }

    @Override
    public Graph visit( final @Nonnull OWLSubPropertyChainOfAxiom axiom ) {
        final OWLPropertyExpressionVisitorEx<Graph> mapper = mappingConfig.getOwlPropertyExpressionMapper();

        final Set<Node> chainLinks = axiom.getPropertyChain().stream()
            .map( expression -> expression.accept( mapper ).getNode() )
            .collect( Collectors.toSet() );
        final String value = chainLinks.stream()
            .flatMap( node -> node.getId().getIri().stream() )
            .map( iri -> mappingConfig.getNameMapper().getName( iri ) )
            .collect( Collectors.joining( " " + Node.PropertyChain.OPERATOR_SYMBOL + " " ) );
        final Node propertyChain = new Node.PropertyChain( mappingConfig.getIdentifierMapper()
            .getSyntheticId(), value );

        final Graph chainGraph = chainLinks.stream().map( chainLink -> {
            final Edge fromPropertyChainToChainLink = new Edge.Plain( Edge.Type.DEFAULT_ARROW, propertyChain
                .getId(),
                chainLink.getId() );
            return Graph.of( chainLink ).and( fromPropertyChainToChainLink );
        } ).reduce( Graph.of( propertyChain ), Graph::and );

        final Node property = axiom.getSuperProperty().accept( mapper ).getNode();
        final Edge propertyToPropertyChain = new Edge.Plain( Edge.Type.HOLLOW_ARROW, property.getId(), propertyChain
            .getId() );
        return Graph.of( property ).and( propertyToPropertyChain ).and( chainGraph );
    }

    @Override
    public Graph visit( final @Nonnull OWLInverseObjectPropertiesAxiom axiom ) {
        final Node equality = new Node.Inverse( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return linkNodeToMultipleOthers( axiom, equality );
    }

    @Override
    public Graph visit( final @Nonnull OWLHasKeyAxiom axiom ) {
        return TODO();
    }

    @Override
    public Graph visit( final @Nonnull OWLDeclarationAxiom axiom ) {
        return axiom.getEntity().accept( mappingConfig.getOwlEntityMapper() );
    }

    @Override
    public Graph visit( final @Nonnull OWLDatatypeDefinitionAxiom axiom ) {
        return TODO();
    }

    @Override
    public Graph visit( final @Nonnull OWLAnnotationAssertionAxiom axiom ) {
        final Graph subjectGraph = axiom.getSubject().accept( mappingConfig.getOwlAnnotationSubjectMapper() );
        final Graph propertyGraph = axiom.getProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Graph objectGraph = axiom.getValue().accept( mappingConfig.getOwlAnnotationObjectMapper() );

        final Node invisible = new Node.Invisible( mappingConfig.getIdentifierMapper()
            .getSyntheticId() );

        final Edge subjectToInvisible = new Edge.Plain( Edge.Type.NO_ARROW, subjectGraph.getNode().getId(),
            invisible.getId() );
        final Edge invisibleToObject = new Edge.Plain( Edge.Type.DEFAULT_ARROW, invisible.getId(),
            objectGraph.getNode().getId() );
        final Edge invisibleToProperty = new Edge.Plain( Edge.Type.DASHED_ARROW, invisible.getId(),
            propertyGraph.getNode().getId() );

        return subjectGraph
            .and( propertyGraph )
            .and( objectGraph )
            .and( invisible )
            .and( subjectToInvisible )
            .and( invisibleToObject )
            .and( invisibleToProperty );
    }

    @Override
    public Graph visit( final @Nonnull OWLSubAnnotationPropertyOfAxiom axiom ) {
        final OWLPropertyExpressionVisitorEx<Graph> mapper = mappingConfig.getOwlPropertyExpressionMapper();
        final Graph superPropertyGraph = axiom.getSuperProperty().accept( mapper );
        final Graph subPropertyGraph = axiom.getSubProperty().accept( mapper );
        return subProperties( superPropertyGraph, subPropertyGraph );
    }

    @Override
    public Graph visit( final @Nonnull OWLAnnotationPropertyDomainAxiom axiom ) {
        final Node.IRIReference iriReference = new Node.IRIReference(
            mappingConfig.getIdentifierMapper().getSyntheticId(), axiom.getDomain() );
        final Graph propertyGraph = axiom.getProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Edge domainEdge = new Edge.Decorated( Edge.Type.DEFAULT_ARROW, propertyGraph.getNode().getId(),
            iriReference.getId(), Edge.Decorated.DOMAIN_LABEL );
        return propertyGraph.and( iriReference ).and( domainEdge );
    }

    @Override
    public Graph visit( final @Nonnull OWLAnnotationPropertyRangeAxiom axiom ) {
        final Node.IRIReference iriReference = new Node.IRIReference(
            mappingConfig.getIdentifierMapper().getSyntheticId(), axiom.getRange() );
        final Graph propertyGraph = axiom.getProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Edge domainEdge = new Edge.Decorated( Edge.Type.DEFAULT_ARROW, propertyGraph.getNode().getId(),
            iriReference.getId(), Edge.Decorated.RANGE_LABEL );
        return propertyGraph.and( iriReference ).and( domainEdge );
    }

    @Override
    public Graph visit( final @Nonnull SWRLRule node ) {
        return TODO();
    }

    @Override
    public <T> Graph doDefault( final T object ) {
        return TODO();
    }
}
