package de.atextor.owlcli.diagram.mappers;

import de.atextor.owlcli.diagram.graph.Node;
import org.semanticweb.owlapi.model.IRI;

import java.util.UUID;

public class IdentifierMapperImpl implements IdentifierMapper {
    private MappingConfiguration mappingConfig;

    public IdentifierMapperImpl( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public Node.Id getIdForIri( final IRI iri ) {
        return new Node.Id( iri.getFragment() );
    }

    @Override
    public Node.Id getSyntheticId() {
        return new Node.Id( "_" + UUID.randomUUID().toString().replace( "-", "" ) );
    }
}
