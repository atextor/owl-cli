package de.atextor.owldiagram.mappers;

import de.atextor.owldiagram.graph.Node;
import org.semanticweb.owlapi.model.IRI;

import java.util.UUID;

public class IdentifierMapper {
    public Node.Id getIdForIri( final IRI iri ) {
        return new Node.Id( iri.getFragment() );
    }

    public Node.Id getSyntheticId() {
        return new Node.Id( UUID.randomUUID().toString() );
    }
}
