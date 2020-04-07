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
}
