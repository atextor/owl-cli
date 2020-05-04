/*
 * This file is part of OWL-CLI.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright (c) 2020, Andreas Textor.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www
 * .gnu.org/licenses/.
 */

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
        public static final String CLASS_LABEL = "C";
        public static final String OBJECT_PROPERTY_LABEL = "P";
        public static final String DATA_PROPERTY_LABEL = "P";
        public static final String DATA_RANGE_LABEL = "C";
        public static final String INDIVIDUAL_LABEL = "v";
        public static final String LITERAL_LABEL = "v";
        public static final String RANGE_LABEL = "range";
        public static final String DOMAIN_LABEL = "domain";

        Type type;
        Node.Id from;
        Node.Id to;
        String label;

        @Override
        public Edge setFrom( final Node.Id newFromId ) {
            return new Decorated( type, newFromId, to, label );
        }

        @Override
        public Edge setTo( final Node.Id newToId ) {
            return new Decorated( type, from, newToId, label );
        }

        @Override
        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }
}
