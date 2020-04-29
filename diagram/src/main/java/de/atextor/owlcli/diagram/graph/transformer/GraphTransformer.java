package de.atextor.owlcli.diagram.graph.transformer;

import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import org.semanticweb.owlapi.model.IRI;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public abstract class GraphTransformer implements UnaryOperator<Set<GraphElement>> {
    protected Stream<Node> findNodesWithIri( final Set<GraphElement> graph, final IRI iri ) {
        return graph.stream()
            .filter( GraphElement::isNode )
            .map( GraphElement::asNode )
            .filter( node -> node.getId().getIri().map( nodeIri -> nodeIri.equals( iri ) ).orElse( false ) );
    }

    protected ChangeSet updateEdgesTo( final Set<GraphElement> graph, final Node oldToNode, final Node newToNode ) {
        return updateEdge( graph, oldToNode, newToNode, Edge::getTo, Edge::setTo );
    }

    protected ChangeSet updateEdgesFrom( final Set<GraphElement> graph, final Node oldFromNode,
                                         final Node newFromNode ) {
        return updateEdge( graph, oldFromNode, newFromNode, Edge::getFrom, Edge::setFrom );
    }

    private ChangeSet updateEdge( final Set<GraphElement> graph, final Node oldNode, final Node newNode,
                                  final Function<Edge, Node.Id> getter,
                                  final BiFunction<Edge, Node.Id, Edge> setter ) {
        return graph.stream()
            .filter( GraphElement::isEdge )
            .map( GraphElement::asEdge )
            .filter( edge -> getter.apply( edge ).equals( oldNode.getId() ) )
            .map( edge -> new ChangeSet( Set.of( setter.apply( edge, newNode.getId() ) ), Set.of( edge ) ) )
            .reduce( ChangeSet.EMPTY, ChangeSet::merge );
    }
}
