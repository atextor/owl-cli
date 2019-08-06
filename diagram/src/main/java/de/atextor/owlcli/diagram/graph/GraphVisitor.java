package de.atextor.owlcli.diagram.graph;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.function.Function;

@AllArgsConstructor
@FieldDefaults( makeFinal = true, level = AccessLevel.PRIVATE )
public class GraphVisitor<T> implements GraphElement.Visitor<T> {
    NodeType.Visitor<T> nodeTypeVisitor;
    Function<PlainEdge, T> plainEdgeHandler;
    Function<DecoratedEdge, T> decoratedEdgeHandler;

    @Override
    public T visit( final PlainEdge edge ) {
        return plainEdgeHandler.apply( edge );
    }

    @Override
    public T visit( final DecoratedEdge decoratedEdge ) {
        return decoratedEdgeHandler.apply( decoratedEdge );
    }

    @Override
    public T visit( final NodeType nodeType ) {
        return nodeType.accept( nodeTypeVisitor );
    }
}
