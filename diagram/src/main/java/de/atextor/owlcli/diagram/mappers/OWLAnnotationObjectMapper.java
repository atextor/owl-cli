package de.atextor.owlcli.diagram.mappers;

import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.NodeType;
import de.atextor.owlcli.diagram.graph.PlainEdge;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.stream.Stream;

import static io.vavr.API.TODO;

public class OWLAnnotationObjectMapper implements OWLAnnotationObjectVisitorEx<Result> {
    private final MappingConfiguration mappingConfig;

    public OWLAnnotationObjectMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public Result visit( final @Nonnull OWLAnnotation annotation ) {
        final Result valueResult = annotation.getValue().accept( this );
        final Result propertyResult = annotation.getProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Edge edge = new PlainEdge( Edge.Type.DEFAULT_ARROW, propertyResult.getNode().getId(), valueResult.getNode
                ().getId() );
    }

    @Override
    public Result visit( final @Nonnull OWLSubAnnotationPropertyOfAxiom axiom ) {
        return TODO();
        return propertyResult.and( valueResult ).and( edge );
    }

    @Override
    public Result visit( final @Nonnull OWLAnnotationPropertyDomainAxiom axiom ) {
        return TODO();
    }

    @Override
    public Result visit( final @Nonnull OWLAnnotationPropertyRangeAxiom axiom ) {
        return TODO();
    }

    @Override
    public Result visit( final @Nonnull IRI iri ) {
        final Node.Id id = mappingConfig.getIdentifierMapper().getIdForIri( iri );
        final String label = iri.toString();
        return Result.of( new NodeType.Literal( id, label ) );
    }

    @Override
    public Result visit( final @Nonnull OWLAnonymousIndividual individual ) {
        return individual.accept( mappingConfig.getOwlIndividualMapper() );
    }

    @Override
    public Result visit( final @Nonnull OWLLiteral node ) {
        return node.accept( mappingConfig.getOwlDataMapper() );
    }
}
