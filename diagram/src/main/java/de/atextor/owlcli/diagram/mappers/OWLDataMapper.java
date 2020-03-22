package de.atextor.owlcli.diagram.mappers;

import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.Graph;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.NodeType;
import de.atextor.owlcli.diagram.graph.PlainEdge;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDataVisitorEx;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLLiteral;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

import static io.vavr.API.TODO;

public class OWLDataMapper implements OWLDataVisitorEx<Graph> {
    private final MappingConfiguration mappingConfig;

    public OWLDataMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    private Stream<GraphElement> createEdgeToDataRange( final Node sourceNode,
                                                        final OWLDataRange classExpression ) {
        final Graph diagramPartsForDataRange = classExpression.accept( this );
        final Node targetNode = diagramPartsForDataRange.getNode();
        final Stream<GraphElement> remainingElements = diagramPartsForDataRange.getOtherElements();
        final Node.Id from = sourceNode.getId();
        final Node.Id to = targetNode.getId();
        final Edge operandEdge = new PlainEdge( Edge.Type.DEFAULT_ARROW, from, to );

        return Stream.concat( Stream.of( sourceNode, targetNode, operandEdge ), remainingElements );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataComplementOf dataRange ) {
        final Node complementNode =
            new NodeType.Complement( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final Stream<GraphElement> remainingElements = createEdgeToDataRange( complementNode,
            dataRange.getDataRange() );
        return new Graph( complementNode, remainingElements );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataOneOf dataRange ) {
        final Node restrictionNode =
            new NodeType.ClosedClass( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return dataRange.values().map( value -> {
            final Graph valueGraph = value.accept( mappingConfig.getOwlDataMapper() );
            final Edge vEdge = new PlainEdge( Edge.Type.DEFAULT_ARROW, restrictionNode.getId(),
                valueGraph.getNode().getId() );
            return valueGraph.and( vEdge );
        } ).reduce( Graph.of( restrictionNode ), Graph::and );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataIntersectionOf dataRange ) {
        final Node intersectionNode =
            new NodeType.Intersection( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final Stream<GraphElement> remainingElements = dataRange.operands().flatMap( operand ->
            createEdgeToDataRange( intersectionNode, operand ) );
        return new Graph( intersectionNode, remainingElements );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataUnionOf dataRange ) {
        final Node unionNode = new NodeType.Union( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final Stream<GraphElement> remainingElements = dataRange.operands().flatMap( operand ->
            createEdgeToDataRange( unionNode, operand ) );
        return new Graph( unionNode, remainingElements );
    }

    @Override
    public Graph visit( final @Nonnull OWLDatatypeRestriction node ) {
        return TODO();
    }

    @Override
    public Graph visit( final @Nonnull OWLFacetRestriction node ) {
        return TODO();
    }

    @Override
    public Graph visit( final @Nonnull OWLDatatype node ) {
        return node.accept( mappingConfig.getOwlEntityMapper() );
    }

    @Override
    public Graph visit( final @Nonnull OWLLiteral node ) {
        final Node.Id id = mappingConfig.getIdentifierMapper().getSyntheticId();
        return Graph.of( new NodeType.Literal( id, node.getLiteral() ) );
    }
}
