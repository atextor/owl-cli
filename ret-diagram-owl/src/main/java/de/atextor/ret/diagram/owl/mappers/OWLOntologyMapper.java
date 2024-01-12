/*
 * Copyright 2024 Andreas Textor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.atextor.ret.diagram.owl.mappers;

import de.atextor.ret.diagram.owl.graph.Graph;
import de.atextor.ret.diagram.owl.graph.GraphElement;
import de.atextor.ret.diagram.owl.graph.transformer.IriReferenceResolver;
import de.atextor.ret.diagram.owl.graph.transformer.PropertyMarkerMerger;
import de.atextor.ret.diagram.owl.graph.transformer.PunningRemover;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Main class for mapping an {@link OWLOntology} to a {@link Graph}. The mapping is done in two steps:
 * First, all axioms in the ontology are separately mapped using the respective OWL*Mappers into nodes and edges.
 * Secondly, the {@link de.atextor.ret.diagram.owl.graph.transformer.GraphTransformer}s clean up the graph by
 * performing changes that take the context of the whole graph into account.
 */
public class OWLOntologyMapper implements Function<OWLOntology, Set<GraphElement>> {
    private final MappingConfiguration mappingConfiguration;

    private final List<Function<Set<GraphElement>, Set<GraphElement>>> transformers;

    /**
     * Creates a new ontology mapper from a given mapping config
     *
     * @param mappingConfiguration the config
     */
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
