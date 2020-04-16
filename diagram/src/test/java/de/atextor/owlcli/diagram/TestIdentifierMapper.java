package de.atextor.owlcli.diagram;

import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.mappers.IdentifierMapper;
import org.semanticweb.owlapi.model.IRI;

import java.util.Stack;
import java.util.UUID;

public class TestIdentifierMapper implements IdentifierMapper {
    private final Stack<Node.Id> preconfiguredAnonIds = new Stack<>();

    @Override
    public Node.Id getIdForIri( final IRI iri ) {
        return new Node.Id( iri.getFragment(), iri );
    }

    void pushAnonId( final Node.Id id ) {
        preconfiguredAnonIds.push( id );
    }

    private String getRandomIdString() {
        return "_" + UUID.randomUUID().toString().replace( "-", "" );
    }

    private Node.Id pop() {
        if ( preconfiguredAnonIds.empty() ) {
            return new Node.Id( getRandomIdString() );
        }
        return preconfiguredAnonIds.pop();
    }

    @Override
    public Node.Id getSyntheticId() {
        return pop();
    }

    @Override
    public Node.Id getSyntheticIdForIri( final IRI iri ) {
        return pop();
    }
}
