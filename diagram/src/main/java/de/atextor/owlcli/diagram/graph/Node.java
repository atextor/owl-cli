package de.atextor.owlcli.diagram.graph;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.semanticweb.owlapi.model.IRI;

import java.util.Optional;

public interface Node extends GraphElement {
    @Getter
    @EqualsAndHashCode
    class Id {
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

    Id getId();

    @Override
    default boolean isNode() {
        return true;
    }

    @Override
    default Node asNode() {
        return this;
    }

    Node clone( Id newId );
}
