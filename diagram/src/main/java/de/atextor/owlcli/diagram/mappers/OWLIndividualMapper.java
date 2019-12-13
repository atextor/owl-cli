package de.atextor.owlcli.diagram.mappers;

import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.NodeType;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;

public class OWLIndividualMapper implements OWLIndividualVisitorEx<Result> {
    private final MappingConfiguration mappingConfig;

    public OWLIndividualMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public Result visit( final @Nonnull OWLAnonymousIndividual individual ) {
        final Node node = new NodeType.Individual( mappingConfig.getIdentifierMapper().getSyntheticId(), "[]" );
        return Result.of( node );
    }

    @Override
    public Result visit( final @Nonnull OWLNamedIndividual individual ) {
        final Node.Id id = mappingConfig.getIdentifierMapper().getIdForIri( individual.getIRI() );
        final String label = id.getId();
        final Node node = new NodeType.Individual( id, label );
        return Result.of( node );
    }
}
