package de.atextor.owlcli.diagram.graph;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.function.Function;

@AllArgsConstructor
@FieldDefaults( makeFinal = true, level = AccessLevel.PRIVATE )
public class GraphVisitor<T> implements GraphElement.Visitor<T> {
    Node.Visitor<T> nodeTypeVisitor;
    Function<Edge.Plain, T> plainEdgeHandler;
    Function<Edge.Decorated, T> decoratedEdgeHandler;

    @Override
    public T visit( final Edge.Plain edge ) {
        return plainEdgeHandler.apply( edge );
    }

    @Override
    public T visit( final Edge.Decorated decoratedEdge ) {
        return decoratedEdgeHandler.apply( decoratedEdge );
    }

    @Override
    public T visit( final Node nodeType ) {
        return nodeType.accept( nodeTypeVisitor );
    }
}
