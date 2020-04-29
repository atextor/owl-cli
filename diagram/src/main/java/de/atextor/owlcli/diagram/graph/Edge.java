package de.atextor.owlcli.diagram.graph;

import lombok.EqualsAndHashCode;
import lombok.Value;

public abstract class Edge implements GraphElement {
    public enum Type {
        DEFAULT_ARROW,
        HOLLOW_ARROW,
        DOUBLE_ENDED_HOLLOW_ARROW,
        INVERSE_HOLLOW_ARROW,
        DASHED_ARROW,
        NO_ARROW
    }

    private Edge() {
    }

    public abstract Node.Id getFrom();

    public abstract Node.Id getTo();

    public abstract Edge.Type getType();

    public abstract Edge setFrom( Node.Id newFromId );

    public abstract Edge setTo( Node.Id newToId );

    @Override
    public boolean isEdge() {
        return true;
    }

    @Override
    public Edge asEdge() {
        return this;
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Plain extends Edge {
        Type type;
        Node.Id from;
        Node.Id to;

        @Override
        public Edge setFrom( final Node.Id newFromId ) {
            return new Plain( type, newFromId, to );
        }

        @Override
        public Edge setTo( final Node.Id newToId ) {
            return new Plain( type, from, newToId );
        }

        @Override
        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Decorated extends Edge {
        public static final Decoration CLASS = new Decoration.ClassSymbol();
        public static final Decoration OBJECT_PROPERTY = new Decoration.ObjectSymbol();
        public static final Decoration DATA_PROPERTY = new Decoration.DataSymbol();
        public static final Decoration DATA_RANGE = new Decoration.DataRangeSymbol();
        public static final Decoration INDIVIDUAL = new Decoration.IndividualSymbol();
        public static final Decoration LITERAL = new Decoration.LiteralSymbol();
        public static final Decoration RANGE = new Decoration.Label( "range" );
        public static final Decoration DOMAIN = new Decoration.Label( "domain" );

        Type type;
        Node.Id from;
        Node.Id to;
        Decoration decoration;

        @Override
        public Edge setFrom( final Node.Id newFromId ) {
            return new Decorated( type, newFromId, to, decoration );
        }

        @Override
        public Edge setTo( final Node.Id newToId ) {
            return new Decorated( type, from, newToId, decoration );
        }

        @Override
        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }
}
