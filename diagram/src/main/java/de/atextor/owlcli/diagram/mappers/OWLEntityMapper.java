package de.atextor.owlcli.diagram.mappers;

import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.NodeType;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.annotation.Nonnull;

public class OWLEntityMapper implements OWLEntityVisitorEx<Result> {
    private final MappingConfiguration mappingConfig;

    public OWLEntityMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public Result visit( final @Nonnull OWLClass classExpression ) {
        final Node node =
            new NodeType.Class( mappingConfig.getIdentifierMapper().getIdForIri( classExpression.getIRI() ),
                mappingConfig.getNameMapper().getName( classExpression ) );
        return Result.of( node );
    }

    @Override
    public Result visit( final @Nonnull OWLDatatype dataType ) {
        final Node node =
            new NodeType.Datatype( mappingConfig.getIdentifierMapper().getIdForIri( dataType.getIRI() ),
                mappingConfig.getNameMapper().getName( dataType ) );
        return Result.of( node );
    }

    @Override
    public Result visit( final @Nonnull OWLNamedIndividual individual ) {
        final Node node =
            new NodeType.Individual( mappingConfig.getIdentifierMapper().getIdForIri( individual.getIRI() ),
                mappingConfig.getNameMapper().getName( individual ) );
        return Result.of( node );
    }

    @Override
    public Result visit( final @Nonnull OWLObjectProperty property ) {
        final Node node =
            new NodeType.AbstractRole( mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() ),
                mappingConfig.getNameMapper().getName( property ) );
        return Result.of( node );
    }

    @Override
    public Result visit( final @Nonnull OWLDataProperty property ) {
        final Node node =
            new NodeType.ConcreteRole( mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() ),
                mappingConfig.getNameMapper().getName( property ) );
        return Result.of( node );
    }

    @Override
    public Result visit( final @Nonnull OWLAnnotationProperty property ) {
        final Node node =
            new NodeType.AnnotationRole( mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() ),
                mappingConfig.getNameMapper().getName( property ) );
        return Result.of( node );
    }
}
