package de.atextor.owlcli.diagram.graph;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.FieldDefaults;

@ToString
@EqualsAndHashCode
@FieldDefaults( makeFinal = true, level = AccessLevel.PRIVATE )
@Getter
public abstract class NodeType implements Node {
    public interface Visitor<T> {
        T visit( Class class_ );

        T visit( AbstractRole abstractRole );

        T visit( ConcreteRole concreteRole );

        T visit( AnnotationRole annotationRole );

        T visit( Individual individual );

        T visit( Datatype datatype );

        T visit( ExistentialRestriction existentialRestriction );

        T visit( ValueRestriction valueRestriction );

        T visit( UniversalRestriction universalRestriction );

        T visit( Intersection intersection );

        T visit( Union union );

        T visit( ClosedClass closedClass );

        T visit( Domain domain );

        T visit( Range range );

        T visit( Complement complement );

        T visit( Self self );

        T visit( AbstractMinimalCardinality abstractMinimalCardinality );

        T visit( AbstractQualifiedMinimalCardinality abstractQualifiedMinimalCardinality );

        T visit( AbstractMaximalCardinality abstractMaximalCardinality );

        T visit( AbstractQualifiedMaximalCardinality abstractQualifiedMaximalCardinality );

        T visit( AbstractExactCardinality abstractExactCardinality );

        T visit( AbstractQualifiedExactCardinality abstractQualifiedExactCardinality );

        T visit( ConcreteMinimalCardinality concreteMinimalCardinality );

        T visit( ConcreteMaximalCardinality concreteMaximalCardinality );

        T visit( ConcreteExactCardinality concreteExactCardinality );

        T visit( Invisible invisible );
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
        public T visit( final AbstractRole abstractRole ) {
            return defaultValue;
        }

        @Override
        public T visit( final ConcreteRole concreteRole ) {
            return defaultValue;
        }

        @Override
        public T visit( final AnnotationRole annotationRole ) {
            return defaultValue;
        }

        @Override
        public T visit( final Individual individual ) {
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
        public T visit( final Intersection intersection ) {
            return defaultValue;
        }

        @Override
        public T visit( final Union union ) {
            return defaultValue;
        }

        @Override
        public T visit( final ClosedClass closedClass ) {
            return defaultValue;
        }

        @Override
        public T visit( final Domain domain ) {
            return defaultValue;
        }

        @Override
        public T visit( final Range range ) {
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
        public T visit( final AbstractMinimalCardinality abstractMinimalCardinality ) {
            return defaultValue;
        }

        @Override
        public T visit( final AbstractQualifiedMinimalCardinality abstractQualifiedMinimalCardinality ) {
            return defaultValue;
        }

        @Override
        public T visit( final AbstractMaximalCardinality abstractMaximalCardinality ) {
            return defaultValue;
        }

        @Override
        public T visit( final AbstractQualifiedMaximalCardinality abstractQualifiedMaximalCardinality ) {
            return defaultValue;
        }

        @Override
        public T visit( final AbstractExactCardinality abstractExactCardinality ) {
            return defaultValue;
        }

        @Override
        public T visit( final AbstractQualifiedExactCardinality abstractQualifiedExactCardinality ) {
            return defaultValue;
        }

        @Override
        public T visit( final ConcreteMinimalCardinality concreteMinimalCardinality ) {
            return defaultValue;
        }

        @Override
        public T visit( final ConcreteMaximalCardinality concreteMaximalCardinality ) {
            return defaultValue;
        }

        @Override
        public T visit( final ConcreteExactCardinality concreteExactCardinality ) {
            return defaultValue;
        }

        @Override
        public T visit( final Invisible invisible ) {
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
    public static final class Class extends NodeType implements NamedNode {
        Id id;
        String name;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class AbstractRole extends NodeType implements NamedNode {
        Id id;
        String name;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class ConcreteRole extends NodeType implements NamedNode {
        Id id;
        String name;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class AnnotationRole extends NodeType implements NamedNode {
        Id id;
        String name;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class Individual extends NodeType implements NamedNode {
        Id id;
        String name;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class Datatype extends NodeType implements NamedNode {
        Id id;
        String name;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class ExistentialRestriction extends NodeType {
        Id id;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class ValueRestriction extends NodeType {
        Id id;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class UniversalRestriction extends NodeType {
        Id id;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class Intersection extends NodeType {
        Id id;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class Union extends NodeType {
        Id id;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class ClosedClass extends NodeType {
        Id id;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class Domain extends NodeType {
        Id id;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class Range extends NodeType {
        Id id;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class Complement extends NodeType {
        Id id;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class Self extends NodeType {
        Id id;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class AbstractMinimalCardinality extends NodeType implements CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class AbstractQualifiedMinimalCardinality extends NodeType implements CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class AbstractMaximalCardinality extends NodeType implements CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class AbstractQualifiedMaximalCardinality extends NodeType implements CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class AbstractExactCardinality extends NodeType implements CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class AbstractQualifiedExactCardinality extends NodeType implements CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class ConcreteMinimalCardinality extends NodeType implements CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class ConcreteMaximalCardinality extends NodeType implements CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class ConcreteExactCardinality extends NodeType implements CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class Invisible extends NodeType implements InvisibleNode {
        Id id;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Override
    public <T> T accept( final GraphElement.Visitor<T> visitor ) {
        return visitor.visit( this );
    }

    abstract public <T> T accept( final Visitor<T> visitor );
}
