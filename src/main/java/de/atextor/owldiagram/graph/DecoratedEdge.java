package de.atextor.owldiagram.graph;

import lombok.Value;

@Value
public class DecoratedEdge implements Edge {
    public static final Decoration CLASS = new Decoration.ClassSymbol();
    public static final Decoration ABSTRACT_ROLE = new Decoration.AbstractRoleSymbol();
    public static final Decoration CONCRETE_ROLE = new Decoration.ConcreteRoleSymbol();
    public static final Decoration DATA_RANGE = new Decoration.DataRangeSymbol();
    public static final Decoration INDIVIDUAL = new Decoration.IndividualSymbol();
    public static final Decoration LITERAL = new Decoration.LiteralSymbol();

    Edge.Type type;
    Node.Id from;
    Node.Id to;
    Decoration decoration;

    @Override
    public <T> T accept( final Visitor<T> visitor ) {
        return visitor.visit( this );
    }
}
