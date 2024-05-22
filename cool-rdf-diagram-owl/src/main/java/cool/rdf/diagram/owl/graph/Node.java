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

package cool.rdf.diagram.owl.graph;

import cool.rdf.diagram.owl.graph.node.AnnotationProperty;
import cool.rdf.diagram.owl.graph.node.Class;
import cool.rdf.diagram.owl.graph.node.ClosedClass;
import cool.rdf.diagram.owl.graph.node.Complement;
import cool.rdf.diagram.owl.graph.node.DataExactCardinality;
import cool.rdf.diagram.owl.graph.node.DataMaximalCardinality;
import cool.rdf.diagram.owl.graph.node.DataMinimalCardinality;
import cool.rdf.diagram.owl.graph.node.DataProperty;
import cool.rdf.diagram.owl.graph.node.Datatype;
import cool.rdf.diagram.owl.graph.node.DisjointUnion;
import cool.rdf.diagram.owl.graph.node.Disjointness;
import cool.rdf.diagram.owl.graph.node.Equality;
import cool.rdf.diagram.owl.graph.node.ExistentialRestriction;
import cool.rdf.diagram.owl.graph.node.IRIReference;
import cool.rdf.diagram.owl.graph.node.Individual;
import cool.rdf.diagram.owl.graph.node.Inequality;
import cool.rdf.diagram.owl.graph.node.Intersection;
import cool.rdf.diagram.owl.graph.node.Inverse;
import cool.rdf.diagram.owl.graph.node.Invisible;
import cool.rdf.diagram.owl.graph.node.Key;
import cool.rdf.diagram.owl.graph.node.Literal;
import cool.rdf.diagram.owl.graph.node.ObjectExactCardinality;
import cool.rdf.diagram.owl.graph.node.ObjectMaximalCardinality;
import cool.rdf.diagram.owl.graph.node.ObjectMinimalCardinality;
import cool.rdf.diagram.owl.graph.node.ObjectProperty;
import cool.rdf.diagram.owl.graph.node.ObjectQualifiedExactCardinality;
import cool.rdf.diagram.owl.graph.node.ObjectQualifiedMaximalCardinality;
import cool.rdf.diagram.owl.graph.node.ObjectQualifiedMinimalCardinality;
import cool.rdf.diagram.owl.graph.node.PropertyChain;
import cool.rdf.diagram.owl.graph.node.PropertyMarker;
import cool.rdf.diagram.owl.graph.node.Rule;
import cool.rdf.diagram.owl.graph.node.Self;
import cool.rdf.diagram.owl.graph.node.Union;
import cool.rdf.diagram.owl.graph.node.UniversalRestriction;
import cool.rdf.diagram.owl.graph.node.ValueRestriction;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.semanticweb.owlapi.model.IRI;

