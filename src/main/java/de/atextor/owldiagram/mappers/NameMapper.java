package de.atextor.owldiagram.mappers;

import org.semanticweb.owlapi.model.HasIRI;

class NameMapper {
    String getName( final HasIRI object ) {
        return object.getIRI().getFragment();
    }
}
