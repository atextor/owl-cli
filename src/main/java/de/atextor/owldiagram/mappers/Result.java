package de.atextor.owldiagram.mappers;

import de.atextor.owldiagram.graph.GraphElement;
import de.atextor.owldiagram.graph.Node;
import lombok.Value;

import java.util.stream.Stream;

@Value
public class Result {
    Node node;
    Stream<GraphElement> remainingElements;

    public static Result of( final Node node ) {
        return new Result( node, Stream.empty() );
    }

    public Result and( final Result other ) {
        final Stream<GraphElement> otherParts = Stream.concat( Stream.of( other.getNode() ),
            other.getRemainingElements() );
        return new Result( node, Stream.concat( remainingElements, otherParts ) );
    }

    public Result and( final GraphElement element ) {
        return new Result( node, Stream.concat( Stream.of( element ), remainingElements ) );
    }

    public Stream<GraphElement> toStream() {
        return Stream.concat( Stream.of( node ), remainingElements );
    }
}
