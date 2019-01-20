package de.atextor.owldiagram;

import de.atextor.owldiagram.graph.GraphElement;
import de.atextor.owldiagram.mappers.OWLAxiomMapper;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {
    public static void main( final String[] args ) throws OWLOntologyCreationException {
        final OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        final OWLOntology ont = m.loadOntologyFromOntologyDocument( App.class.getResourceAsStream( "/test.owl" ) );

        final OWLAxiomMapper visitor = new OWLAxiomMapper();
        final Stream<GraphElement> graphElements = ont.axioms().flatMap( axiom -> axiom.accept( visitor ) );
        final Set<GraphElement> graph = graphElements.collect( Collectors.toSet() );

        graph.forEach( System.out::println );
    }
}
