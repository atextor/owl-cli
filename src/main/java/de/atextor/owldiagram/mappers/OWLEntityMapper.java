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

import java.util.stream.Stream;

public class OWLEntityMapper implements OWLEntityVisitorEx<Result> {
    @Override
    public Result visit( final OWLClass classExpression ) {
        final Node node = new NodeType.Class( Mappers.getIdentifierMapper().getIdForIri( classExpression.getIRI() ),
                Mappers.getNameMapper().getNameForEntity( classExpression ) );
        return new Result( node, Stream.empty() );
    }

    @Override
    public Result visit( final OWLDatatype dataType ) {
        final Node node = new NodeType.Datatype( Mappers.getIdentifierMapper().getIdForIri( dataType.getIRI() ),
                Mappers.getNameMapper().getNameForEntity( dataType ) );
        return new Result( node, Stream.empty() );
    }

    @Override
    public Result visit( final OWLNamedIndividual individual ) {
        final Node node = new NodeType.Individual( Mappers.getIdentifierMapper().getIdForIri( individual.getIRI() ),
                Mappers.getNameMapper().getNameForEntity( individual ) );
        return new Result( node, Stream.empty() );
    }

    @Override
    public Result visit( final OWLObjectProperty property ) {
        final Node node = new NodeType.AbstractRole( Mappers.getIdentifierMapper().getIdForIri( property.getIRI() ),
                Mappers.getNameMapper().getNameForEntity( property ) );
        return new Result( node, Stream.empty() );
    }

    @Override
    public Result visit( final OWLDataProperty property ) {
        final Node node = new NodeType.ConcreteRole( Mappers.getIdentifierMapper().getIdForIri( property.getIRI() ),
                Mappers.getNameMapper().getNameForEntity( property ) );
        return new Result( node, Stream.empty() );
    }

    @Override
    public Result visit( final OWLAnnotationProperty property ) {
        final Node node = new NodeType.AnnotationRole( Mappers.getIdentifierMapper().getIdForIri( property.getIRI() ),
                Mappers.getNameMapper().getNameForEntity( property ) );
        return new Result( node, Stream.empty() );
    }
}
