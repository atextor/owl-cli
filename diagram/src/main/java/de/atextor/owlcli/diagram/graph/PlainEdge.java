package de.atextor.owlcli.diagram.graph;

import lombok.Value;

@Value
public class PlainEdge implements Edge {
    Edge.Type type;
    Node.Id from;
    Node.Id to;

    @Override
    public <T> T accept( final Visitor<T> visitor ) {
        return visitor.visit( this );
    }
}
