package de.atextor.owlcli.diagram.mappers;

import org.semanticweb.owlapi.model.HasIRI;

public interface NameMapper {
    String getName( final HasIRI object );
}
