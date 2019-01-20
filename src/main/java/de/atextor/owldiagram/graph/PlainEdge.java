package de.atextor.owldiagram.graph;

import lombok.Value;

@Value
public class PlainEdge implements Edge {
    Edge.Type type;
    Node.Id from;
    Node.Id to;
}
