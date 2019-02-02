package de.atextor.owldiagram;

import de.atextor.owldiagram.diagram.GraphvizDocument;
import de.atextor.owldiagram.diagram.GraphvizGenerator;
import de.atextor.owldiagram.graph.GraphElement;
import de.atextor.owldiagram.mappers.OWLAxiomMapper;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.util.stream.Stream;

public class App {
    public static void main( final String[] args ) throws OWLOntologyCreationException {
        final OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        final OWLOntology ontology = m.loadOntologyFromOntologyDocument( App.class.getResourceAsStream( "/test.owl" ) );

        final OWLAxiomMapper visitor = new OWLAxiomMapper();
        final GraphvizGenerator graphvizGenerator = new GraphvizGenerator();

        final Stream<GraphElement> graphElements = ontology.axioms().flatMap( axiom -> axiom.accept( visitor ) );
        final GraphvizDocument graphvizDocument = graphvizGenerator.apply( graphElements );
        System.out.println( graphvizDocument.apply( GraphvizDocument.DEFAULT_CONFIGURATION ) );
    }
}
