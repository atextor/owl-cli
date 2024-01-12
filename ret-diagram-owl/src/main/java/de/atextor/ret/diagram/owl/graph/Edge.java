/*
 * Copyright 2024 Andreas Textor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
    /**
     * The type of edge
     */
    public enum Type {
        /**
         * Default filled black arrow, indicating default relation
         */
        DEFAULT_ARROW,

        /**
         * Hollow tipped arrow, indicating inheritance
         */
        HOLLOW_ARROW,

        /**
         * Double ended hollow tipped arrow, indicating equivalence
         */
        DOUBLE_ENDED_HOLLOW_ARROW,

        /**
         * Dashed arrow, indicating implicit relation
         */
        DASHED_ARROW,

        /**
         * Edge without arrows
         */
        NO_ARROW
    }

    private Edge() {
    }

    /**
     * Returns the "from" node
     *
     * @return the node
     */
    public abstract Node getFrom();

    /**
     * Returns the "to" node
     *
     * @return the node
     */
    public abstract Node getTo();

    /**
     * Returns the edge's type
     *
     * @return the type
     */
    public abstract Type getType();

    /**
     * Returns a new edge with the "from" node updated to a new value
     *
     * @param newFromId the new "from" node id
     * @return the new edge
     */
    public abstract Edge setFrom( Node newFromId );

    /**
     * Returns a new edge with the "to" node updated to a new value
     *
     * @param newToId the new "to" node id
     * @return the new edge
     */
    public abstract Edge setTo( Node newToId );

    @Override
    public boolean isEdge() {
        return true;
    }

    @Override
    public Edge asEdge() {
        return this;
    }

    /**
     * A plain edge (i.e., which does not have a label)
     */
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

    /**
     * An Edge with an attached label
     */
    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Decorated extends Edge {
        /**
         * The possible labels
         */
        public enum Label {
            /**
             * Indicates an edge pointing to a class
             */
            CLASS( "C" ),

            /**
             * Indicates an edge pointing to an object property
             */
            OBJECT_PROPERTY( "P" ),

            /**
             * Indicates an edge pointing to a data property
             */
            DATA_PROPERTY( "P" ),

            /**
             * Indicates an edge pointing to a (data) class
             */
            DATA_RANGE( "C" ),

            /**
             * Indicates an edge pointing to an individual
             */
            INDIVIDUAL( "v" ),

            /**
             * Indicates an edge pointing to a literal value
             */
            LITERAL( "v" ),

            /**
             * Indicates an edge pointing to a given data range
             */
            RANGE( "range" ),

            /**
             * Indicates an edge pointing to a given data domain
             */
            DOMAIN( "domain" );

            final String label;

            Label( final String label ) {
                this.label = label;
            }

            /**
             * Returns the label string
             *
             * @return the label string
             */
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
