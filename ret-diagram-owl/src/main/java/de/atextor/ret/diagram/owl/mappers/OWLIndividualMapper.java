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
import de.atextor.ret.diagram.owl.graph.Node;
import de.atextor.ret.diagram.owl.graph.node.Individual;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;

/**
 * Maps {@link org.semanticweb.owlapi.model.OWLIndividual}s to {@link Graph}s
 */
public class OWLIndividualMapper implements OWLIndividualVisitorEx<Graph> {
    private final MappingConfiguration mappingConfig;

    /**
     * Creates a new individual mapper from a given mapping config
     *
     * @param mappingConfig the config
     */
    public OWLIndividualMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public Graph visit( final @Nonnull OWLAnonymousIndividual individual ) {
        final Node node = new Individual( mappingConfig.getIdentifierMapper().getSyntheticId(),
            individual.accept( mappingConfig.getOwlIndividualPrinter() ) );
        return Graph.of( node );
    }

    @Override
    public Graph visit( final @Nonnull OWLNamedIndividual individual ) {
        final Node.Id id = mappingConfig.getIdentifierMapper().getIdForIri( individual.getIRI() );
        final String label = individual.accept( mappingConfig.getOwlIndividualPrinter() );
        final Node node = new Individual( id, label );
        return Graph.of( node );
    }
}
