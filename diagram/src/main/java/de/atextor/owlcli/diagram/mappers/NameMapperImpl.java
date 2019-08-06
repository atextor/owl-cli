package de.atextor.owlcli.diagram.mappers;

import org.semanticweb.owlapi.model.HasIRI;

class NameMapperImpl implements NameMapper {
    private MappingConfiguration mappingConfig;

    public NameMapperImpl( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public String getName( final HasIRI object ) {
        return object.getIRI().getFragment();
    }
}
