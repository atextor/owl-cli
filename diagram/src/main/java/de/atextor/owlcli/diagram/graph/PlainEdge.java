package de.atextor.owlcli.diagram.graph;

import lombok.Value;

@Value
public class PlainEdge implements Edge {
    Edge.Type type;
    Node.Id from;
    Node.Id to;

    @Override
    public Edge setFrom( final Node.Id newFromId ) {
        return new PlainEdge( type, newFromId, to );
    }

    @Override
    public Edge setTo( final Node.Id newToId ) {
        return new PlainEdge( type, from, newToId );
    }

    @Override
    public <T> T accept( final Visitor<T> visitor ) {
        return visitor.visit( this );
    }
}
