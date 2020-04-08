package de.atextor.owlcli.diagram.graph;

public interface Edge extends GraphElement {
    enum Type {
        DEFAULT_ARROW,
        HOLLOW_ARROW,
        DOUBLE_ENDED_HOLLOW_ARROW,
        INVERSE_HOLLOW_ARROW,
        DASHED_ARROW,
        NO_ARROW
    }

    Node.Id getFrom();

    Node.Id getTo();

    Edge.Type getType();

    Edge setFrom( Node.Id newFromId );

    Edge setTo( Node.Id newToId );

    @Override
    default boolean isEdge() {
        return true;
    }

    @Override
    default Edge asEdge() {
        return this;
    }
}
