package de.atextor.owlcli.diagram.mappers;

import de.atextor.owlcli.diagram.graph.Graph;
import de.atextor.owlcli.diagram.graph.GraphElement;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.function.Function;
import java.util.stream.Stream;

public class OWLOntologyMapper implements Function<OWLOntology, Stream<GraphElement>> {
    private final MappingConfiguration mappingConfiguration;

    public OWLOntologyMapper( final MappingConfiguration mappingConfiguration ) {
        this.mappingConfiguration = mappingConfiguration;
    }

    @Override
    public Stream<GraphElement> apply( final OWLOntology ontology ) {
        return ontology.axioms()
            .map( axiom -> axiom.accept( mappingConfiguration.getOwlAxiomMapper() ) )
            .flatMap( Graph::toStream );
    }
}
