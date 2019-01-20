package de.atextor.owldiagram.mappers;

import de.atextor.owldiagram.graph.DecoratedEdge;
import de.atextor.owldiagram.graph.Decoration;
import de.atextor.owldiagram.graph.Edge;
import de.atextor.owldiagram.graph.GraphElement;
import de.atextor.owldiagram.graph.Node;
import de.atextor.owldiagram.graph.NodeType;
import de.atextor.owldiagram.graph.PlainEdge;
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

import java.util.stream.Stream;

public class OWLClassExpressionMapper implements OWLClassExpressionVisitorEx<MappingResult> {

    private Stream<GraphElement> createEdgeToClassExpression( final Node sourceNode,
                                                              final OWLClassExpression classExpression ) {
        final MappingResult diagramPartsForClassExpression = classExpression.accept( this );
        final Node targetNode = diagramPartsForClassExpression.getNode();
        final Stream<GraphElement> remainingElements = diagramPartsForClassExpression.getRemainingElements();
        final Node.Id from = sourceNode.getId();
        final Node.Id to = targetNode.getId();
        final Edge operandEdge = new PlainEdge( Edge.Type.DEFAULT_ARROW, from, to );

        return Stream.concat( Stream.of( sourceNode, targetNode, operandEdge ), remainingElements );
    }

    @Override
    public MappingResult visit( final OWLObjectIntersectionOf classExpression ) {
        final Node intersectionNode = new NodeType.Intersection( Mappers.getIdentifierMapper().getSyntheticId() );
        final Stream<GraphElement> remainingElements = classExpression.operands().flatMap( operand ->
                createEdgeToClassExpression( intersectionNode, operand ) );
        return new MappingResult( intersectionNode, remainingElements );
    }

    @Override
    public MappingResult visit( final OWLObjectUnionOf classExpression ) {
        final Node unionNode = new NodeType.Union( Mappers.getIdentifierMapper().getSyntheticId() );
        final Stream<GraphElement> remainingElements = classExpression.operands().flatMap( operand ->
                createEdgeToClassExpression( unionNode, operand ) );
        return new MappingResult( unionNode, remainingElements );
    }

    @Override
    public MappingResult visit( final OWLObjectComplementOf classExpression ) {
        final Node complementNode = new NodeType.Complement( Mappers.getIdentifierMapper().getSyntheticId() );
        final Stream<GraphElement> remainingElements = createEdgeToClassExpression( complementNode,
                classExpression.getOperand() );
        return new MappingResult( complementNode, remainingElements );
    }

    private MappingResult createPropertyAndDataRangeEdges( final Node restrictionNode,
                                                           final OWLQuantifiedObjectRestriction classExpression ) {
        final OWLClassExpression c = classExpression.getFiller();
        final OWLObjectPropertyExpression r = classExpression.getProperty();
        final MappingResult cNodeResult = c.accept( Mappers.getOwlClassExpressionMapper() );
        final MappingResult rNodeResult = r.accept( Mappers.getOwlPropertyExpressionMapper() );
        final Edge cEdge = new DecoratedEdge( Edge.Type.DEFAULT_ARROW, restrictionNode.getId(),
                cNodeResult.getNode().getId(), DecoratedEdge.CLASS );
        final Edge rEdge = new DecoratedEdge( Edge.Type.DEFAULT_ARROW, restrictionNode.getId(),
                rNodeResult.getNode().getId(), DecoratedEdge.ABSTRACT_ROLE );
        final Stream<GraphElement> remainingElements = Stream.concat( cNodeResult.getRemainingElements(),
                rNodeResult.getRemainingElements() );

        return new MappingResult( restrictionNode, Stream.concat( Stream.of( cEdge, cNodeResult.getNode(), rEdge,
                rNodeResult.getNode() ), remainingElements ) );
    }

    private MappingResult createPropertyAndDataRangeEdges( final Node restrictionNode,
                                                           final OWLQuantifiedDataRestriction classExpression ) {
        final OWLDataRange u = classExpression.getFiller();
        final OWLDataPropertyExpression d = classExpression.getProperty();
        final MappingResult uNodeResult = u.accept( Mappers.getOwlDataMapper() );
        final MappingResult dNodeResult = d.accept( Mappers.getOwlPropertyExpressionMapper() );
        final Edge uEdge = new DecoratedEdge( Edge.Type.DEFAULT_ARROW, restrictionNode.getId(),
                uNodeResult.getNode().getId(), DecoratedEdge.DATA_RANGE );
        final Edge dEdge = new DecoratedEdge( Edge.Type.DEFAULT_ARROW, restrictionNode.getId(),
                dNodeResult.getNode().getId(), DecoratedEdge.CONCRETE_ROLE );
        final Stream<GraphElement> remainingElements = Stream.concat( uNodeResult.getRemainingElements(),
                dNodeResult.getRemainingElements() );

        return new MappingResult( restrictionNode, Stream.concat( Stream.of( uEdge, uNodeResult.getNode(), dEdge,
                dNodeResult.getNode() ), remainingElements ) );
    }

