package de.atextor.owldiagram.graph;

public interface GraphElement {
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
}
