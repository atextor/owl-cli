package de.atextor.owlcli.diagram.mappers;

import de.atextor.owlcli.diagram.graph.DecoratedEdge;
import de.atextor.owlcli.diagram.graph.Decoration;
import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.NodeType;
import de.atextor.owlcli.diagram.graph.PlainEdge;
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

public class OWLClassExpressionMapper implements OWLClassExpressionVisitorEx<Result> {
    private final MappingConfiguration mappingConfig;

    public OWLClassExpressionMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    private Stream<GraphElement> createEdgeToClassExpression( final Node sourceNode,
                                                              final OWLClassExpression classExpression ) {
        final Result diagramPartsForClassExpression = classExpression.accept( this );
        final Node targetNode = diagramPartsForClassExpression.getNode();
        final Stream<GraphElement> remainingElements = diagramPartsForClassExpression.getRemainingElements();
        final Node.Id from = sourceNode.getId();
        final Node.Id to = targetNode.getId();
        final Edge operandEdge = new PlainEdge( Edge.Type.DEFAULT_ARROW, from, to );

        return Result.of( sourceNode ).and( targetNode ).and( operandEdge ).and( remainingElements ).toStream();
    }

    @Override
    public Result visit( final @Nonnull OWLObjectIntersectionOf classExpression ) {
        final Node intersectionNode = new NodeType.Intersection( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final Stream<GraphElement> remainingElements = classExpression.operands()
            .flatMap( operand -> createEdgeToClassExpression( intersectionNode, operand ) );
        return new Result( intersectionNode, remainingElements );
    }

    @Override
    public Result visit( final @Nonnull OWLObjectUnionOf classExpression ) {
        final Node unionNode = new NodeType.Union( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final Stream<GraphElement> remainingElements = classExpression.operands()
            .flatMap( operand -> createEdgeToClassExpression( unionNode, operand ) );
        return new Result( unionNode, remainingElements );
    }

    @Override
    public Result visit( final @Nonnull OWLObjectComplementOf classExpression ) {
        final Node complementNode = new NodeType.Complement( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final Stream<GraphElement> remainingElements = createEdgeToClassExpression( complementNode, classExpression
            .getOperand() );
        return new Result( complementNode, remainingElements );
    }

    private Result createPropertyAndObjectRangeEdges( final Node restrictionNode,
                                                      final OWLQuantifiedObjectRestriction classExpression ) {
        final OWLClassExpression c = classExpression.getFiller();
        final OWLObjectPropertyExpression r = classExpression.getProperty();
        final Result cNodeResult = c.accept( mappingConfig.getOwlClassExpressionMapper() );
        final Result rNodeResult = r.accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Edge cEdge = new DecoratedEdge( Edge.Type.DEFAULT_ARROW, restrictionNode.getId(), cNodeResult.getNode()
            .getId(), DecoratedEdge.CLASS );
        final Edge rEdge = new DecoratedEdge( Edge.Type.DEFAULT_ARROW, restrictionNode.getId(), rNodeResult.getNode()
            .getId(), DecoratedEdge.ABSTRACT_ROLE );

        return Result.of( restrictionNode ).and( cEdge ).and( rEdge ).and( cNodeResult ).and( rNodeResult );
    }

    private Result createPropertyAndObjectRangeEdges( final Node restrictionNode,
                                                      final OWLQuantifiedDataRestriction classExpression ) {
        final OWLDataRange u = classExpression.getFiller();
        final OWLDataPropertyExpression d = classExpression.getProperty();
        final Result uNodeResult = u.accept( mappingConfig.getOwlDataMapper() );
        final Result dNodeResult = d.accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Edge uEdge = new DecoratedEdge( Edge.Type.DEFAULT_ARROW, restrictionNode.getId(), uNodeResult.getNode()
            .getId(), DecoratedEdge.DATA_RANGE );
        final Edge dEdge = new DecoratedEdge( Edge.Type.DEFAULT_ARROW, restrictionNode.getId(), dNodeResult.getNode()
            .getId(), DecoratedEdge.CONCRETE_ROLE );
        return Result.of( restrictionNode ).and( uNodeResult ).and( dNodeResult ).and( uEdge ).and( dEdge );
    }

    private Result createPropertyEdge( final Node restrictionNode, final OWLRestriction classExpression,
                                       final Decoration edgeDecoration ) {
        final OWLPropertyExpression property = classExpression.getProperty();
        final Result rNodeResult = property.accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Edge rEdge = new DecoratedEdge( Edge.Type.DEFAULT_ARROW, restrictionNode.getId(), rNodeResult.getNode()
            .getId(), edgeDecoration );
        return Result.of( restrictionNode ).and( rNodeResult ).and( rEdge );
    }

    @Override
    public Result visit( final @Nonnull OWLObjectSomeValuesFrom classExpression ) {
        final Node restrictionNode = new NodeType.ExistentialRestriction( mappingConfig.getIdentifierMapper()
            .getSyntheticId() );
        return createPropertyAndObjectRangeEdges( restrictionNode, classExpression );
    }

    @Override
    public Result visit( final @Nonnull OWLObjectAllValuesFrom classExpression ) {
        final Node restrictionNode = new NodeType.UniversalRestriction( mappingConfig.getIdentifierMapper()
            .getSyntheticId() );
        return createPropertyAndObjectRangeEdges( restrictionNode, classExpression );
    }

    @Override
    public Result visit( final @Nonnull OWLObjectHasValue classExpression ) {
        final Node restrictionNode = new NodeType.ValueRestriction( mappingConfig.getIdentifierMapper()
            .getSyntheticId() );
        final Result rNodeResult = createPropertyEdge( restrictionNode, classExpression, DecoratedEdge.ABSTRACT_ROLE );
        final OWLIndividual individual = classExpression.getFiller();
        final Result oNodeResult = individual.accept( mappingConfig.getOwlIndividualMapper() );
        final Edge oEdge = new DecoratedEdge( Edge.Type.DEFAULT_ARROW, restrictionNode.getId(), oNodeResult.getNode()
            .getId(), DecoratedEdge.INDIVIDUAL );
        return Result.of( restrictionNode ).and( rNodeResult ).and( oNodeResult ).and( oEdge );
    }

    @Override
    public Result visit( final @Nonnull OWLObjectMinCardinality classExpression ) {
        if ( classExpression.isQualified() ) {
            final Node restrictionNode = new NodeType.AbstractQualifiedMinimalCardinality( mappingConfig
                .getIdentifierMapper().getSyntheticId(), classExpression.getCardinality() );
            return createPropertyAndObjectRangeEdges( restrictionNode, classExpression );
        } else {
            final Node restrictionNode = new NodeType.AbstractMinimalCardinality( mappingConfig.getIdentifierMapper()
                .getSyntheticId(), classExpression.getCardinality() );
            return createPropertyEdge( restrictionNode, classExpression, DecoratedEdge.ABSTRACT_ROLE );
        }
    }

    @Override
    public Result visit( final @Nonnull OWLObjectExactCardinality classExpression ) {
        if ( classExpression.isQualified() ) {
            return createPropertyAndObjectRangeEdges( new NodeType.AbstractQualifiedExactCardinality( mappingConfig
                .getIdentifierMapper().getSyntheticId(), classExpression.getCardinality() ), classExpression );
        } else {
            final Node restrictionNode = new NodeType.AbstractExactCardinality( mappingConfig.getIdentifierMapper()
                .getSyntheticId(), classExpression.getCardinality() );
            return createPropertyEdge( restrictionNode, classExpression, DecoratedEdge.ABSTRACT_ROLE );
        }
    }

    @Override
    public Result visit( final @Nonnull OWLObjectMaxCardinality classExpression ) {
        if ( classExpression.isQualified() ) {
            return createPropertyAndObjectRangeEdges( new NodeType.AbstractQualifiedMaximalCardinality( mappingConfig
                .getIdentifierMapper().getSyntheticId(), classExpression.getCardinality() ), classExpression );
        } else {
            final Node restrictionNode = new NodeType.AbstractMaximalCardinality( mappingConfig.getIdentifierMapper()
                .getSyntheticId(), classExpression.getCardinality() );
            return createPropertyEdge( restrictionNode, classExpression, DecoratedEdge.ABSTRACT_ROLE );
        }
    }

    @Override
    public Result visit( final @Nonnull OWLObjectHasSelf classExpression ) {
        final Node restrictionNode = new NodeType.Self( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return createPropertyEdge( restrictionNode, classExpression, DecoratedEdge.ABSTRACT_ROLE );
    }

    @Override
    public Result visit( final @Nonnull OWLObjectOneOf classExpression ) {
        final Node restrictionNode = new NodeType.ClosedClass( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return classExpression.individuals().map( individual -> {
            final Result individualResult = individual.accept( mappingConfig.getOwlIndividualMapper() );
            final Edge iEdge = new PlainEdge( Edge.Type.DEFAULT_ARROW, restrictionNode.getId(), individualResult
                .getNode().getId() );
            return individualResult.and( iEdge );
        } ).reduce( Result.of( restrictionNode ), Result::and );
    }

    @Override
    public Result visit( final @Nonnull OWLDataSomeValuesFrom classExpression ) {
        final Node restrictionNode = new NodeType.ExistentialRestriction( mappingConfig.getIdentifierMapper()
            .getSyntheticId() );
        return createPropertyAndObjectRangeEdges( restrictionNode, classExpression );
    }

    @Override
    public Result visit( final @Nonnull OWLDataAllValuesFrom classExpression ) {
        final Node restrictionNode = new NodeType.UniversalRestriction( mappingConfig.getIdentifierMapper()
            .getSyntheticId() );
        return createPropertyAndObjectRangeEdges( restrictionNode, classExpression );
    }

    @Override
    public Result visit( final @Nonnull OWLDataHasValue classExpression ) {
        final Node restrictionNode = new NodeType.ValueRestriction( mappingConfig.getIdentifierMapper()
            .getSyntheticId() );
        final Result uNodeResult = createPropertyEdge( restrictionNode, classExpression, DecoratedEdge.CONCRETE_ROLE );
        final OWLLiteral literal = classExpression.getFiller();
        final Result vNodeResult = literal.accept( mappingConfig.getOwlDataMapper() );
        final Edge vEdge = new DecoratedEdge( Edge.Type.DEFAULT_ARROW, restrictionNode.getId(), vNodeResult.getNode()
            .getId(), DecoratedEdge.LITERAL );
        return Result.of( restrictionNode ).and( uNodeResult ).and( vNodeResult ).and( vEdge );
    }

    @Override
    public Result visit( final @Nonnull OWLDataMinCardinality classExpression ) {
        final Node restrictionNode = new NodeType.ConcreteMinimalCardinality( mappingConfig.getIdentifierMapper()
            .getSyntheticId(), classExpression.getCardinality() );
        return createPropertyEdge( restrictionNode, classExpression, DecoratedEdge.CONCRETE_ROLE );
    }

    @Override
    public Result visit( final @Nonnull OWLDataExactCardinality classExpression ) {
        final Node restrictionNode = new NodeType.ConcreteExactCardinality( mappingConfig.getIdentifierMapper()
            .getSyntheticId(), classExpression.getCardinality() );
        return createPropertyEdge( restrictionNode, classExpression, DecoratedEdge.CONCRETE_ROLE );
    }

    @Override
    public Result visit( final @Nonnull OWLDataMaxCardinality classExpression ) {
        final Node restrictionNode = new NodeType.ConcreteMaximalCardinality( mappingConfig.getIdentifierMapper()
            .getSyntheticId(), classExpression.getCardinality() );
        return createPropertyEdge( restrictionNode, classExpression, DecoratedEdge.CONCRETE_ROLE );
    }

    @Override
    public Result visit( final @Nonnull OWLClass classExpression ) {
        final Node classNode = new NodeType.Class( mappingConfig.getIdentifierMapper()
            .getIdForIri( classExpression.getIRI() ), mappingConfig.getNameMapper().getName( classExpression ) );
        return Result.of( classNode );
    }
}
