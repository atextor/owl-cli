package de.atextor.owlcli.diagram;

import de.atextor.owlcli.diagram.mappers.NameMapper;
import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.model.IRI;

public class TestNameMapper implements NameMapper {

    @Override
    public String getName( final HasIRI object ) {
        return getName( object.getIRI() );
    }

    @Override
    public String getName( final IRI object ) {
        return object.getFragment();
    }
}
