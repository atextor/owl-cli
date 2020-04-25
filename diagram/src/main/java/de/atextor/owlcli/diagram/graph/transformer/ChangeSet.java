package de.atextor.owlcli.diagram.graph.transformer;

import com.google.common.collect.Sets;
import de.atextor.owlcli.diagram.graph.GraphElement;
import lombok.Value;

import java.util.Set;

@Value
class ChangeSet {
    public static final ChangeSet EMPTY = new ChangeSet( Set.of(), Set.of() );

    Set<GraphElement> additions;
    Set<GraphElement> deletions;

    ChangeSet merge( final ChangeSet other ) {
        return new ChangeSet( Sets.union( additions, other.additions ), Sets.union( deletions, other.deletions ) );
    }

    Set<GraphElement> applyTo( final Set<GraphElement> graph ) {
        return Sets.union( Sets.difference( graph, deletions ), additions );
    }
}
