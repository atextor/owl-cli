package de.atextor.owlcli.diagram.mappers;

import org.semanticweb.owlapi.model.HasIRI;

class DefaultNameMapper implements NameMapper {
    private final MappingConfiguration mappingConfig;

    public DefaultNameMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public String getName( final HasIRI object ) {
        return object.getIRI().getFragment();
    }
}
