package de.atextor.owlcli.diagram.graph;

import lombok.Value;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Value
public class Graph {
    Node node;
    Stream<GraphElement> otherElements;

    public static Graph of( final Node node ) {
        return new Graph( node, Stream.empty() );
    }

    public Graph and( final Graph other ) {
        return new Graph( node, Stream.concat( otherElements,
            Stream.concat( Stream.of( other.getNode() ), other.getOtherElements() ) ) );
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
