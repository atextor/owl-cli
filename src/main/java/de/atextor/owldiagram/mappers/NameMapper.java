package de.atextor.owldiagram.mappers;

import org.semanticweb.owlapi.model.HasIRI;

public interface NameMapper {
    String getName( final HasIRI object );
}
