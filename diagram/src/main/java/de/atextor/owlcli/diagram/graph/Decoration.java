package de.atextor.owlcli.diagram.graph;

import lombok.EqualsAndHashCode;
import lombok.Value;

public abstract class Decoration {
    public interface Visitor<T> {
        T visit( final Label label );

        T visit( final ClassSymbol classSymbol );

        T visit( final AbstractRoleSymbol abstractRoleSymbol );

        T visit( final ConcreteRoleSymbol concreteRoleSymbol );

        T visit( final DataRangeSymbol dataRangeSymbol );

        T visit( final IndividualSymbol individualSymbol );

        T visit( final LiteralSymbol literalSymbol );
    }

    private Decoration() {
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class Label extends Decoration {
        String text;

        @Override
        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class ClassSymbol extends Decoration {
        @Override
        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class AbstractRoleSymbol extends Decoration {
        @Override
        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class ConcreteRoleSymbol extends Decoration {
        @Override
        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class DataRangeSymbol extends Decoration {
        @Override
        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class IndividualSymbol extends Decoration {
        @Override
        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    @Value
    @EqualsAndHashCode( callSuper = true )
    public static final class LiteralSymbol extends Decoration {
        @Override
        public <T> T accept( final Visitor<T> visitor ) {
            return visitor.visit( this );
        }
    }

    abstract public <T> T accept( final Visitor<T> visitor );
}
