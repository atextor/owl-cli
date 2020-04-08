package de.atextor.owlcli.diagram.mappers;

import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.Graph;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.NodeType;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OWLOntologyMapper implements Function<OWLOntology, Stream<GraphElement>> {
    private final MappingConfiguration mappingConfiguration;

    public OWLOntologyMapper( final MappingConfiguration mappingConfiguration ) {
        this.mappingConfiguration = mappingConfiguration;
    }


    /**
     * For an edge to an {@link NodeType.IRIReference}, find all nodes that have the IRI of the IRIReference as part of
     * their id. Due to punning, this can actually be more than one node, e.g. there can be a {@link NodeType.Class}
     * and a {@link NodeType.Individual} with the same IRI in their ids.
     * Replace the edge to the IRIReference node with edges to each found referenced node.
     * If no referenced node is found, replace the IRIReference with a Literal node containing the IRI and
     * an updated edge to the literal.
     *
     * @param edge              the edge to an {@link NodeType.IRIReference}
     * @param unchangedElements the graph elements that are not being replaced
     * @param referenceElements the graph elements describing references, i.e. the IRIReferences and edges to them
     * @return the updated referenceElements that contain no IRIReferences any more
     */
    private Stream<GraphElement> resolveEdgeToIriReference( final Edge edge,
                                                            final List<GraphElement> unchangedElements,
                                                            final List<GraphElement> referenceElements ) {
        return referenceElements.stream()
            .flatMap( element -> element.view( NodeType.IRIReference.class ) )
            .flatMap( iriReference -> {
                final Set<Node> referencedNodes = unchangedElements.stream()
                    .filter( GraphElement::isNode )
                    .map( GraphElement::asNode )
                    .filter( node -> node.getId().getIri().map( iri -> iri.equals( iriReference.getIri() ) )
                        .orElse( false ) )
                    .collect( Collectors.toSet() );

                if ( referencedNodes.isEmpty() ) {
                    final NodeType.Literal iriLiteral = new NodeType.Literal(
                        mappingConfiguration.getIdentifierMapper().getSyntheticId(),
                        mappingConfiguration.getNameMapper().getName( iriReference.getIri() ) );
                    return Stream.of( iriLiteral, edge.setTo( iriLiteral.getId() ) );
                } else {
                    return referencedNodes.stream()
                        .map( referencedNode -> edge.setTo( referencedNode.getId() ) );
                }
            } );
    }

    /**
     * Checks if a given {@link GraphElement} is a {@link NodeType.IRIReference} or the edge to one
     *
     * @param allElements all elements of the graph
     * @param element     the element to check
     * @return true if the element is a reference or the edge to one, otherwise false
     */
    private boolean isIriReferenceOrEdgeToIt( final Set<GraphElement> allElements, final GraphElement element ) {
        if ( element.is( NodeType.IRIReference.class ) ) {
            return true;
        }

        if ( element.isNode() ) {
            return false;
        }

        final Edge edge = element.asEdge();
        return allElements.stream()
            .flatMap( e -> e.view( NodeType.IRIReference.class ) )
            .anyMatch( e -> edge.getTo().equals( e.getId() ) );
    }

    /**
     * Replace IRIReferences and the edges to them with edges to the referenced nodes, if such nodes exist.
     * Otherwise turn the IRIReference into a Literal node.
     *
     * @param elements the elements on the graph that can contain {@link NodeType.IRIReference}s
     * @return elements of the graph where all references are resolved
     */
    private Stream<GraphElement> resolveIriReferences( final Set<GraphElement> elements ) {
        final Map<Boolean, List<GraphElement>> map = elements.stream()
            .collect( Collectors.partitioningBy( element -> isIriReferenceOrEdgeToIt( elements, element ) ) );

        final List<GraphElement> unchangedElements = map.get( false );
        final List<GraphElement> referenceElements = map.get( true );

        final Stream<GraphElement> updatedElements = referenceElements.stream()
            .filter( GraphElement::isEdge )
            .map( GraphElement::asEdge )
            .flatMap( edge -> resolveEdgeToIriReference( edge, unchangedElements, referenceElements ) );

        return Stream.concat( unchangedElements.stream(), updatedElements );
    }

    @Override
    public Stream<GraphElement> apply( final OWLOntology ontology ) {
        final Set<GraphElement> elements = ontology.axioms()
            .map( axiom -> axiom.accept( mappingConfiguration.getOwlAxiomMapper() ) )
            .flatMap( Graph::toStream )
            .collect( Collectors.toSet() );

        return resolveIriReferences( elements );
    }

}
