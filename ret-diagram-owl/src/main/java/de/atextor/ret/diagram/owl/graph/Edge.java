/*
 * Copyright 2021 Andreas Textor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.atextor.ret.diagram.owl.graph;

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

    public abstract Type getType();

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

            final String label;

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
