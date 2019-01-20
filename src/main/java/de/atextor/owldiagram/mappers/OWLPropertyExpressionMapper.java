package de.atextor.owldiagram.mappers;

import de.atextor.owldiagram.graph.Edge;
import de.atextor.owldiagram.graph.Node;
import de.atextor.owldiagram.graph.NodeType;
import de.atextor.owldiagram.graph.PlainEdge;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;

import java.util.stream.Stream;

public class OWLPropertyExpressionMapper implements OWLPropertyExpressionVisitorEx<MappingResult> {
    @Override
    public MappingResult visit( final OWLObjectInverseOf property ) {
        final Node complementNode = new NodeType.Complement( Mappers.getIdentifierMapper().getSyntheticId() );
        final OWLPropertyExpression invertedProperty = property.getInverseProperty();
        final MappingResult propertyVisitorResult =
                invertedProperty.accept( Mappers.getOwlPropertyExpressionMapper() );
        final Edge propertyEdge = new PlainEdge( Edge.Type.DEFAULT_ARROW, complementNode.getId(),
                propertyVisitorResult.getNode().getId() );
        return new MappingResult( complementNode, Stream.concat( Stream.of( propertyEdge,
                propertyVisitorResult.getNode() ), propertyVisitorResult.getRemainingElements() ) );
    }

    @Override
    public MappingResult visit( final OWLObjectProperty property ) {
        final Node.Id id = Mappers.getIdentifierMapper().getIdForIri( property.getIRI() );
        final String label = id.getId();
        final Node node = new NodeType.AbstractRole( id, label );
        return new MappingResult( node, Stream.empty() );
    }

    @Override
    public MappingResult visit( final OWLDataProperty property ) {
        final Node.Id id = Mappers.getIdentifierMapper().getIdForIri( property.getIRI() );
        final String label = id.getId();
        final Node node = new NodeType.ConcreteRole( id, label );
        return new MappingResult( node, Stream.empty() );
    }

    @Override
    public MappingResult visit( final OWLAnnotationProperty property ) {
        final Node.Id id = Mappers.getIdentifierMapper().getIdForIri( property.getIRI() );
        final String label = id.getId();
        final Node node = new NodeType.AnnotationRole( id, label );
        return new MappingResult( node, Stream.empty() );
    }
}