    private MappingResult createPropertyEdge( final Node restrictionNode, final OWLRestriction classExpression,
                                              final Decoration edgeDecoration ) {
        final OWLPropertyExpression property = classExpression.getProperty();
        final MappingResult rNodeResult = property.accept( Mappers.getOwlPropertyExpressionMapper() );
        final Edge rEdge = new DecoratedEdge( Edge.Type.DEFAULT_ARROW, restrictionNode.getId(),
                rNodeResult.getNode().getId(), edgeDecoration );
        final Stream<GraphElement> remainingElements = rNodeResult.getRemainingElements();
        return new MappingResult( restrictionNode, Stream.concat( Stream.of( rEdge, rNodeResult.getNode() ),
                remainingElements ) );
    }

    @Override
    public MappingResult visit( final OWLObjectSomeValuesFrom classExpression ) {
        final Node restrictionNode =
                new NodeType.ExistentialRestriction( Mappers.getIdentifierMapper().getSyntheticId() );
        return createPropertyAndDataRangeEdges( restrictionNode, classExpression );
    }

    @Override
    public MappingResult visit( final OWLObjectAllValuesFrom classExpression ) {
        final Node restrictionNode =
                new NodeType.UniversalRestriction( Mappers.getIdentifierMapper().getSyntheticId() );
        return createPropertyAndDataRangeEdges( restrictionNode, classExpression );
    }

    @Override
    public MappingResult visit( final OWLObjectHasValue classExpression ) {
        final Node restrictionNode = new NodeType.ValueRestriction( Mappers.getIdentifierMapper().getSyntheticId() );
        final MappingResult rNodeResult = createPropertyEdge( restrictionNode, classExpression,
                DecoratedEdge.ABSTRACT_ROLE );
        final OWLIndividual individual = classExpression.getFiller();
        final MappingResult oNodeResult = individual.accept( Mappers.getOwlIndividualMapper() );
        final Edge oEdge = new DecoratedEdge( Edge.Type.DEFAULT_ARROW, restrictionNode.getId(),
                rNodeResult.getNode().getId(), DecoratedEdge.INDIVIDUAL );
        final Stream<GraphElement> remainingElements = Stream.concat( rNodeResult.getRemainingElements(),
                oNodeResult.getRemainingElements() );
        return new MappingResult( restrictionNode, Stream.concat( Stream.of( oEdge, rNodeResult.getNode() ),
                remainingElements ) );
    }

    @Override
    public MappingResult visit( final OWLObjectMinCardinality classExpression ) {
        if ( classExpression.isQualified() ) {
            final Node restrictionNode =
                    new NodeType.AbstractQualifiedMinimalCardinality( Mappers.getIdentifierMapper().getSyntheticId(),
                            classExpression.getCardinality() );
            return createPropertyAndDataRangeEdges( restrictionNode, classExpression );
        } else {
            final Node restrictionNode =
                    new NodeType.AbstractMinimalCardinality( Mappers.getIdentifierMapper().getSyntheticId(),
                            classExpression.getCardinality() );
            return createPropertyEdge( restrictionNode, classExpression, DecoratedEdge.ABSTRACT_ROLE );
        }
    }

    @Override
    public MappingResult visit( final OWLObjectExactCardinality classExpression ) {
        if ( classExpression.isQualified() ) {
            return createPropertyAndDataRangeEdges( new NodeType.AbstractQualifiedExactCardinality( Mappers.getIdentifierMapper().getSyntheticId(),
                    classExpression.getCardinality() ), classExpression );
        } else {
            final Node restrictionNode =
                    new NodeType.AbstractExactCardinality( Mappers.getIdentifierMapper().getSyntheticId(),
                            classExpression.getCardinality() );
            return createPropertyEdge( restrictionNode, classExpression, DecoratedEdge.ABSTRACT_ROLE );
        }
    }

    @Override
    public MappingResult visit( final OWLObjectMaxCardinality classExpression ) {
        if ( classExpression.isQualified() ) {
            return createPropertyAndDataRangeEdges( new NodeType.AbstractQualifiedMaximalCardinality( Mappers.getIdentifierMapper().getSyntheticId(),
                    classExpression.getCardinality() ), classExpression );
        } else {
            final Node restrictionNode =
                    new NodeType.AbstractMaximalCardinality( Mappers.getIdentifierMapper().getSyntheticId(),
                            classExpression.getCardinality() );
            return createPropertyEdge( restrictionNode, classExpression, DecoratedEdge.ABSTRACT_ROLE );
        }
    }

