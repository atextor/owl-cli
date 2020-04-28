package de.atextor.owlcli.diagram.mappers;

import de.atextor.owlcli.diagram.graph.Graph;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.transformer.IriReferenceResolver;
import de.atextor.owlcli.diagram.graph.transformer.PropertyMarkerMerger;
import de.atextor.owlcli.diagram.graph.transformer.PunningRemover;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OWLOntologyMapper implements Function<OWLOntology, Set<GraphElement>> {
    private final MappingConfiguration mappingConfiguration;
    private final List<Function<Set<GraphElement>, Set<GraphElement>>> transformers;

    public OWLOntologyMapper( final MappingConfiguration mappingConfiguration ) {
        this.mappingConfiguration = mappingConfiguration;
        transformers = List.of(
            new PunningRemover( mappingConfiguration ),
            new IriReferenceResolver( mappingConfiguration ),
            new PropertyMarkerMerger( mappingConfiguration )
        );
    }

    @Override
    public Set<GraphElement> apply( final OWLOntology ontology ) {
        final Set<GraphElement> elements = ontology.axioms()
            .map( axiom -> axiom.accept( mappingConfiguration.getOwlAxiomMapper() ) )
            .flatMap( Graph::toStream )
            .collect( Collectors.toSet() );

        return transformers.stream().sequential().reduce( Function.identity(), Function::andThen ).apply( elements );
    }
}
