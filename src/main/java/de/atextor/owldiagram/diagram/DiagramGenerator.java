package de.atextor.owldiagram.diagram;

import de.atextor.owldiagram.graph.GraphElement;
import de.atextor.owldiagram.mappers.OWLAxiomMapper;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import org.semanticweb.owlapi.model.OWLOntology;

import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Stream;

public class DiagramGenerator {
    private final OWLAxiomMapper visitor;
    private final GraphvizGenerator graphvizGenerator;

    public DiagramGenerator() {
        visitor = new OWLAxiomMapper();
        graphvizGenerator = new GraphvizGenerator();
    }

    public void generate( final OWLOntology ontology, final OutputStream outputStream ) {
        generate( ontology, GraphvizDocument.DEFAULT_CONFIGURATION, outputStream );
    }

    public void generate( final OWLOntology ontology, final Configuration configuration,
                          final OutputStream outputStream ) {
        final Stream<GraphElement> graphElements = ontology.axioms().flatMap( axiom -> axiom.accept( visitor ) );
        final GraphvizDocument graphvizDocument = graphvizGenerator.apply( graphElements );

        try {
            final MutableGraph graph = Parser.read( graphvizDocument.apply( configuration ) );
            Graphviz.fromGraph( graph ).render( Format.SVG ).toOutputStream( outputStream );
        } catch ( final IOException e ) {
            e.printStackTrace();
        }
    }
}
