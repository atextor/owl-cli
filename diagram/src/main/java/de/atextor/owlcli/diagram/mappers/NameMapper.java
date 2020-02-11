package de.atextor.owlcli.diagram.mappers;

import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.model.IRI;

public interface NameMapper {
    String getName( final HasIRI object );

    String getName( IRI object );
}
