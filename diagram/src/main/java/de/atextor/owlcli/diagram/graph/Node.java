package de.atextor.owlcli.diagram.graph;

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
