package de.atextor.owlcli.diagram.graph;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.semanticweb.owlapi.model.IRI;

import java.util.Set;

@ToString
@EqualsAndHashCode
@FieldDefaults( makeFinal = true, level = AccessLevel.PRIVATE )
@Getter
public abstract class NodeType implements Node {
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

    public static class VisitorAdapter<T> implements Visitor<T> {
        private final T defaultValue;

        public VisitorAdapter( final T defaultValue ) {
            this.defaultValue = defaultValue;
        }

        @Override
        public T visit( final Class class_ ) {
            return defaultValue;
        }

        @Override
        public T visit( final DataProperty dataProperty ) {
            return defaultValue;
        }

        @Override
        public T visit( final ObjectProperty objectProperty ) {
            return defaultValue;
        }

        @Override
        public T visit( final AnnotationProperty annotationProperty ) {
            return defaultValue;
        }

        @Override
        public T visit( final Individual individual ) {
            return defaultValue;
        }

        @Override
        public T visit( final Literal literal ) {
            return defaultValue;
        }

        @Override
        public T visit( final PropertyChain propertyChain ) {
            return defaultValue;
        }

        @Override
        public T visit( final Datatype datatype ) {
            return defaultValue;
        }

        @Override
        public T visit( final ExistentialRestriction existentialRestriction ) {
            return defaultValue;
        }

        @Override
        public T visit( final ValueRestriction valueRestriction ) {
            return defaultValue;
        }

        @Override
        public T visit( final UniversalRestriction universalRestriction ) {
            return defaultValue;
        }

        @Override
        public T visit( final Inverse inverse ) {
            return defaultValue;
        }

        @Override
        public T visit( final Intersection intersection ) {
            return defaultValue;
        }

        @Override
        public T visit( final Union union ) {
            return defaultValue;
        }

        @Override
        public T visit( final Disjointness disjointness ) {
            return defaultValue;
        }

        @Override
        public T visit( final DisjointUnion disjointness ) {
            return defaultValue;
        }

        @Override
        public T visit( final Equality equality ) {
            return defaultValue;
        }

        @Override
        public T visit( final Inequality inequality ) {
            return defaultValue;
        }

        @Override
        public T visit( final ClosedClass closedClass ) {
            return defaultValue;
        }

        @Override
        public T visit( final Complement complement ) {
            return defaultValue;
        }

        @Override
        public T visit( final Self self ) {
            return defaultValue;
        }

        @Override
        public T visit( final ObjectMinimalCardinality objectMinimalCardinality ) {
            return defaultValue;
        }

        @Override
        public T visit( final ObjectQualifiedMinimalCardinality objectQualifiedMinimalCardinality ) {
            return defaultValue;
        }

        @Override
        public T visit( final ObjectMaximalCardinality objectMaximalCardinality ) {
            return defaultValue;
        }

        @Override
        public T visit( final ObjectQualifiedMaximalCardinality objectQualifiedMaximalCardinality ) {
            return defaultValue;
        }

        @Override
        public T visit( final ObjectExactCardinality objectExactCardinality ) {
            return defaultValue;
        }

        @Override
        public T visit( final ObjectQualifiedExactCardinality objectQualifiedExactCardinality ) {
            return defaultValue;
        }

        @Override
        public T visit( final DataMinimalCardinality dataMinimalCardinality ) {
            return defaultValue;
        }

        @Override
        public T visit( final DataMaximalCardinality dataMaximalCardinality ) {
            return defaultValue;
        }

        @Override
        public T visit( final DataExactCardinality dataExactCardinality ) {
            return defaultValue;
        }

        @Override
        public T visit( final Invisible invisible ) {
            return defaultValue;
        }

        @Override
        public T visit( final IRIReference iriReference ) {
            return defaultValue;
        }

        @Override
        public T visit( final PropertyMarker propertyMarker ) {
            return defaultValue;
        }
    }

    public interface NamedNode extends Node {
        String getName();
    }

    public interface CardinalityNode extends Node {
        int getCardinality();
    }

    public interface InvisibleNode extends Node {
    }

    private NodeType() {
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Class extends NodeType implements NamedNode {
        Id id;
        String name;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Class( newId, name );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class DataProperty extends NodeType implements NamedNode {
        Id id;
        String name;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new DataProperty( newId, name );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class ObjectProperty extends NodeType implements NamedNode {
        Id id;
        String name;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new ObjectProperty( newId, name );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class AnnotationProperty extends NodeType implements NamedNode {
        Id id;
        String name;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new AnnotationProperty( newId, name );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Individual extends NodeType implements NamedNode {
        Id id;
        String name;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Individual( newId, name );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Datatype extends NodeType implements NamedNode {
        Id id;
        String name;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Datatype( newId, name );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Literal extends NodeType {
        Id id;
        String value;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Literal( newId, value );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class PropertyChain extends NodeType {
        Id id;
        String value;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Literal( newId, value );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class ExistentialRestriction extends NodeType {
        Id id;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new ExistentialRestriction( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class ValueRestriction extends NodeType {
        Id id;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new ValueRestriction( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class UniversalRestriction extends NodeType {
        Id id;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new UniversalRestriction( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Intersection extends NodeType {
        Id id;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Intersection( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Union extends NodeType {
        Id id;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Union( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Disjointness extends NodeType {
        Id id;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Disjointness( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class DisjointUnion extends NodeType {
        Id id;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new DisjointUnion( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Equality extends NodeType {
        Id id;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Equality( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Inverse extends NodeType {
        Id id;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Inverse( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Inequality extends NodeType {
        Id id;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Inequality( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class ClosedClass extends NodeType {
        Id id;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new ClosedClass( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Complement extends NodeType {
        Id id;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Complement( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Self extends NodeType {
        Id id;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Self( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class ObjectMinimalCardinality extends NodeType implements CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new ObjectMinimalCardinality( newId, cardinality );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class ObjectQualifiedMinimalCardinality extends NodeType implements CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new ObjectQualifiedMinimalCardinality( newId, cardinality );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class ObjectMaximalCardinality extends NodeType implements CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new ObjectMaximalCardinality( newId, cardinality );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class ObjectQualifiedMaximalCardinality extends NodeType implements CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new ObjectQualifiedMaximalCardinality( newId, cardinality );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class ObjectExactCardinality extends NodeType implements CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new ObjectExactCardinality( newId, cardinality );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class ObjectQualifiedExactCardinality extends NodeType implements CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new ObjectQualifiedExactCardinality( newId, cardinality );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class DataMinimalCardinality extends NodeType implements CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new DataMinimalCardinality( newId, cardinality );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class DataMaximalCardinality extends NodeType implements CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new DataMaximalCardinality( newId, cardinality );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class DataExactCardinality extends NodeType implements CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new DataExactCardinality( newId, cardinality );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class Invisible extends NodeType implements InvisibleNode {
        Id id;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new Invisible( newId );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class IRIReference extends NodeType implements InvisibleNode {
        Id id;
        IRI iri;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }

        @Override
        public Node clone( final Id newId ) {
            return new IRIReference( newId, iri );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static class PropertyMarker extends NodeType {
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
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
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

    abstract public <T> T accept( final Visitor<T> visitor );
}
