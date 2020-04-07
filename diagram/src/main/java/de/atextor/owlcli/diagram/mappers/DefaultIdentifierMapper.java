package de.atextor.owlcli.diagram.mappers;

import de.atextor.owlcli.diagram.graph.Node;
import org.semanticweb.owlapi.model.IRI;

import java.util.UUID;

public class DefaultIdentifierMapper implements IdentifierMapper {
    private final MappingConfiguration mappingConfig;

    public DefaultIdentifierMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public Node.Id getIdForIri( final IRI iri ) {
        return new Node.Id( iri.getFragment(), iri );
    }

    @Override
    public Node.Id getSyntheticId() {
        return new Node.Id( "_" + UUID.randomUUID().toString().replace( "-", "" ) );
    }
}
