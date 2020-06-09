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

package de.atextor.owlcli.diagram.graph.transformer;

import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.node.IRIReference;
import de.atextor.owlcli.diagram.graph.node.Literal;
import de.atextor.owlcli.diagram.mappers.MappingConfiguration;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implements a graph transformation that removes all nodes of type {@link IRIReference} from a graph and
 * replaces them with the corresponding direct links to the referenced nodes where possible, and with literal
 * nodes representing the reference IRI otherwise
 */
public class IriReferenceResolver extends GraphTransformer {
    private final MappingConfiguration mappingConfiguration;

    /**
     * Initialize the transformer
     *
     * @param mappingConfiguration the context mapping configuration
     */
    public IriReferenceResolver( final MappingConfiguration mappingConfiguration ) {
        this.mappingConfiguration = mappingConfiguration;
    }

    /**
     * Apply this transformer to the given input graph
     *
     * @param graph the input graph
     * @return the resulting graph that contains no {@link IRIReference}s any more
     */
    @Override
    public Set<GraphElement> apply( final Set<GraphElement> graph ) {
        final Set<IRIReference> references = graph.stream()
            .flatMap( element -> element.view( IRIReference.class ) )
            .collect( Collectors.toSet() );

        if ( references.isEmpty() ) {
            return graph;
        }

        final ChangeSet changeSet = resolveReferences( graph, references );
        return changeSet.applyTo( graph );
    }

    private ChangeSet resolveReferences( final Set<GraphElement> graph, final Set<IRIReference> references ) {
        return references.stream().flatMap( reference -> {
            final Set<Node> referencedNodes = findNodesWithIri( graph, reference.getIri() )
                .collect( Collectors.toSet() );

            if ( referencedNodes.isEmpty() ) {
                final Node literal = turnReferenceIntoLiteral( reference );
                final ChangeSet updatedToEdges = updateEdgesTo( graph, reference, literal );
                return Stream.of( new ChangeSet( Set.of( literal ), Set.of( reference ) ).merge( updatedToEdges ) );
            }

            return referencedNodes.stream().flatMap( node -> {
                final ChangeSet updatedReferenceElement = new ChangeSet( Set.of(), Set.of( reference ) );
                final ChangeSet updatedToEdges = updateEdgesTo( graph, reference, node );
                final ChangeSet updatedFromEdges = updateEdgesFrom( graph, reference, node );
                return Stream.of( updatedReferenceElement, updatedToEdges, updatedFromEdges );
            } );
        } ).reduce( ChangeSet.EMPTY, ChangeSet::merge );
    }

    private Literal turnReferenceIntoLiteral( final IRIReference reference ) {
        return new Literal(
            mappingConfiguration.getIdentifierMapper().getSyntheticId(),
            mappingConfiguration.getNameMapper().getName( reference.getIri() ) );
    }
}
