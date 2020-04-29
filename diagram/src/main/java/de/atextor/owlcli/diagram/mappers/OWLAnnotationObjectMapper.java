package de.atextor.owlcli.diagram.mappers;

import de.atextor.owlcli.diagram.graph.Graph;
import de.atextor.owlcli.diagram.graph.Node;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAnnotationSubjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

import javax.annotation.Nonnull;

public class OWLAnnotationObjectMapper implements OWLAnnotationObjectVisitorEx<Graph>,
    OWLAnnotationSubjectVisitorEx<Graph> {
    private final MappingConfiguration mappingConfig;

    public OWLAnnotationObjectMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public Graph visit( final @Nonnull OWLAnnotation annotation ) {
        return annotation.getProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
    }

    @Override
    public Graph visit( final @Nonnull IRI iri ) {
        final Node.Id id = mappingConfig.getIdentifierMapper().getSyntheticId();
        return Graph.of( new Node.IRIReference( id, iri ) );
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
