package de.atextor.owlcli.diagram.mappers;

import de.atextor.owlcli.diagram.graph.Graph;
import de.atextor.owlcli.diagram.graph.Node;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.annotation.Nonnull;

public class OWLEntityMapper implements OWLEntityVisitorEx<Graph> {
    private final MappingConfiguration mappingConfig;

    public OWLEntityMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public Graph visit( final @Nonnull OWLClass classExpression ) {
        final Node node =
            new Node.Class( mappingConfig.getIdentifierMapper().getIdForIri( classExpression.getIRI() ),
                mappingConfig.getNameMapper().getName( classExpression ) );
        return Graph.of( node );
    }

    @Override
    public Graph visit( final @Nonnull OWLDatatype dataType ) {
        final Node node =
            new Node.Datatype( mappingConfig.getIdentifierMapper().getIdForIri( dataType.getIRI() ),
                mappingConfig.getNameMapper().getName( dataType ) );
        return Graph.of( node );
    }

    @Override
    public Graph visit( final @Nonnull OWLNamedIndividual individual ) {
        return individual.accept( mappingConfig.getOwlIndividualMapper() );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectProperty property ) {
        final Node node =
            new Node.ObjectProperty( mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() ),
                mappingConfig.getNameMapper().getName( property ) );
        return Graph.of( node );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataProperty property ) {
        final Node node =
            new Node.DataProperty( mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() ),
                mappingConfig.getNameMapper().getName( property ) );
        return Graph.of( node );
    }

    @Override
    public Graph visit( final @Nonnull OWLAnnotationProperty property ) {
        final Node node =
            new Node.AnnotationProperty( mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() ),
                mappingConfig.getNameMapper().getName( property ) );
        return Graph.of( node );
    }
}
