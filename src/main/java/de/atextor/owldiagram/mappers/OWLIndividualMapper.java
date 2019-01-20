package de.atextor.owldiagram.mappers;

import de.atextor.owldiagram.graph.Node;
import de.atextor.owldiagram.graph.NodeType;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import java.util.stream.Stream;

public class OWLIndividualMapper implements OWLIndividualVisitorEx<MappingResult> {
    @Override
    public MappingResult visit( final OWLAnonymousIndividual individual ) {
        final Node node = new NodeType.Individual( Mappers.getIdentifierMapper().getSyntheticId(), "[]" );
        return new MappingResult( node, Stream.empty() );
    }

    @Override
    public MappingResult visit( final OWLNamedIndividual individual ) {
        final Node.Id id = Mappers.getIdentifierMapper().getIdForIri( individual.getIRI() );
        final String label = id.getId();
        final Node node = new NodeType.Individual( id, label );
        return new MappingResult( node, Stream.empty() );
    }
}
