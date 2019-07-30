package de.atextor.owldiagram.graph;

public interface GraphElement {
    interface Visitor<T> {
        T visit( PlainEdge edge );

        T visit( DecoratedEdge decoratedEdge );

        T visit( NodeType nodeType );
    }

    class VisitorAdapter<T> implements Visitor<T> {
        private final T defaultValue;

        public VisitorAdapter( final T defaultValue ) {
            this.defaultValue = defaultValue;
        }

        @Override
        public T visit( final PlainEdge edge ) {
            return defaultValue;
        }

        @Override
        public T visit( final DecoratedEdge decoratedEdge ) {
            return defaultValue;
        }

        @Override
        public T visit( final NodeType nodeType ) {
            return defaultValue;
        }
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
