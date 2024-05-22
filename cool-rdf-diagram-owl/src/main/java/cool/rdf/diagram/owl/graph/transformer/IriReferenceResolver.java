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

package cool.rdf.diagram.owl.graph.transformer;

import cool.rdf.diagram.owl.graph.GraphElement;
import cool.rdf.diagram.owl.graph.Node;
import cool.rdf.diagram.owl.mappers.MappingConfiguration;
import cool.rdf.diagram.owl.graph.node.IRIReference;
import cool.rdf.diagram.owl.graph.node.Literal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOG = LoggerFactory.getLogger( IriReferenceResolver.class );

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
     * @return the resulting graph that contains no {@link IRIReference}s anymore
     */
    @Override
    public Set<GraphElement> apply( final Set<GraphElement> graph ) {
        LOG.debug( "Resolving IRI references in {}", graph );
        final Set<IRIReference> references = graph.stream()
            .flatMap( element -> element.view( IRIReference.class ) )
            .collect( Collectors.toSet() );

        if ( references.isEmpty() ) {
            LOG.debug( "Resolved graph: {}", graph );
            return graph;
        }

        final ChangeSet changeSet = resolveReferences( graph, references );
        final Set<GraphElement> result = changeSet.applyTo( graph );
        LOG.debug( "Resolved graph: {}", result );
        return result;
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
