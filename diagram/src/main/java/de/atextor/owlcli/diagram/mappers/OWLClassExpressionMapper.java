package de.atextor.owlcli.diagram.mappers;

import de.atextor.owlcli.diagram.graph.DecoratedEdge;
import de.atextor.owlcli.diagram.graph.Decoration;
import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.Graph;
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

public class OWLClassExpressionMapper implements OWLClassExpressionVisitorEx<Graph> {
    private final MappingConfiguration mappingConfig;

    public OWLClassExpressionMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    private Stream<GraphElement> createEdgeToClassExpression( final Node sourceNode,
                                                              final OWLClassExpression classExpression ) {
        final Graph diagramPartsForClassExpression = classExpression.accept( this );
        final Node targetNode = diagramPartsForClassExpression.getNode();
        final Stream<GraphElement> remainingElements = diagramPartsForClassExpression.getOtherElements();
        final Node.Id from = sourceNode.getId();
        final Node.Id to = targetNode.getId();
        final Edge operandEdge = new PlainEdge( Edge.Type.DEFAULT_ARROW, from, to );

        return new Graph( sourceNode, remainingElements ).and( targetNode ).and( operandEdge ).toStream();
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectIntersectionOf classExpression ) {
        final Node intersectionNode = new NodeType.Intersection( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final Stream<GraphElement> remainingElements = classExpression.operands()
            .flatMap( operand -> createEdgeToClassExpression( intersectionNode, operand ) );
        return new Graph( intersectionNode, remainingElements );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectUnionOf classExpression ) {
        final Node unionNode = new NodeType.Union( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final Stream<GraphElement> remainingElements = classExpression.operands()
            .flatMap( operand -> createEdgeToClassExpression( unionNode, operand ) );
        return new Graph( unionNode, remainingElements );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectComplementOf classExpression ) {
        final Node complementNode = new NodeType.Complement( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final Stream<GraphElement> remainingElements = createEdgeToClassExpression( complementNode, classExpression
            .getOperand() );
        return new Graph( complementNode, remainingElements );
    }

    private Graph createPropertyAndObjectRangeEdges( final Node restrictionNode,
                                                     final OWLQuantifiedObjectRestriction classExpression ) {
        final OWLClassExpression c = classExpression.getFiller();
        final OWLObjectPropertyExpression r = classExpression.getProperty();
        final Graph cNodeGraph = c.accept( mappingConfig.getOwlClassExpressionMapper() );
        final Graph rNodeGraph = r.accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Edge cEdge = new DecoratedEdge( Edge.Type.DEFAULT_ARROW, restrictionNode.getId(), cNodeGraph.getNode()
            .getId(), DecoratedEdge.CLASS );
        final Edge rEdge = new DecoratedEdge( Edge.Type.DEFAULT_ARROW, restrictionNode.getId(), rNodeGraph.getNode()
            .getId(), DecoratedEdge.OBJECT_PROPERTY );

        return Graph.of( restrictionNode ).and( cEdge ).and( rEdge ).and( cNodeGraph ).and( rNodeGraph );
    }

    private Graph createPropertyAndObjectRangeEdges( final Node restrictionNode,
                                                     final OWLQuantifiedDataRestriction classExpression ) {
        final OWLDataRange u = classExpression.getFiller();
        final OWLDataPropertyExpression d = classExpression.getProperty();
        final Graph uNodeGraph = u.accept( mappingConfig.getOwlDataMapper() );
        final Graph dNodeGraph = d.accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Edge uEdge = new DecoratedEdge( Edge.Type.DEFAULT_ARROW, restrictionNode.getId(), uNodeGraph.getNode()
            .getId(), DecoratedEdge.DATA_RANGE );
        final Edge dEdge = new DecoratedEdge( Edge.Type.DEFAULT_ARROW, restrictionNode.getId(), dNodeGraph.getNode()
            .getId(), DecoratedEdge.DATA_PROPERTY );
        return Graph.of( restrictionNode ).and( uNodeGraph ).and( dNodeGraph ).and( uEdge ).and( dEdge );
    }

    private Graph createPropertyEdge( final Node restrictionNode, final OWLRestriction classExpression,
                                      final Decoration edgeDecoration ) {
        final OWLPropertyExpression property = classExpression.getProperty();
        final Graph rNodeGraph = property.accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Edge rEdge = new DecoratedEdge( Edge.Type.DEFAULT_ARROW, restrictionNode.getId(), rNodeGraph.getNode()
            .getId(), edgeDecoration );
        return Graph.of( restrictionNode ).and( rNodeGraph ).and( rEdge );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectSomeValuesFrom classExpression ) {
        final Node restrictionNode = new NodeType.ExistentialRestriction( mappingConfig.getIdentifierMapper()
            .getSyntheticId() );
        return createPropertyAndObjectRangeEdges( restrictionNode, classExpression );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectAllValuesFrom classExpression ) {
        final Node restrictionNode = new NodeType.UniversalRestriction( mappingConfig.getIdentifierMapper()
            .getSyntheticId() );
        return createPropertyAndObjectRangeEdges( restrictionNode, classExpression );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectHasValue classExpression ) {
        final Node restrictionNode = new NodeType.ValueRestriction( mappingConfig.getIdentifierMapper()
            .getSyntheticId() );
        final Graph rNodeGraph = createPropertyEdge( restrictionNode, classExpression, DecoratedEdge.OBJECT_PROPERTY );
        final OWLIndividual individual = classExpression.getFiller();
        final Graph oNodeGraph = individual.accept( mappingConfig.getOwlIndividualMapper() );
        final Edge oEdge = new DecoratedEdge( Edge.Type.DEFAULT_ARROW, restrictionNode.getId(), oNodeGraph.getNode()
            .getId(), DecoratedEdge.INDIVIDUAL );
        return Graph.of( restrictionNode ).and( rNodeGraph ).and( oNodeGraph ).and( oEdge );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectMinCardinality classExpression ) {
        if ( classExpression.isQualified() ) {
            final Node restrictionNode = new NodeType.ObjectQualifiedMinimalCardinality( mappingConfig
                .getIdentifierMapper().getSyntheticId(), classExpression.getCardinality() );
            return createPropertyAndObjectRangeEdges( restrictionNode, classExpression );
        } else {
            final Node restrictionNode = new NodeType.ObjectMinimalCardinality( mappingConfig.getIdentifierMapper()
                .getSyntheticId(), classExpression.getCardinality() );
            return createPropertyEdge( restrictionNode, classExpression, DecoratedEdge.OBJECT_PROPERTY );
        }
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectExactCardinality classExpression ) {
        if ( classExpression.isQualified() ) {
            return createPropertyAndObjectRangeEdges( new NodeType.ObjectQualifiedExactCardinality( mappingConfig
                .getIdentifierMapper().getSyntheticId(), classExpression.getCardinality() ), classExpression );
        } else {
            final Node restrictionNode = new NodeType.ObjectExactCardinality( mappingConfig.getIdentifierMapper()
                .getSyntheticId(), classExpression.getCardinality() );
            return createPropertyEdge( restrictionNode, classExpression, DecoratedEdge.OBJECT_PROPERTY );
        }
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectMaxCardinality classExpression ) {
        if ( classExpression.isQualified() ) {
            return createPropertyAndObjectRangeEdges( new NodeType.ObjectQualifiedMaximalCardinality( mappingConfig
                .getIdentifierMapper().getSyntheticId(), classExpression.getCardinality() ), classExpression );
        } else {
            final Node restrictionNode = new NodeType.ObjectMaximalCardinality( mappingConfig.getIdentifierMapper()
                .getSyntheticId(), classExpression.getCardinality() );
            return createPropertyEdge( restrictionNode, classExpression, DecoratedEdge.OBJECT_PROPERTY );
        }
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectHasSelf classExpression ) {
        final Node restrictionNode = new NodeType.Self( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return createPropertyEdge( restrictionNode, classExpression, DecoratedEdge.OBJECT_PROPERTY );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectOneOf classExpression ) {
        final Node restrictionNode = new NodeType.ClosedClass( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return classExpression.individuals().map( individual -> {
            final Graph individualGraph = individual.accept( mappingConfig.getOwlIndividualMapper() );
            final Edge iEdge = new PlainEdge( Edge.Type.DEFAULT_ARROW, restrictionNode.getId(), individualGraph
                .getNode().getId() );
            return individualGraph.and( iEdge );
        } ).reduce( Graph.of( restrictionNode ), Graph::and );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataSomeValuesFrom classExpression ) {
        final Node restrictionNode = new NodeType.ExistentialRestriction( mappingConfig.getIdentifierMapper()
            .getSyntheticId() );
        return createPropertyAndObjectRangeEdges( restrictionNode, classExpression );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataAllValuesFrom classExpression ) {
        final Node restrictionNode = new NodeType.UniversalRestriction( mappingConfig.getIdentifierMapper()
            .getSyntheticId() );
        return createPropertyAndObjectRangeEdges( restrictionNode, classExpression );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataHasValue classExpression ) {
        final Node restrictionNode = new NodeType.ValueRestriction( mappingConfig.getIdentifierMapper()
            .getSyntheticId() );
        final Graph uNodeGraph = createPropertyEdge( restrictionNode, classExpression, DecoratedEdge.DATA_PROPERTY );
        final OWLLiteral literal = classExpression.getFiller();
        final Graph vNodeGraph = literal.accept( mappingConfig.getOwlDataMapper() );
        final Edge vEdge = new DecoratedEdge( Edge.Type.DEFAULT_ARROW, restrictionNode.getId(), vNodeGraph.getNode()
            .getId(), DecoratedEdge.LITERAL );
        return Graph.of( restrictionNode ).and( uNodeGraph ).and( vNodeGraph ).and( vEdge );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataMinCardinality classExpression ) {
        final Node restrictionNode = new NodeType.DataMinimalCardinality( mappingConfig.getIdentifierMapper()
            .getSyntheticId(), classExpression.getCardinality() );
        return createPropertyEdge( restrictionNode, classExpression, DecoratedEdge.DATA_PROPERTY );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataExactCardinality classExpression ) {
        final Node restrictionNode = new NodeType.DataExactCardinality( mappingConfig.getIdentifierMapper()
            .getSyntheticId(), classExpression.getCardinality() );
        return createPropertyEdge( restrictionNode, classExpression, DecoratedEdge.DATA_PROPERTY );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataMaxCardinality classExpression ) {
        final Node restrictionNode = new NodeType.DataMaximalCardinality( mappingConfig.getIdentifierMapper()
            .getSyntheticId(), classExpression.getCardinality() );
        return createPropertyEdge( restrictionNode, classExpression, DecoratedEdge.DATA_PROPERTY );
    }

    @Override
    public Graph visit( final @Nonnull OWLClass classExpression ) {
        final Node classNode = new NodeType.Class( mappingConfig.getIdentifierMapper()
            .getIdForIri( classExpression.getIRI() ), mappingConfig.getNameMapper().getName( classExpression ) );
        return Graph.of( classNode );
    }
}
