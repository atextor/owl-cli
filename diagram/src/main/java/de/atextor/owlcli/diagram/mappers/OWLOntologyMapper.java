package de.atextor.owlcli.diagram.mappers;

import com.google.common.collect.Sets;
import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.Graph;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.NodeType;
import io.vavr.Tuple2;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OWLOntologyMapper implements Function<OWLOntology, Stream<GraphElement>> {
    private final MappingConfiguration mappingConfiguration;

    public OWLOntologyMapper( final MappingConfiguration mappingConfiguration ) {
        this.mappingConfiguration = mappingConfiguration;
    }

    private Stream<GraphElement> resolveIriReferences( final Set<GraphElement> graph ) {
        final Map<Edge, NodeType.IRIReference> references = getReferences( graph );

        if ( references.isEmpty() ) {
            return graph.stream();
        }

        final Set<GraphElement> resolvedReferences = resolveReferences( graph, references );
        return mergeResolvedReferences( graph, references, resolvedReferences ).stream();
    }

    private Set<GraphElement> mergeResolvedReferences( final Set<GraphElement> graph,
                                                       final Map<Edge, NodeType.IRIReference> references,
                                                       final Set<GraphElement> resolvedReferences ) {
        final Set<GraphElement> elementsToRemove = references.entrySet().stream().flatMap( entry -> {
            final Edge edge = entry.getKey();
            final NodeType.IRIReference reference = entry.getValue();
            final Stream<Node> referencedNodes = findNodesWithIri( graph, reference.getIri() );
            return Stream.concat( Stream.of( edge, reference ), referencedNodes );
        } ).collect( Collectors.toSet() );
        final Set<GraphElement> graphWithoutReferences = Sets.difference( graph, elementsToRemove );
        return Sets.union( graphWithoutReferences, resolvedReferences );
    }

    private Set<GraphElement> resolveReferences( final Set<GraphElement> graph,
                                                 final Map<Edge, NodeType.IRIReference> referenceMap ) {
        return referenceMap.entrySet().stream().flatMap( entry -> {
            final Edge edge = entry.getKey();
            final NodeType.IRIReference reference = entry.getValue();
            final Set<Node> referencedNodes =
                findNodesWithIri( graph, reference.getIri() ).collect( Collectors.toSet() );

            if ( referencedNodes.isEmpty() ) {
                final Node literal = turnReferenceIntoLiteral( reference );
                return Stream.of( edge.setTo( literal.getId() ), literal );
            }

            return updateReferenceElements( edge, referencedNodes );
        } ).collect( Collectors.toSet() );
    }

    private Stream<GraphElement> updateReferenceElements( final Edge edge, final Set<Node> referencedNodes ) {
        return referencedNodes.stream().flatMap( node -> {
            final Stream<Node> classView = node.view( NodeType.Class.class ).map( classNode ->
                new NodeType.Class( buildNewNodeId( classNode.getId() ), classNode.getName() ) );
            final Stream<Node> individualView = node.view( NodeType.Individual.class ).map( indivualNode ->
                new NodeType.Individual( buildNewNodeId( indivualNode.getId() ), indivualNode.getName() ) );

            return Stream.concat( classView, individualView )
                .flatMap( updatedNode -> Stream.of( edge.setTo( updatedNode.getId() ), updatedNode ) );
        } );
    }

    private Node.Id buildNewNodeId( final Node.Id original ) {
        return original.getIri().map( iri ->
            mappingConfiguration.getIdentifierMapper().getSyntheticIdForIri( iri ) )
            .orElseGet( () -> mappingConfiguration.getIdentifierMapper().getSyntheticId() );
    }

    private NodeType.Literal turnReferenceIntoLiteral( final NodeType.IRIReference reference ) {
        return new NodeType.Literal(
            mappingConfiguration.getIdentifierMapper().getSyntheticId(),
            mappingConfiguration.getNameMapper().getName( reference.getIri() ) );
    }

    private Stream<Node> findNodesWithIri( final Set<GraphElement> graph, final IRI iri ) {
        return graph.stream()
            .filter( GraphElement::isNode )
            .map( GraphElement::asNode )
            .filter( node -> node.getId().getIri().map( nodeIri -> nodeIri.equals( iri ) ).orElse( false ) );
    }

    private Map<Edge, NodeType.IRIReference> getReferences( final Set<GraphElement> graph ) {
        return graph.stream()
            .filter( GraphElement::isEdge )
            .map( GraphElement::asEdge )
            .flatMap( edge -> getReferenceThatEdgeLeadsTo( graph, edge ).map( node -> new Tuple2<>( edge, node ) ).stream() )
            .collect( Collectors.toMap( tuple -> tuple._1, tuple -> tuple._2 ) );
    }

    private Optional<NodeType.IRIReference> getReferenceThatEdgeLeadsTo( final Set<GraphElement> graph,
                                                                         final Edge edge ) {
        return graph.stream()
            .flatMap( element -> element.view( NodeType.IRIReference.class ) )
            .filter( reference -> reference.getId().equals( edge.getTo() ) )
            .findAny();
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
