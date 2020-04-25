package de.atextor.owlcli.diagram.graph.transformer;

import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.NodeType;
import de.atextor.owlcli.diagram.mappers.MappingConfiguration;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IriReferenceResolver extends GraphTransformer {
    private final MappingConfiguration mappingConfiguration;

    public IriReferenceResolver( final MappingConfiguration mappingConfiguration ) {
        this.mappingConfiguration = mappingConfiguration;
    }

    @Override
    public Set<GraphElement> apply( final Set<GraphElement> graph ) {
        final Set<NodeType.IRIReference> references = graph.stream()
            .flatMap( element -> element.view( NodeType.IRIReference.class ) )
            .collect( Collectors.toSet() );

        if ( references.isEmpty() ) {
            return graph;
        }

        final ChangeSet changeSet = resolveReferences( graph, references );
        return changeSet.applyTo( graph );
    }

    private ChangeSet resolveReferences( final Set<GraphElement> graph, final Set<NodeType.IRIReference> references ) {
        return references.stream().flatMap( reference -> {
            final Set<Node> referencedNodes = findNodesWithIri( graph, reference.getIri() )
                .collect( Collectors.toSet() );

            if ( referencedNodes.isEmpty() ) {
                final Node literal = turnReferenceIntoLiteral( reference );
                final ChangeSet updatedToEdges = updateEdgesTo( graph, reference, literal );
                return Stream.of( new ChangeSet( Set.of( literal ), Set.of( reference ) ).merge( updatedToEdges ) );
            }

            return referencedNodes.stream().flatMap( node -> {
                final ChangeSet updatedReferenceElement = new ChangeSet( Set.of(), Set.of( reference ) );
                final ChangeSet updatedToEdges = updateEdgesTo( graph, reference, node );
                final ChangeSet updatedFromEdges = updateEdgesFrom( graph, reference, node );
                return Stream.of( updatedReferenceElement, updatedToEdges, updatedFromEdges );
            } );
        } ).reduce( ChangeSet.EMPTY, ChangeSet::merge );
    }

    private NodeType.Literal turnReferenceIntoLiteral( final NodeType.IRIReference reference ) {
        return new NodeType.Literal(
            mappingConfiguration.getIdentifierMapper().getSyntheticId(),
            mappingConfiguration.getNameMapper().getName( reference.getIri() ) );
    }
}
