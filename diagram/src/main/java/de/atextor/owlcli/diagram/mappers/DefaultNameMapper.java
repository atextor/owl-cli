package de.atextor.owlcli.diagram.mappers;

import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.model.IRI;

class DefaultNameMapper implements NameMapper {
    private final MappingConfiguration mappingConfig;

    public DefaultNameMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }


    @Override
    public String getName( final HasIRI object ) {
        return getName( object.getIRI() );
    }

    @Override
    public String getName( final IRI object ) {
        return object.getFragment();
    }
}
