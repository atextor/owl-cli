/*
 * This file is part of OWL-CLI.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright (c) 2020, Andreas Textor.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www
 * .gnu.org/licenses/.
 */

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