import java.util.Optional;

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
        /**
         * Visit a class object
         *
         * @param class_ the object
         * @return the visitor's return value
         */
        T visit( Class class_ );

        /**
         * Visit a data property object
         *
         * @param dataProperty the data property
         * @return the visitor's return value
         */
        T visit( DataProperty dataProperty );

        /**
         * Visit an object property object
         *
         * @param objectProperty the object property
         * @return the visitor's return value
         */
        T visit( ObjectProperty objectProperty );

        /**
         * Visit an annotation property object
         *
         * @param annotationProperty the annotation property
         * @return the visitor's return value
         */
        T visit( AnnotationProperty annotationProperty );

        /**
         * Visit an OWL individual object
         *
         * @param individual the individual
         * @return the visitor's return value
         */
        T visit( Individual individual );

        /**
         * Visit a literal
         *
         * @param literal the literal
         * @return the visitor's return value
         */
        T visit( Literal literal );

        /**
         * Visit an object property chain
         *
         * @param propertyChain the property chain
         * @return the visitor's return value
         */
        T visit( PropertyChain propertyChain );

        /**
         * Visit a data type
         *
         * @param datatype the data type
         * @return the visitor's return value
         */
        T visit( Datatype datatype );

        /**
         * Visit an existential restriction
         *
         * @param existentialRestriction the restriction
         * @return the visitor's return value
         */
        T visit( ExistentialRestriction existentialRestriction );

        /**
         * Visit a value restriction
         *
         * @param valueRestriction the restriction
         * @return the visitor's return value
         */
        T visit( ValueRestriction valueRestriction );

        /**
         * Visit a universal restriction
         *
         * @param universalRestriction the restriction
         * @return the visitor's return value
         */
        T visit( UniversalRestriction universalRestriction );

        /**
         * Visit a class intersection
         *
         * @param intersection the intersection
         * @return the visitor's return value
         */
        T visit( Intersection intersection );

        /**
         * Visit a class union
         *
         * @param union the union
         * @return the visitor's return value
         */
        T visit( Union union );

        /**
         * Visit a disjointness axiom
         *
         * @param disjointness the axiom
         * @return the visitor's return value
         */
        T visit( Disjointness disjointness );

        /**
         * Visit a disjoint union axiom
         *
         * @param disjointUnion the axiom
         * @return the visitor's return value
         */
        T visit( DisjointUnion disjointUnion );

        /**
         * Visit a class inequality axiom
         *
         * @param inequality the axiom
         * @return the visitor's return value
         */
        T visit( Equality inequality );

        /**
         * Visit an inverse axiom
         *
         * @param inverse the axiom
         * @return the visitor's return value
         */
        T visit( Inverse inverse );

        /**
         * Visit a class inequality axiom
         *
         * @param inequality the axiom
         * @return the visitor's return value
         */
        T visit( Inequality inequality );

        /**
         * Visit a closed class axiom
         *
         * @param closedClass the axiom
         * @return the visitor's return value
         */
        T visit( ClosedClass closedClass );

        /**
         * Visit a complement axiom
         *
         * @param complement the axiom
         * @return the visitor's return value
         */
        T visit( Complement complement );

        /**
         * Visit a self axiom
         *
         * @param self the axiom
         * @return the visitor's return value
         */
        T visit( Self self );

        /**
         * Visit a minimal cardinality restriction
         *
         * @param objectMinimalCardinality the restriction
         * @return the visitor's return value
         */
        T visit( ObjectMinimalCardinality objectMinimalCardinality );

        /**
         * Visit a qualified minimal cardinality restriction
         *
         * @param objectQualifiedMinimalCardinality the restriction
         * @return the visitor's return value
         */
        T visit( ObjectQualifiedMinimalCardinality objectQualifiedMinimalCardinality );

        /**
         * Visit a maximal cardinality restriction
         *
         * @param objectMaximalCardinality the restriction
         * @return the visitor's return value
         */
        T visit( ObjectMaximalCardinality objectMaximalCardinality );

        /**
         * Visit a qualified maximal cardinality restriction
         *
         * @param objectQualifiedMaximalCardinality the restriction
         * @return the visitor's return value
         */
        T visit( ObjectQualifiedMaximalCardinality objectQualifiedMaximalCardinality );

        /**
         * Visit an exact cardinality restriction
         *
         * @param objectExactCardinality the restriction
         * @return the visitor's return value
         */
        T visit( ObjectExactCardinality objectExactCardinality );

        /**
         * Visit a qualified exact cardinality restriction
         *
         * @param objectQualifiedExactCardinality the restriction
         * @return the visitor's return value
         */
        T visit( ObjectQualifiedExactCardinality objectQualifiedExactCardinality );

        /**
         * Visit a data minimal cardinality restriction
         *
         * @param dataMinimalCardinality the restriction
         * @return the visitor's return value
         */
        T visit( DataMinimalCardinality dataMinimalCardinality );

        /**
         * Visit a data maximal cardinality restriction
         *
         * @param dataMaximalCardinality the restriction
         * @return the visitor's return value
         */
        T visit( DataMaximalCardinality dataMaximalCardinality );

        /**
         * Visit a data exact cardinality restriction
         *
         * @param dataExactCardinality the restriction
         * @return the visitor's return value
         */
        T visit( DataExactCardinality dataExactCardinality );

        /**
         * Visit an invisible node
         *
         * @param invisible the node
         * @return the visitor's return value
         */
        T visit( Invisible invisible );

        /**
         * Visit an IRI reference node
         *
         * @param iriReference the node
         * @return the visitor's return value
         */
        T visit( IRIReference iriReference );

        /**
         * Visit a property marker node
         *
         * @param propertyMarker the marker
         * @return the visitor's return value
         */
        T visit( PropertyMarker propertyMarker );

        /**
         * Visit a key node
         *
         * @param key the node
         * @return the visitor's return value
         */
        T visit( Key key );

        /**
         * Visit a rule node
         *
         * @param rule the node
         * @return the visitor's return value
         */
        T visit( Rule rule );
    }

    /**
     * ID of a node that has the (unique) internal identifier string, and if present, the {@link IRI} of the
     * ontology element that is represented by the node having this ID.
     */
    @Getter
    @EqualsAndHashCode
    public static class Id {
        final String id;

        final Optional<IRI> iri;

        /**
         * Constructs an ID from an internal identifier (id) and stores the IRI of the element identified by the Id
         *
         * @param id the internal identifier
         * @param iri the IRI of the identified element
         */
        public Id( final String id, final IRI iri ) {
            this.id = id;
            this.iri = Optional.of( iri );
        }

        /**
         * Constructs an ID from an internal identifier
         *
         * @param id the id
         */
        public Id( final String id ) {
            this.id = id;
            iri = Optional.empty();
        }

        @Override
        public String toString() {
            return "Id{" + "id='" + id + '\'' + ", iri=" + iri.map( IRI::toString ).orElse( "" ) + '}';
        }
    }

    /**
     * A node with a name
     */
    public abstract static class NamedNode extends Node {
        /**
         * The name of this node
         *
         * @return the name
         */
        public abstract String getName();
    }

    /**
     * A node representing a cardinality
     */
    public abstract static class CardinalityNode extends Node {
        /**
         * The cardinality
         *
         * @return the cardinality
         */
        public abstract int getCardinality();
    }

    /**
     * An invisible node (without label or border)
     */
    public abstract static class InvisibleNode extends Node {
    }

    @Override
    public <T> T accept( final GraphElement.Visitor<T> visitor ) {
        return visitor.visit( this );
    }

    /**
     * The ID of this node
     *
     * @return the ID
     */
    public abstract Id getId();

    /**
     * Accepts visitors following the visitor pattern
     *
     * @param visitor the visitor
     * @param <T> the visitor's return type
     * @return the value returned by the visitor
     */
    public abstract <T> T accept( final Visitor<T> visitor );

    /**
     * Constructs a copy of this node with a changed ID
     *
     * @param newId the new id
     * @return the new node
     */
    public abstract Node withId( Id newId );

    @Override
    public boolean isNode() {
        return true;
    }

    @Override
    public Node asNode() {
        return this;
    }
}
