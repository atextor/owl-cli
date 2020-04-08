package de.atextor.owlcli.diagram.graph;

import lombok.Value;

@Value
public class DecoratedEdge implements Edge {
    public static final Decoration CLASS = new Decoration.ClassSymbol();
    public static final Decoration OBJECT_PROPERTY = new Decoration.ObjectSymbol();
    public static final Decoration DATA_PROPERTY = new Decoration.DataSymbol();
    public static final Decoration DATA_RANGE = new Decoration.DataRangeSymbol();
    public static final Decoration INDIVIDUAL = new Decoration.IndividualSymbol();
    public static final Decoration LITERAL = new Decoration.LiteralSymbol();
    public static final Decoration RANGE = new Decoration.Label( "range" );
    public static final Decoration DOMAIN = new Decoration.Label( "domain" );

    Edge.Type type;
    Node.Id from;
    Node.Id to;
    Decoration decoration;

    @Override
    public Edge setFrom( final Node.Id newFromId ) {
        return new DecoratedEdge( type, newFromId, to, decoration );
    }

    @Override
    public Edge setTo( final Node.Id newToId ) {
        return new DecoratedEdge( type, from, newToId, decoration );
    }

    @Override
    public <T> T accept( final Visitor<T> visitor ) {
        return visitor.visit( this );
    }
}
