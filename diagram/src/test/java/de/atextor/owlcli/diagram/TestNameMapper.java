package de.atextor.owlcli.diagram;

import de.atextor.owlcli.diagram.mappers.NameMapper;
import org.semanticweb.owlapi.model.HasIRI;

public class TestNameMapper implements NameMapper {

    @Override
    public String getName( final HasIRI object ) {
        return object.getIRI().getFragment();
    }
}
