package de.atextor.owlcli.diagram;

import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.mappers.IdentifierMapper;
import org.semanticweb.owlapi.model.IRI;

import java.util.Stack;

public class TestIdentifierMapper implements IdentifierMapper {
    private Stack<Node.Id> preconfiguredAnonIds = new Stack<>();

    @Override
    public Node.Id getIdForIri( final IRI iri ) {
        return new Node.Id( iri.getFragment() );
    }

    void pushAnonId( final Node.Id id ) {
        preconfiguredAnonIds.push( id );
    }

    @Override
    public Node.Id getSyntheticId() {
        return preconfiguredAnonIds.pop();
    }
}
