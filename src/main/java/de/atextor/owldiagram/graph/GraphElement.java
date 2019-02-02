package de.atextor.owldiagram.graph;

public interface GraphElement {
    interface Visitor<T> {
        T visit( PlainEdge edge );

        T visit( DecoratedEdge decoratedEdge );

        T visit( NodeType nodeType );
    }

    default boolean isEdge() {
        return false;
    }

    default boolean isNode() {
        return false;
    }

    default Node asNode() {
        throw new UnsupportedOperationException();
    }

    default Edge asEdge() {
        throw new UnsupportedOperationException();
    }

    <T> T accept( Visitor<T> visitor );
}
