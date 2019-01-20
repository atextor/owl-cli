package de.atextor.owldiagram.mappers;

import de.atextor.owldiagram.graph.GraphElement;
import de.atextor.owldiagram.graph.Node;
import lombok.Value;

import java.util.stream.Stream;

@Value
public class MappingResult {
    Node node;
    Stream<GraphElement> remainingElements;

    public MappingResult and( final MappingResult other ) {
        return new MappingResult( node, Stream.concat( Stream.of( other.getNode() ),
                other.getRemainingElements() ) );
    }

    public MappingResult and( final GraphElement element ) {
        return new MappingResult( node, Stream.concat( Stream.of( element ), remainingElements ) );
    }

    public Stream<GraphElement> toStream() {
        return Stream.concat( Stream.of( node ), remainingElements );
    }
}
