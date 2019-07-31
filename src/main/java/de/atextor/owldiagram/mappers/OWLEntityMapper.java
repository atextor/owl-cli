package de.atextor.owldiagram.mappers;

import de.atextor.owldiagram.graph.Node;
import de.atextor.owldiagram.graph.NodeType;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

public class OWLEntityMapper implements OWLEntityVisitorEx<Result> {
    private MappingConfiguration mappingConfig;

    public OWLEntityMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public Result visit( final OWLClass classExpression ) {
        final Node node =
            new NodeType.Class( mappingConfig.getIdentifierMapper().getIdForIri( classExpression.getIRI() ),
                mappingConfig.getNameMapper().getName( classExpression ) );
        return Result.of( node );
    }

    @Override
    public Result visit( final OWLDatatype dataType ) {
        final Node node =
            new NodeType.Datatype( mappingConfig.getIdentifierMapper().getIdForIri( dataType.getIRI() ),
                mappingConfig.getNameMapper().getName( dataType ) );
        return Result.of( node );
    }

    @Override
    public Result visit( final OWLNamedIndividual individual ) {
        final Node node =
            new NodeType.Individual( mappingConfig.getIdentifierMapper().getIdForIri( individual.getIRI() ),
                mappingConfig.getNameMapper().getName( individual ) );
        return Result.of( node );
    }

    @Override
    public Result visit( final OWLObjectProperty property ) {
        final Node node =
            new NodeType.AbstractRole( mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() ),
                mappingConfig.getNameMapper().getName( property ) );
        return Result.of( node );
    }

    @Override
    public Result visit( final OWLDataProperty property ) {
        final Node node =
            new NodeType.ConcreteRole( mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() ),
                mappingConfig.getNameMapper().getName( property ) );
        return Result.of( node );
    }

    @Override
    public Result visit( final OWLAnnotationProperty property ) {
        final Node node =
            new NodeType.AnnotationRole( mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() ),
                mappingConfig.getNameMapper().getName( property ) );
        return Result.of( node );
    }
}
