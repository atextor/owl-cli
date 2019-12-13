package de.atextor.owlcli.diagram.mappers;

import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.NodeType;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDataVisitorEx;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLLiteral;

import javax.annotation.Nonnull;

import static io.vavr.API.TODO;

public class OWLDataMapper implements OWLDataVisitorEx<Result> {
    private final MappingConfiguration mappingConfig;

    public OWLDataMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public Result visit( final @Nonnull OWLDataComplementOf node ) {
        return TODO();
    }

    @Override
    public Result visit( final @Nonnull OWLDataOneOf node ) {
        return TODO();
    }

    @Override
    public Result visit( final @Nonnull OWLDataIntersectionOf node ) {
        return TODO();
    }

    @Override
    public Result visit( final @Nonnull OWLDataUnionOf node ) {
        return TODO();
    }

    @Override
    public Result visit( final @Nonnull OWLDatatypeRestriction node ) {
        return TODO();
    }

    @Override
    public Result visit( final @Nonnull OWLFacetRestriction node ) {
        return TODO();
    }

    @Override
    public Result visit( final @Nonnull OWLDatatype node ) {
        final Node.Id id = mappingConfig.getIdentifierMapper().getIdForIri( node.getIRI() );
        final String name = mappingConfig.getNameMapper().getName( node );
        return Result.of( new NodeType.Datatype( id, name ) );
    }

    @Override
    public Result visit( final @Nonnull OWLLiteral node ) {
        final Node.Id id = mappingConfig.getIdentifierMapper().getSyntheticId();
        return Result.of( new NodeType.Literal( id, node.getLiteral() ) );
    }
}
