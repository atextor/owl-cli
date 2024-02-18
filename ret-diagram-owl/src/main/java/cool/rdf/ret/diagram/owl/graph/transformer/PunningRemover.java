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

package cool.rdf.ret.diagram.owl.graph.transformer;

import cool.rdf.ret.diagram.owl.graph.GraphElement;
import cool.rdf.ret.diagram.owl.graph.Node;
import cool.rdf.ret.diagram.owl.mappers.MappingConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implements a graph transformer that resolves
 * <a href="https://www.w3.org/TR/owl2-new-features/#F12:_Punning">Punning</a> in a graph: An input ontology that
 * uses punning for e.g. an individual and a class results in a graph that contains both the individual and the class
 * as nodes, but both share the same {@link Node.Id}, as it is derived from the element's
 * {@link org.semanticweb.owlapi.model.IRI}. This transformer replaces the nodes with new, uniquely identified nodes
 * (that keep the original IRI in their IDs) and updates all edges in the graph accordingly.
 */
public class PunningRemover extends GraphTransformer {
    private final MappingConfiguration mappingConfiguration;

    private static final Logger LOG = LoggerFactory.getLogger( PunningRemover.class );

    /**
     * Initialize the transformer
     *
     * @param mappingConfiguration the context mapping configuration
     */
    public PunningRemover( final MappingConfiguration mappingConfiguration ) {
        this.mappingConfiguration = mappingConfiguration;
    }

    /**
     * Apply this transformer to the given input graph
     *
     * @param graph the input graph
     * @return the resulting graph that has no more nodes with duplicate {@link Node.Id}s due to punning
     */
    @Override
    public Set<GraphElement> apply( final Set<GraphElement> graph ) {
        LOG.debug( "Removing punning in {}", graph );
        @SuppressWarnings( "OptionalGetWithoutIsPresent" ) final Set<GraphElement> result = graph.stream()
            .filter( GraphElement::isNode )
            .map( GraphElement::asNode )
            .filter( node -> node.getId().getIri().isPresent() )
            .collect( Collectors.groupingBy( node -> node.getId().getIri().get(), Collectors.counting() ) )
            .entrySet().stream()
            .filter( entry -> entry.getValue() > 1 )
            .map( Map.Entry::getKey )
            .flatMap( iri -> findNodesWithIri( graph, iri ).flatMap( node -> updateNode( graph, node ) ) )
            .reduce( ChangeSet.EMPTY, ChangeSet::merge )
            .applyTo( graph );
        LOG.debug( "Processed graph: {}", graph );
        return result;
    }

    private Stream<ChangeSet> updateNode( final Set<GraphElement> graph, final Node node ) {
        final Node newNode = node.withId( buildNewNodeId( node.getId() ) );
        final ChangeSet updatedToEdges = updateEdgesTo( graph, node, newNode );
        final ChangeSet updatedNode = new ChangeSet( Set.of( newNode ), Set.of( node ) );
        return Stream.of( updatedNode, updatedToEdges );
    }

    private Node.Id buildNewNodeId( final Node.Id original ) {
        return original.getIri().map( iri ->
                mappingConfiguration.getIdentifierMapper().getSyntheticIdForIri( iri ) )
            .orElseGet( () -> mappingConfiguration.getIdentifierMapper().getSyntheticId() );
    }
}
