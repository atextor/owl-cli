package de.atextor.owlcli.diagram.mappers;

import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.Graph;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.NodeType;
import de.atextor.owlcli.diagram.graph.PlainEdge;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

import javax.annotation.Nonnull;

public class OWLAnnotationObjectMapper implements OWLAnnotationObjectVisitorEx<Graph> {
    private final MappingConfiguration mappingConfig;

    public OWLAnnotationObjectMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public Graph visit( final @Nonnull OWLAnnotation annotation ) {
        final Graph valueGraph = annotation.getValue().accept( this );
        final Graph propertyGraph = annotation.getProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Edge edge = new PlainEdge( Edge.Type.DEFAULT_ARROW, propertyGraph.getNode().getId(), valueGraph.getNode
            ().getId() );
        return propertyGraph.and( valueGraph ).and( edge );
    }

    @Override
    public Graph visit( final @Nonnull IRI iri ) {
        final Node.Id id = mappingConfig.getIdentifierMapper().getIdForIri( iri );
        final String label = iri.toString();
        return Graph.of( new NodeType.Literal( id, label ) );
    }

    @Override
    public Graph visit( final @Nonnull OWLAnonymousIndividual individual ) {
        return individual.accept( mappingConfig.getOwlIndividualMapper() );
    }

    @Override
    public Graph visit( final @Nonnull OWLLiteral node ) {
        return node.accept( mappingConfig.getOwlDataMapper() );
    }
}
