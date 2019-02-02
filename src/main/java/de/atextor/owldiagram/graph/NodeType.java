package de.atextor.owldiagram.graph;

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
    }

    public interface NamedNode extends Node {
        String getName();
    }

    public interface CardinalityNode extends Node {
        int getCardinality();
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

    @Override
    public <T> T accept( final GraphElement.Visitor<T> visitor ) {
        return visitor.visit( this );
    }

    abstract public <T> T accept( final Visitor<T> visitor );
}
