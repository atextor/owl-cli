package de.atextor.owlcli.diagram.mappers;

import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.Graph;
import de.atextor.owlcli.diagram.graph.Node;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;

import javax.annotation.Nonnull;

public class OWLPropertyExpressionMapper implements OWLPropertyExpressionVisitorEx<Graph> {
    private final MappingConfiguration mappingConfig;

    public OWLPropertyExpressionMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectInverseOf property ) {
        final Node complementNode =
            new Node.Complement( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final OWLPropertyExpression invertedProperty = property.getInverseProperty();
        final Graph propertyVisitorGraph =
            invertedProperty.accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Edge propertyEdge = new Edge.Plain( Edge.Type.DEFAULT_ARROW, complementNode.getId(),
            propertyVisitorGraph.getNode().getId() );
        return Graph.of( complementNode ).and( propertyVisitorGraph ).and( propertyEdge );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectProperty property ) {
        final Node.Id id = mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() );
        final String label = id.getId();
        final Node node = new Node.ObjectProperty( id, label );
        return Graph.of( node );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataProperty property ) {
        final Node.Id id = mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() );
        final String label = id.getId();
        final Node node = new Node.DataProperty( id, label );
        return Graph.of( node );
    }

    @Override
    public Graph visit( final @Nonnull OWLAnnotationProperty property ) {
        final Node.Id id = mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() );
        final String label = id.getId();
        final Node node = new Node.AnnotationProperty( id, label );
        return Graph.of( node );
    }
}
