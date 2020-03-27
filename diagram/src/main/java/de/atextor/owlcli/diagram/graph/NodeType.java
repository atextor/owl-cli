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

        T visit( DataProperty dataProperty );

        T visit( ObjectProperty objectProperty );

        T visit( AnnotationProperty annotationProperty );

        T visit( Individual individual );

        T visit( Literal literal );

        T visit( Datatype datatype );

        T visit( ExistentialRestriction existentialRestriction );

        T visit( ValueRestriction valueRestriction );

        T visit( UniversalRestriction universalRestriction );

        T visit( Intersection intersection );

        T visit( Union union );

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
    public static final class DataProperty extends NodeType implements NamedNode {
        Id id;
        String name;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class ObjectProperty extends NodeType implements NamedNode {
        Id id;
        String name;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class AnnotationProperty extends NodeType implements NamedNode {
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
    public static final class Literal extends NodeType {
        Id id;
        String value;

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
    public static final class ObjectMinimalCardinality extends NodeType implements CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class ObjectQualifiedMinimalCardinality extends NodeType implements CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class ObjectMaximalCardinality extends NodeType implements CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class ObjectQualifiedMaximalCardinality extends NodeType implements CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class ObjectExactCardinality extends NodeType implements CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class ObjectQualifiedExactCardinality extends NodeType implements CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class DataMinimalCardinality extends NodeType implements CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class DataMaximalCardinality extends NodeType implements CardinalityNode {
        Id id;
        int cardinality;

        @Override
        public <T> T accept( final NodeType.Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class DataExactCardinality extends NodeType implements CardinalityNode {
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
