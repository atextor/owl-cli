package de.atextor.owldiagram;

import de.atextor.owldiagram.graph.Node;
import de.atextor.owldiagram.mappers.IdentifierMapper;
import org.semanticweb.owlapi.model.IRI;

import java.util.UUID;

public class TestIdentifierMapper implements IdentifierMapper {
    @Override
    public Node.Id getIdForIri( final IRI iri ) {
        return new Node.Id( iri.getFragment() );
    }

    @Override
    public Node.Id getSyntheticId() {
        return new Node.Id( UUID.randomUUID().toString() );
    }
}
