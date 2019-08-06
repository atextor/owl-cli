package de.atextor.owlcli.diagram.mappers;

import de.atextor.owlcli.diagram.graph.Node;
import org.semanticweb.owlapi.model.IRI;

public interface IdentifierMapper {
    Node.Id getIdForIri( final IRI iri );

    Node.Id getSyntheticId();
}
