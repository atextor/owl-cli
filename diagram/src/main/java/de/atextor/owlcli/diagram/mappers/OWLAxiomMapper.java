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

package de.atextor.owlcli.diagram.mappers;

import com.google.common.collect.Sets;
import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.Graph;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.node.Complement;
import de.atextor.owlcli.diagram.graph.node.DisjointUnion;
import de.atextor.owlcli.diagram.graph.node.Disjointness;
import de.atextor.owlcli.diagram.graph.node.Equality;
import de.atextor.owlcli.diagram.graph.node.IRIReference;
import de.atextor.owlcli.diagram.graph.node.Inequality;
import de.atextor.owlcli.diagram.graph.node.Inverse;
import de.atextor.owlcli.diagram.graph.node.Invisible;
import de.atextor.owlcli.diagram.graph.node.Key;
import de.atextor.owlcli.diagram.graph.node.Literal;
import de.atextor.owlcli.diagram.graph.node.PropertyChain;
import de.atextor.owlcli.diagram.graph.node.PropertyMarker;
import de.atextor.owlcli.diagram.graph.node.Rule;
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

        final Edge edge = new Edge.Plain( Edge.Type.HOLLOW_ARROW, subClassGraph.getNode(), superClassGraph.getNode() );
        return superClassGraph.and( subClassGraph ).and( edge );
    }

    private <P extends OWLPropertyExpression, O extends OWLPropertyAssertionObject> Stream<GraphElement>
    propertyStructure( final OWLPropertyAssertionAxiom<P, O> axiom, final Node thirdNode,
                       final Edge.Type subjectToThirdNodeType ) {

        final Graph subjectGraph = axiom.getSubject().accept( mappingConfig.getOwlIndividualMapper() );
        final Graph propertyGraph = axiom.getProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Graph objectGraph = axiom.getObject().accept( mappingConfig.getOwlObjectMapper() );

        final Edge subjectToThirdNode = new Edge.Plain( subjectToThirdNodeType, subjectGraph.getNode(), thirdNode );
        final Edge thirdNodeToObject = new Edge.Plain( Edge.Type.DEFAULT_ARROW, thirdNode, objectGraph.getNode() );
        final Edge thirdNodeToProperty = new Edge.Plain( Edge.Type.DASHED_ARROW, thirdNode, propertyGraph.getNode() );

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
        final Node complement = new Complement( mappingConfig.getIdentifierMapper()
            .getSyntheticId() );
        return Graph.of( complement ).and( propertyStructure( axiom, complement, Edge.Type.DEFAULT_ARROW ) );
    }

    @Override
    public Graph visit( final @Nonnull OWLAsymmetricObjectPropertyAxiom axiom ) {
        return propertyMarker( axiom.getProperty(), PropertyMarker.Kind.ASYMMETRIC );
    }

    @Override
    public Graph visit( final @Nonnull OWLReflexiveObjectPropertyAxiom axiom ) {
        return propertyMarker( axiom.getProperty(), PropertyMarker.Kind.REFLEXIVE );
    }

    @Override
    public Graph visit( final @Nonnull OWLDisjointClassesAxiom axiom ) {
        final Node disjointness = new Disjointness( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return linkNodeToMultipleOthers( axiom, disjointness );
    }

    private <P extends OWLPropertyExpression, A extends OWLPropertyDomainAxiom<P>> Graph propertyDomain( final A axiom ) {
        final Graph domainGraph = axiom.getDomain().accept( mappingConfig.getOwlClassExpressionMapper() );
        final Graph propertyGraph = axiom.getProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Edge domainEdge = new Edge.Decorated( Edge.Type.DEFAULT_ARROW, propertyGraph.getNode(),
            domainGraph.getNode(), Edge.Decorated.Label.DOMAIN );
        return domainGraph.and( propertyGraph ).and( domainEdge );
    }

    private <P extends OWLPropertyExpression, R extends OWLPropertyRange, A extends OWLPropertyRangeAxiom<P, R>> Graph propertyRange( final A axiom ) {
        final Graph propertyGraph = axiom.getProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Graph rangeGraph = axiom.getRange().accept( mappingConfig.getOwlObjectMapper() );
        final Edge rangeEdge = new Edge.Decorated( Edge.Type.DEFAULT_ARROW, propertyGraph.getNode(),
            rangeGraph.getNode(), Edge.Decorated.Label.RANGE );
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
        final Node complement = new Complement( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return Graph.of( complement ).and( propertyStructure( axiom, complement, Edge.Type.DEFAULT_ARROW ) );
    }

    @Override
    public Graph visit( final @Nonnull OWLDifferentIndividualsAxiom axiom ) {
        final Node inequality = new Inequality( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return linkNodeToMultipleOthers( axiom, inequality );
    }

    @Override
    public Graph visit( final @Nonnull OWLDisjointDataPropertiesAxiom axiom ) {
        final Node disjointness = new Disjointness( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return linkNodeToMultipleOthers( axiom, disjointness );
    }

    @Override
    public Graph visit( final @Nonnull OWLDisjointObjectPropertiesAxiom axiom ) {
        final Node disjointness = new Disjointness( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return linkNodeToMultipleOthers( axiom, disjointness );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectPropertyRangeAxiom axiom ) {
        return propertyRange( axiom );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectPropertyAssertionAxiom axiom ) {
        final Node invisible = new Invisible( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return Graph.of( invisible ).and( propertyStructure( axiom, invisible, Edge.Type.NO_ARROW ) );
    }

    @Override
    public Graph visit( final @Nonnull OWLFunctionalObjectPropertyAxiom axiom ) {
        return propertyMarker( axiom.getProperty(), PropertyMarker.Kind.FUNCTIONAL );
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
            final Edge fromNodeToOperandEdge = new Edge.Plain( Edge.Type.DEFAULT_ARROW, fromNode, operandGraph
                .getNode() );
            return operandGraph.and( fromNodeToOperandEdge );
        } ).reduce( Graph.of( fromNode ), Graph::and );
    }

    @Override
    public Graph visit( final @Nonnull OWLDisjointUnionAxiom axiom ) {
        final Node disjointUnion = new DisjointUnion( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final Graph linksGraph = linkNodeToMultipleOthers( axiom, disjointUnion );
        final Graph classGraph = axiom.getOWLClass().accept( mappingConfig.getOwlClassExpressionMapper() );
        final Edge classToDisjointUnion = new Edge.Plain( Edge.Type.DOUBLE_ENDED_HOLLOW_ARROW, classGraph
            .getNode(), disjointUnion );
        return classGraph.and( linksGraph ).and( classToDisjointUnion );
    }

    @Override
    public Graph visit( final @Nonnull OWLSymmetricObjectPropertyAxiom axiom ) {
        return propertyMarker( axiom.getProperty(), PropertyMarker.Kind.SYMMETRIC );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataPropertyRangeAxiom axiom ) {
        return propertyRange( axiom );
    }

    private Graph propertyMarker( final OWLPropertyExpression propertyExpression,
                                  final PropertyMarker.Kind markerKind ) {
        final Node marker = new PropertyMarker( mappingConfig.getIdentifierMapper().getSyntheticId(),
            Set.of( markerKind ) );
        final Node propertyNode = propertyExpression.accept( mappingConfig.getOwlPropertyExpressionMapper() ).getNode();
        final Edge edge = new Edge.Plain( Edge.Type.DASHED_ARROW, propertyNode, marker );
        return Graph.of( marker ).and( propertyNode ).and( edge );
    }

    @Override
    public Graph visit( final @Nonnull OWLFunctionalDataPropertyAxiom axiom ) {
        return propertyMarker( axiom.getProperty(), PropertyMarker.Kind.FUNCTIONAL );
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
            return new Edge.Plain( Edge.Type.DOUBLE_ENDED_HOLLOW_ARROW, graph1.getNode(), graph2.getNode() );
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

        final Edge edge = new Edge.Plain( Edge.Type.DEFAULT_ARROW, individualGraph.getNode(),
            classExpressionGraph.getNode() );
        return individualGraph.and( classExpressionGraph ).and( edge );
    }

    @Override
    public Graph visit( final @Nonnull OWLEquivalentClassesAxiom axiom ) {
        return pairwiseEquivalent( axiom, mappingConfig.getOwlObjectMapper() );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataPropertyAssertionAxiom axiom ) {
        final Node invisible = new Invisible( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return Graph.of( invisible ).and( propertyStructure( axiom, invisible, Edge.Type.NO_ARROW ) );
    }

    @Override
    public Graph visit( final @Nonnull OWLTransitiveObjectPropertyAxiom axiom ) {
        return propertyMarker( axiom.getProperty(), PropertyMarker.Kind.TRANSITIVE );
    }

    @Override
    public Graph visit( final @Nonnull OWLIrreflexiveObjectPropertyAxiom axiom ) {
        return propertyMarker( axiom.getProperty(), PropertyMarker.Kind.IRREFLEXIVE );
    }

    private Graph subProperties( final Graph superPropertyGraph, final Graph subPropertyGraph ) {
        final Edge edge = new Edge.Plain( Edge.Type.HOLLOW_ARROW, subPropertyGraph.getNode(),
            superPropertyGraph.getNode() );
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
        return propertyMarker( axiom.getProperty(), PropertyMarker.Kind.INVERSE_FUNCTIONAL );
    }

    @Override
    public Graph visit( final @Nonnull OWLSameIndividualAxiom axiom ) {
        final Node equality = new Equality( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return linkNodeToMultipleOthers( axiom, equality );
    }

    @Override
    public Graph visit( final @Nonnull OWLSubPropertyChainOfAxiom axiom ) {
        final OWLPropertyExpressionVisitorEx<Graph> mapper = mappingConfig.getOwlPropertyExpressionMapper();

        final List<Node> chainLinks = axiom.getPropertyChain().stream()
            .map( expression -> expression.accept( mapper ).getNode() )
            .collect( Collectors.toList() );
        final String value = chainLinks.stream()
            .flatMap( node -> node.getId().getIri().stream() )
            .map( iri -> mappingConfig.getNameMapper().getName( iri ) )
            .collect( Collectors.joining( " " + PropertyChain.OPERATOR_SYMBOL + " " ) );
        final Node propertyChain = new PropertyChain( mappingConfig.getIdentifierMapper()
            .getSyntheticId(), value );

        final Graph chainGraph = chainLinks.stream().map( chainLink -> {
            final Edge fromPropertyChainToChainLink = new Edge.Plain( Edge.Type.DEFAULT_ARROW, propertyChain,
                chainLink );
            return Graph.of( chainLink ).and( fromPropertyChainToChainLink );
        } ).reduce( Graph.of( propertyChain ), Graph::and );

        final Node property = axiom.getSuperProperty().accept( mapper ).getNode();
        final Edge propertyToPropertyChain = new Edge.Plain( Edge.Type.HOLLOW_ARROW, property, propertyChain );
        return Graph.of( property ).and( propertyToPropertyChain ).and( chainGraph );
    }

    @Override
    public Graph visit( final @Nonnull OWLInverseObjectPropertiesAxiom axiom ) {
        final Node equality = new Inverse( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return linkNodeToMultipleOthers( axiom, equality );
    }

    @Override
    public Graph visit( final @Nonnull OWLHasKeyAxiom axiom ) {
        final Graph classResult = axiom.getClassExpression().accept( mappingConfig.getOwlClassExpressionMapper() );
        final Node key = new Key( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final Edge fromClassToKey = new Edge.Plain( Edge.Type.DEFAULT_ARROW, classResult.getNode(), key );
        return linkNodeToMultipleOthers( axiom, key ).and( classResult ).and( fromClassToKey );
    }

    @Override
    public Graph visit( final @Nonnull OWLDeclarationAxiom axiom ) {
        return axiom.getEntity().accept( mappingConfig.getOwlEntityMapper() );
    }

    @Override
    public Graph visit( final @Nonnull OWLDatatypeDefinitionAxiom axiom ) {
        final Graph dataTypeGraph = axiom.getDatatype().accept( mappingConfig.getOwlEntityMapper() );
        final Graph dataRangeGraph = axiom.getDataRange().accept( mappingConfig.getOwlDataMapper() );
        final Edge edge = new Edge.Plain( Edge.Type.HOLLOW_ARROW, dataTypeGraph.getNode(), dataRangeGraph.getNode() );
        return dataTypeGraph.and( dataRangeGraph ).and( edge );
    }

    @Override
    public Graph visit( final @Nonnull OWLAnnotationAssertionAxiom axiom ) {
        final Graph subjectGraph = axiom.getSubject().accept( mappingConfig.getOwlAnnotationSubjectMapper() );
        final Graph propertyGraph = axiom.getProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Graph objectGraph = axiom.getValue().accept( mappingConfig.getOwlAnnotationObjectMapper() );

        final Node invisible = new Invisible( mappingConfig.getIdentifierMapper()
            .getSyntheticId() );

        final Edge subjectToInvisible = new Edge.Plain( Edge.Type.NO_ARROW, subjectGraph.getNode(), invisible );
        final Edge invisibleToObject = new Edge.Plain( Edge.Type.DEFAULT_ARROW, invisible, objectGraph.getNode() );
        final Edge invisibleToProperty = new Edge.Plain( Edge.Type.DASHED_ARROW, invisible, propertyGraph.getNode() );

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
        final IRIReference iriReference = new IRIReference(
            mappingConfig.getIdentifierMapper().getSyntheticId(), axiom.getDomain() );
        final Graph propertyGraph = axiom.getProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Edge domainEdge = new Edge.Decorated( Edge.Type.DEFAULT_ARROW, propertyGraph.getNode(),
            iriReference, Edge.Decorated.Label.DOMAIN );
        return propertyGraph.and( iriReference ).and( domainEdge );
    }

    @Override
    public Graph visit( final @Nonnull OWLAnnotationPropertyRangeAxiom axiom ) {
        final IRIReference iriReference = new IRIReference(
            mappingConfig.getIdentifierMapper().getSyntheticId(), axiom.getRange() );
        final Graph propertyGraph = axiom.getProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Edge domainEdge = new Edge.Decorated( Edge.Type.DEFAULT_ARROW, propertyGraph
            .getNode(), iriReference, Edge.Decorated.Label.RANGE );
        return propertyGraph.and( iriReference ).and( domainEdge );
    }

    @Override
    public Graph visit( final @Nonnull SWRLRule rule ) {
        final Function<Stream<GraphElement>, String> reduceWithConjuction = stream ->
            stream.map( element -> element.as( Literal.class ) )
                .map( Literal::getValue )
                .collect( Collectors.joining( " " + Rule.CONJUNCTION_SYMBOL + " " ) );

        final Map<Boolean, List<GraphElement>> partitionedBodyElements =
            rule.body().flatMap( atom -> atom.accept( mappingConfig.getSwrlObjectMapper() ).toStream() )
                .collect( Collectors.partitioningBy( SWRLObjectMapper.IS_RULE_SYNTAX_PART ) );

        final String bodyExpression = reduceWithConjuction.apply( partitionedBodyElements.get( true ).stream() );

        final Map<Boolean, List<GraphElement>> partitionedHeadElements =
            rule.head().flatMap( atom -> atom.accept( mappingConfig.getSwrlObjectMapper() ).toStream() )
                .collect( Collectors.partitioningBy( SWRLObjectMapper.IS_RULE_SYNTAX_PART ) );

        final String headExpression = reduceWithConjuction.apply( partitionedHeadElements.get( true ).stream() );

        final Node ruleNode = new Rule( mappingConfig.getIdentifierMapper().getSyntheticId(),
            String.format( "%s %s %s", bodyExpression, Rule.IMPLICATION_SYMBOL, headExpression ) );

        final Set<Node> allSyntaxParts =
            Stream.of( partitionedBodyElements.get( true ), partitionedHeadElements.get( true ) )
                .flatMap( List::stream )
                .map( GraphElement::asNode )
                .collect( Collectors.toSet() );

        final Stream<GraphElement> remainingElements =
            Stream.of( partitionedBodyElements.get( false ), partitionedHeadElements.get( false ) )
                .flatMap( List::stream )
                .map( element -> {
                    if ( element.isEdge() ) {
                        final Edge edge = element.asEdge();
                        if ( allSyntaxParts.contains( edge.getFrom() ) ) {
                            return edge.setFrom( ruleNode );
                        }
                    }
                    return element;
                } );

        return Graph.of( ruleNode, remainingElements );
    }

    @Override
    public <T> Graph doDefault( final T object ) {
        return TODO();
    }
}
