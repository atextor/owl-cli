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

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.semanticweb.owlapi.model.IRI;

import java.util.Optional;
import java.util.Set;

/**
 * Sealed class that contains the different types of nodes of the ontology graph.
 */
@ToString
@EqualsAndHashCode
@FieldDefaults( makeFinal = true, level = AccessLevel.PRIVATE )
@Getter
public abstract class Node implements GraphElement {
    /**
     * Visitor for the nodes
     *
     * @param <T> the resulting type of the visit operation
     */
    public interface Visitor<T> {
        T visit( Class class_ );

        T visit( DataProperty dataProperty );

        T visit( ObjectProperty objectProperty );

        T visit( AnnotationProperty annotationProperty );

        T visit( Individual individual );

        T visit( Literal literal );

        T visit( PropertyChain propertyChain );

        T visit( Datatype datatype );

        T visit( ExistentialRestriction existentialRestriction );

        T visit( ValueRestriction valueRestriction );

        T visit( UniversalRestriction universalRestriction );

        T visit( Intersection intersection );

        T visit( Union union );

        T visit( Disjointness disjointness );

        T visit( DisjointUnion disjointness );

        T visit( Equality inequality );

        T visit( Inverse inverse );

        T visit( Inequality inequality );

        T visit( ClosedClass closedClass );

        T visit( Complement complement );

        T visit( Self self );

        T visit( ObjectMinimalCardinality objectMinimalCardinality );

        T visit( ObjectQualifiedMinimalCardinality objectQualifiedMinimalCardinality );

        T visit( ObjectMaximalCardinality objectMaximalCardinality );

        T visit( ObjectQualifiedMaximalCardinality objectQualifiedMaximalCardinality );

        T visit( ObjectExactCardinality objectExactCardinality );

        T visit( ObjectQualifiedExactCardinality objectQualifiedExactCardinality );

        T visit( DataMinimalCardinality dataMinimalCardinality );

        T visit( DataMaximalCardinality dataMaximalCardinality );

        T visit( DataExactCardinality dataExactCardinality );

        T visit( Invisible invisible );

        T visit( IRIReference iriReference );

        T visit( PropertyMarker propertyMarker );
    }

    /**
     * Id of a node that has the (unique) internal identifier string, and if present, the {@link IRI} of the
     * ontology element that is represented by the node having this Id.
     */
    @Getter
    @EqualsAndHashCode
    public static class Id {
        String id;
        Optional<IRI> iri;

        public Id( final String id, final IRI iri ) {
            this.id = id;
            this.iri = Optional.of( iri );
        }

        public Id( final String id ) {
            this.id = id;
            iri = Optional.empty();
        }

        @Override
        public String toString() {
            return "Id{" + "id='" + id + '\'' + ", iri=" + iri.map( IRI::toString ).orElse( "" ) + '}';
        }
    }

    public abstract static class NamedNode extends Node {
        public abstract String getName();
    }

    public abstract static class CardinalityNode extends Node {
        public abstract int getCardinality();
    }

    public abstract static class InvisibleNode extends Node {
    }

    private Node() {
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Class extends NamedNode {
        Id id;
        String name;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Class( newId, name );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class DataProperty extends NamedNode {
        Id id;
        String name;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new DataProperty( newId, name );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class ObjectProperty extends NamedNode {
        Id id;
        String name;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new ObjectProperty( newId, name );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class AnnotationProperty extends NamedNode {
        Id id;
        String name;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new AnnotationProperty( newId, name );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Individual extends NamedNode {
        Id id;
        String name;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Individual( newId, name );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Datatype extends NamedNode {
        Id id;
        String name;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Datatype( newId, name );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Literal extends Node {
        Id id;
        String value;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Literal( newId, value );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class PropertyChain extends Node {
        public static final String OPERATOR_SYMBOL = "o";

        Id id;
        String value;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Literal( newId, value );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class ExistentialRestriction extends Node {
        Id id;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new ExistentialRestriction( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class ValueRestriction extends Node {
        Id id;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new ValueRestriction( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class UniversalRestriction extends Node {
        Id id;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new UniversalRestriction( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Intersection extends Node {
        Id id;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Intersection( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Union extends Node {
        Id id;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Union( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Disjointness extends Node {
        Id id;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Disjointness( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class DisjointUnion extends Node {
        Id id;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new DisjointUnion( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Equality extends Node {
        Id id;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Equality( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Inverse extends Node {
        Id id;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Inverse( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Inequality extends Node {
        Id id;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Inequality( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class ClosedClass extends Node {
        Id id;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new ClosedClass( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Complement extends Node {
        Id id;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Complement( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Self extends Node {
        Id id;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Self( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class ObjectMinimalCardinality extends CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new ObjectMinimalCardinality( newId, cardinality );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class ObjectQualifiedMinimalCardinality extends CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new ObjectQualifiedMinimalCardinality( newId, cardinality );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class ObjectMaximalCardinality extends CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new ObjectMaximalCardinality( newId, cardinality );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class ObjectQualifiedMaximalCardinality extends CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new ObjectQualifiedMaximalCardinality( newId, cardinality );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class ObjectExactCardinality extends CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new ObjectExactCardinality( newId, cardinality );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class ObjectQualifiedExactCardinality extends CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new ObjectQualifiedExactCardinality( newId, cardinality );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class DataMinimalCardinality extends CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new DataMinimalCardinality( newId, cardinality );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class DataMaximalCardinality extends CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new DataMaximalCardinality( newId, cardinality );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class DataExactCardinality extends CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new DataExactCardinality( newId, cardinality );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Invisible extends InvisibleNode {
        Id id;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Invisible( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class IRIReference extends InvisibleNode {
        Id id;
        IRI iri;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new IRIReference( newId, iri );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class PropertyMarker extends Node {
        public enum Kind {
            FUNCTIONAL,
            INVERSE_FUNCTIONAL,
            TRANSITIVE,
            SYMMETRIC,
            ASYMMETRIC,
            REFLEXIVE,
            IRREFLEXIVE
        }

        Id id;
        Set<Kind> kind;

        @Override
        public <T> T accept( final Node.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Invisible( newId );
        }
    }

    @Override
    public <T> T accept( final GraphElement.Visitor<T> visitor ) {
        return visitor.visit( this );
    }

    public abstract Node.Id getId();

    public abstract <T> T accept( final Visitor<T> visitor );

    public abstract Node clone( Node.Id newId );

    @Override
    public boolean isNode() {
        return true;
    }

    @Override
    public Node asNode() {
        return this;
    }
}