    @Override
    public MappingResult visit( final OWLObjectHasSelf classExpression ) {
        final Node restrictionNode = new NodeType.Self( Mappers.getIdentifierMapper().getSyntheticId() );
        return createPropertyEdge( restrictionNode, classExpression, DecoratedEdge.ABSTRACT_ROLE );
    }

    @Override
    public MappingResult visit( final OWLObjectOneOf classExpression ) {
        final Node restrictionNode = new NodeType.ClosedClass( Mappers.getIdentifierMapper().getSyntheticId() );
        final Stream<GraphElement> individualEdgesAndNodes = classExpression.individuals().flatMap( individual -> {
            final MappingResult individualResult = individual.accept( Mappers.getOwlIndividualMapper() );
            final Edge iEdge = new PlainEdge( Edge.Type.DEFAULT_ARROW, restrictionNode.getId(),
                    individualResult.getNode().getId() );
            final Stream<GraphElement> remainingElements = Stream.concat( individualResult.getRemainingElements(),
                    Stream.of( iEdge ) );
            return Stream.concat( Stream.of( individualResult.getNode() ), remainingElements );
        } );
        return new MappingResult( restrictionNode, individualEdgesAndNodes );
    }

    @Override
    public MappingResult visit( final OWLDataSomeValuesFrom classExpression ) {
        final Node restrictionNode =
                new NodeType.ExistentialRestriction( Mappers.getIdentifierMapper().getSyntheticId() );
        return createPropertyAndDataRangeEdges( restrictionNode, classExpression );
    }

    @Override
    public MappingResult visit( final OWLDataAllValuesFrom classExpression ) {
        final Node restrictionNode =
                new NodeType.UniversalRestriction( Mappers.getIdentifierMapper().getSyntheticId() );
        return createPropertyAndDataRangeEdges( restrictionNode, classExpression );
    }

    @Override
    public MappingResult visit( final OWLDataHasValue classExpression ) {
        final Node restrictionNode = new NodeType.ValueRestriction( Mappers.getIdentifierMapper().getSyntheticId() );
        final MappingResult uNodeResult = createPropertyEdge( restrictionNode, classExpression,
                DecoratedEdge.CONCRETE_ROLE );
        final OWLLiteral literal = classExpression.getFiller();
        final MappingResult vNodeResult = literal.accept( Mappers.getOwlDataMapper() );
        final Edge vEdge = new DecoratedEdge( Edge.Type.DEFAULT_ARROW, restrictionNode.getId(),
                uNodeResult.getNode().getId(), DecoratedEdge.LITERAL );
        final Stream<GraphElement> remainingElements = Stream.concat( uNodeResult.getRemainingElements(),
                vNodeResult.getRemainingElements() );
        return new MappingResult( restrictionNode, Stream.concat( Stream.of( vEdge, uNodeResult.getNode() ),
                remainingElements ) );
    }

    @Override
    public MappingResult visit( final OWLDataMinCardinality classExpression ) {
        final Node restrictionNode =
                new NodeType.ConcreteMinimalCardinality( Mappers.getIdentifierMapper().getSyntheticId(),
                        classExpression.getCardinality() );
        return createPropertyEdge( restrictionNode, classExpression, DecoratedEdge.ABSTRACT_ROLE );
    }

    @Override
    public MappingResult visit( final OWLDataExactCardinality classExpression ) {
        final Node restrictionNode =
                new NodeType.ConcreteExactCardinality( Mappers.getIdentifierMapper().getSyntheticId(),
                        classExpression.getCardinality() );
        return createPropertyEdge( restrictionNode, classExpression, DecoratedEdge.ABSTRACT_ROLE );
    }

    @Override
    public MappingResult visit( final OWLDataMaxCardinality classExpression ) {
        final Node restrictionNode =
                new NodeType.ConcreteMaximalCardinality( Mappers.getIdentifierMapper().getSyntheticId(),
                        classExpression.getCardinality() );
        return createPropertyEdge( restrictionNode, classExpression, DecoratedEdge.ABSTRACT_ROLE );
    }

    @Override
    public MappingResult visit( final OWLClass classExpression ) {
        final Node classNode =
                new NodeType.Class( Mappers.getIdentifierMapper().getIdForIri( classExpression.getIRI() ),
                        Mappers.getNameMapper().getNameForEntity( classExpression ) );
        return new MappingResult( classNode, Stream.empty() );
    }
}
