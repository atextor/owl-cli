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
    interface Visitor<T> {
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

    private NodeType() {
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class Class extends NodeType {
        Id id;
        String name;

        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class AbstractRole extends NodeType {
        Id id;
        String name;

        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class ConcreteRole extends NodeType {
        Id id;
        String name;

        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class AnnotationRole extends NodeType {
        Id id;
        String name;

        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class Individual extends NodeType {
        Id id;
        String name;

        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class Datatype extends NodeType {
        Id id;
        String name;

        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class ExistentialRestriction extends NodeType {
        Id id;

        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class ValueRestriction extends NodeType {
        Id id;

        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class UniversalRestriction extends NodeType {
        Id id;

        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class Intersection extends NodeType {
        Id id;

        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class Union extends NodeType {
        Id id;

        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class ClosedClass extends NodeType {
        Id id;

        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class Domain extends NodeType {
        Id id;

        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class Range extends NodeType {
        Id id;

        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class Complement extends NodeType {
        Id id;

        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class Self extends NodeType {
        Id id;

        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class AbstractMinimalCardinality extends NodeType {
        Id id;
        int cardinality;

        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class AbstractQualifiedMinimalCardinality extends NodeType {
        Id id;
        int cardinality;

        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class AbstractMaximalCardinality extends NodeType {
        Id id;
        int cardinality;

        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class AbstractQualifiedMaximalCardinality extends NodeType {
        Id id;
        int cardinality;

        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class AbstractExactCardinality extends NodeType {
        Id id;
        int cardinality;

        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class AbstractQualifiedExactCardinality extends NodeType {
        Id id;
        int cardinality;

        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class ConcreteMinimalCardinality extends NodeType {
        Id id;
        int cardinality;

        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class ConcreteMaximalCardinality extends NodeType {
        Id id;
        int cardinality;

        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class ConcreteExactCardinality extends NodeType {
        Id id;
        int cardinality;

        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }
}
