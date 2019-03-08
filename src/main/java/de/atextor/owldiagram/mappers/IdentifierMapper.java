package de.atextor.owldiagram.mappers;

import de.atextor.owldiagram.graph.Node;
import org.semanticweb.owlapi.model.IRI;

public interface IdentifierMapper {
    Node.Id getIdForIri( final IRI iri );

    Node.Id getSyntheticId();
}
