package de.atextor.owldiagram.graph;

public interface Edge extends GraphElement {
    enum Type {
        DEFAULT_ARROW,
        HOLLOW_ARROW,
        DOUBLE_ENDED_HOLLOW_ARROW,
        DASHED_ARROW
    }

    Node.Id getFrom();

    Node.Id getTo();

    @Override
    default boolean isEdge() {
        return true;
    }

    @Override
    default Edge asEdge() {
        return this;
    }
}
