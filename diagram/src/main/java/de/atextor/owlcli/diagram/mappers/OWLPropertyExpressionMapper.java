package de.atextor.owlcli.diagram.mappers;

import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.NodeType;
import de.atextor.owlcli.diagram.graph.PlainEdge;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;

import java.util.stream.Stream;

public class OWLPropertyExpressionMapper implements OWLPropertyExpressionVisitorEx<Result> {
    private MappingConfiguration mappingConfig;

    public OWLPropertyExpressionMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public Result visit( final OWLObjectInverseOf property ) {
        final Node complementNode =
            new NodeType.Complement( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final OWLPropertyExpression invertedProperty = property.getInverseProperty();
        final Result propertyVisitorResult =
            invertedProperty.accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Edge propertyEdge = new PlainEdge( Edge.Type.DEFAULT_ARROW, complementNode.getId(),
            propertyVisitorResult.getNode().getId() );
        return new Result( complementNode, Stream.concat( Stream.of( propertyEdge,
            propertyVisitorResult.getNode() ), propertyVisitorResult.getRemainingElements() ) );
    }

    @Override
    public Result visit( final OWLObjectProperty property ) {
        final Node.Id id = mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() );
        final String label = id.getId();
        final Node node = new NodeType.AbstractRole( id, label );
        return Result.of( node );
    }

    @Override
    public Result visit( final OWLDataProperty property ) {
        final Node.Id id = mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() );
        final String label = id.getId();
        final Node node = new NodeType.ConcreteRole( id, label );
        return Result.of( node );
    }

    @Override
    public Result visit( final OWLAnnotationProperty property ) {
        final Node.Id id = mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() );
        final String label = id.getId();
        final Node node = new NodeType.AnnotationRole( id, label );
        return Result.of( node );
    }
}
