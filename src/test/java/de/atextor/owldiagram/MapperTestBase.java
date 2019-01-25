package de.atextor.owldiagram;

import de.atextor.owldiagram.graph.Edge;
import de.atextor.owldiagram.graph.GraphElement;
import de.atextor.owldiagram.graph.Node;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Fail.fail;

public class MapperTestBase {
    protected OWLOntology createOntology( final String content ) {
        final OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        final String ontologyContent = "@prefix : <http://test.de/> .\n" +
                "@prefix owl: <http://www.w3.org/2002/07/owl#> .\n" +
                "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
                "@prefix xml: <http://www.w3.org/XML/1998/namespace> .\n" +
                "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n" +
                "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n" +
                "\n" +
                content;
        final OWLOntology ontology;

        try {
            final InputStream stream = new ByteArrayInputStream( ontologyContent.getBytes( StandardCharsets.UTF_8 ) );
            ontology = m.loadOntologyFromOntologyDocument( stream );
            return ontology;
        } catch ( final OWLOntologyCreationException e ) {
            fail( "Could not create ontology", e );
        }
        return null;
    }

    @SuppressWarnings( "unchecked" )
    protected <T extends OWLAxiom> T getAxiom( final String content ) {
        return (T) createOntology( content ).axioms().findFirst().get();
    }

    @SuppressWarnings( "unchecked" )
    protected <T extends OWLAxiom> T getAxiom( final String content, final AxiomType axiomType ) {
        return (T) createOntology( content ).axioms().filter( axiom -> axiom.isOfType( axiomType ) ).findAny().get();
    }

    protected List<Edge> edges( final List<GraphElement> elements ) {
        return elements.stream()
                .filter( GraphElement::isEdge )
                .map( GraphElement::asEdge )
                .collect( Collectors.toList() );
    }

    protected List<Node> nodes( final List<GraphElement> elements ) {
        return elements.stream()
                .filter( GraphElement::isNode )
                .map( GraphElement::asNode )
                .collect( Collectors.toList() );
    }

    protected Predicate<Node> isNodeWithId( final String targetId ) {
        return node -> node.getId().getId().equals( targetId );
    }

    protected Predicate<Edge> isEdgeWithFromAndTo( final String fromId, final String toId ) {
        return edge -> edge.getFrom().getId().equals( fromId )
                && edge.getTo().getId().equals( toId );
    }

}
