package de.atextor.owlcli.diagram.graph;

import java.util.stream.Stream;

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

    default <T extends GraphElement> boolean is( final Class<T> class_ ) {
        return getClass().isAssignableFrom( class_ );
    }

    default <T extends GraphElement> T as( final Class<T> class_ ) {
        return class_.cast( this );
    }

    default <T extends GraphElement> Stream<T> view( final Class<T> class_ ) {
        return is( class_ ) ? Stream.of( as( class_ ) ) : Stream.empty();
    }

    default Node asNode() {
        throw new UnsupportedOperationException();
    }

    default Edge asEdge() {
        throw new UnsupportedOperationException();
    }

    <T> T accept( Visitor<T> visitor );
}
