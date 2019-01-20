package de.atextor.owldiagram.graph;

import lombok.Value;

public interface Node extends GraphElement {
    @Value
    class Id {
        String id;
    }

    Id getId();

    @Override
    default boolean isNode() {
        return true;
    }

    @Override
    default Node asNode() {
        return this;
    }
}
