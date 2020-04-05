package de.atextor.owlcli.diagram.graph;

import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class Graph {
    Node node;
    Stream<GraphElement> otherElements;

    protected Graph( final Node node, final Stream<GraphElement> otherElements ) {
        this.node = node;
        this.otherElements = otherElements;
    }

    public static Graph of( final Node node, final Stream<GraphElement> otherElements ) {
        return new Graph( node, otherElements );
    }

    public static Graph of( final Node node ) {
        return new Graph( node, Stream.empty() );
    }

    public Graph and( final Graph other ) {
        return new Graph( node, Stream.concat( otherElements, other.toStream() ) );
    }

    public Graph and( final GraphElement element ) {
        return new Graph( node, Stream.concat( Stream.of( element ), otherElements ) );
    }

    public Graph and( final Stream<GraphElement> elements ) {
        return new Graph( node, Stream.concat( otherElements, elements ) );
    }

    public Stream<GraphElement> toStream() {
        return Stream.concat( Stream.of( node ), otherElements );
    }

    public Set<GraphElement> getElementSet() {
        return toStream().collect( Collectors.toSet() );
    }
}
