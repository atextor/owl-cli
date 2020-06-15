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

/**
 * Sealed class that represents the types of edges in the graph
 */
public abstract class Edge implements GraphElement {
    public enum Type {
        DEFAULT_ARROW,
        HOLLOW_ARROW,
        DOUBLE_ENDED_HOLLOW_ARROW,
        DASHED_ARROW,
        NO_ARROW
    }

    private Edge() {
    }

    public abstract Node getFrom();

    public abstract Node getTo();

    public abstract Edge.Type getType();

    public abstract Edge setFrom( Node newFromId );

    public abstract Edge setTo( Node newToId );

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
        Node from;
        Node to;

        @Override
        public Edge setFrom( final Node newFrom ) {
            return new Plain( type, newFrom, to );
        }

        @Override
        public Edge setTo( final Node newTo ) {
            return new Plain( type, from, newTo );
        }

        @Override
        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Decorated extends Edge {
        public enum Label {
            CLASS( "C" ),
            OBJECT_PROPERTY( "P" ),
            DATA_PROPERTY( "P" ),
            DATA_RANGE( "C" ),
            INDIVIDUAL( "v" ),
            LITERAL( "v" ),
            RANGE( "range" ),
            DOMAIN( "domain" );

            String label;

            Label( final String label ) {
                this.label = label;
            }

            public String getLabel() {
                return label;
            }
        }

        Type type;
        Node from;
        Node to;
        Label label;

        @Override
        public Edge setFrom( final Node newFrom ) {
            return new Decorated( type, newFrom, to, label );
        }

        @Override
        public Edge setTo( final Node newTo ) {
            return new Decorated( type, from, newTo, label );
        }

        @Override
        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }
}
