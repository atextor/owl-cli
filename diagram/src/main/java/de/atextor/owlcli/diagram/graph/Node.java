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

package de.atextor.owlcli.diagram.graph;

import de.atextor.owlcli.diagram.graph.node.AnnotationProperty;
import de.atextor.owlcli.diagram.graph.node.Class;
import de.atextor.owlcli.diagram.graph.node.ClosedClass;
import de.atextor.owlcli.diagram.graph.node.Complement;
import de.atextor.owlcli.diagram.graph.node.DataExactCardinality;
import de.atextor.owlcli.diagram.graph.node.DataMaximalCardinality;
import de.atextor.owlcli.diagram.graph.node.DataMinimalCardinality;
import de.atextor.owlcli.diagram.graph.node.DataProperty;
import de.atextor.owlcli.diagram.graph.node.Datatype;
import de.atextor.owlcli.diagram.graph.node.DisjointUnion;
import de.atextor.owlcli.diagram.graph.node.Disjointness;
import de.atextor.owlcli.diagram.graph.node.Equality;
import de.atextor.owlcli.diagram.graph.node.ExistentialRestriction;
import de.atextor.owlcli.diagram.graph.node.IRIReference;
import de.atextor.owlcli.diagram.graph.node.Individual;
import de.atextor.owlcli.diagram.graph.node.Inequality;
import de.atextor.owlcli.diagram.graph.node.Intersection;
import de.atextor.owlcli.diagram.graph.node.Inverse;
import de.atextor.owlcli.diagram.graph.node.Invisible;
import de.atextor.owlcli.diagram.graph.node.Key;
import de.atextor.owlcli.diagram.graph.node.Literal;
import de.atextor.owlcli.diagram.graph.node.ObjectExactCardinality;
import de.atextor.owlcli.diagram.graph.node.ObjectMaximalCardinality;
import de.atextor.owlcli.diagram.graph.node.ObjectMinimalCardinality;
import de.atextor.owlcli.diagram.graph.node.ObjectProperty;
import de.atextor.owlcli.diagram.graph.node.ObjectQualifiedExactCardinality;
import de.atextor.owlcli.diagram.graph.node.ObjectQualifiedMaximalCardinality;
import de.atextor.owlcli.diagram.graph.node.ObjectQualifiedMinimalCardinality;
import de.atextor.owlcli.diagram.graph.node.PropertyChain;
import de.atextor.owlcli.diagram.graph.node.PropertyMarker;
import de.atextor.owlcli.diagram.graph.node.Rule;
import de.atextor.owlcli.diagram.graph.node.Self;
import de.atextor.owlcli.diagram.graph.node.Union;
import de.atextor.owlcli.diagram.graph.node.UniversalRestriction;
import de.atextor.owlcli.diagram.graph.node.ValueRestriction;
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

        T visit( Key key );

        T visit( Rule rule );
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

    @Override
    public <T> T accept( final GraphElement.Visitor<T> visitor ) {
        return visitor.visit( this );
    }

    public abstract Node.Id getId();

    public abstract <T> T accept( final Visitor<T> visitor );

    public abstract Node withId( Node.Id newId );

    @Override
    public boolean isNode() {
        return true;
    }

    @Override
    public Node asNode() {
        return this;
    }
}
