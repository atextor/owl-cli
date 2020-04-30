package de.atextor.owlcli.diagram.graph;

import java.util.stream.Stream;

public interface GraphElement {
    interface Visitor<T> {
        T visit( Edge.Plain edge );

        T visit( Edge.Decorated decoratedEdge );

        T visit( Node nodeType );
    }

    default boolean isEdge() {
        return false;
    }

    default boolean isNode() {
        return false;
    }

    default <T extends GraphElement> boolean is( final Class<T> class_ ) {
        return class_.isAssignableFrom( getClass() );
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
