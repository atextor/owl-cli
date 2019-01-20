package de.atextor.owldiagram.mappers;

import org.semanticweb.owlapi.model.OWLObject;

public class NameMapper {
    public String getNameForEntity( final OWLObject object ) {
        return object.toString();
    }
}
