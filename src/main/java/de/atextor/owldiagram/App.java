package de.atextor.owldiagram;

import de.atextor.owldiagram.diagram.GraphvizDocument;
import de.atextor.owldiagram.diagram.GraphvizGenerator;
import de.atextor.owldiagram.graph.GraphElement;
import de.atextor.owldiagram.mappers.OWLAxiomMapper;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class App {
    private static void writeToStdout( final InputStream in ) throws IOException {
        final byte[] buffer = new byte[1024];
        for ( ; ; ) {
            final int bytesRead = in.read( buffer );
            if ( bytesRead == -1 ) {
                break;
            }
            System.out.write( buffer, 0, bytesRead );
        }
    }

    private static void executeDot( final Consumer<OutputStream> contentProvider, final String outputFile,
                                    final File workingDir ) {
        try {
            final boolean writeToStdout = outputFile.equals( "-" );
            final String output = writeToStdout ? "" : " -o" + outputFile;
            final String command = "dot -Tsvg" + output;
            final Process process;
            process = Runtime.getRuntime().exec( command, null, workingDir );
            final OutputStream processStdIn = process.getOutputStream();
            final InputStream processStdOut = process.getInputStream();

            contentProvider.accept( processStdIn );

            if ( writeToStdout ) {
                writeToStdout( processStdOut );
            }

            process.waitFor();
        } catch ( final IOException exception ) {
            System.err.println( "Error while running dot: " + exception.getMessage() );
        } catch ( final InterruptedException exception ) {
            System.err.println( "Command interrupted: " + exception.getMessage() );
        }
    }

    public static void main( final String[] args ) throws OWLOntologyCreationException {
        final OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        final OWLOntology ontology = m.loadOntologyFromOntologyDocument( App.class.getResourceAsStream( "/test.owl" ) );

        final OWLAxiomMapper visitor = new OWLAxiomMapper();
        final GraphvizGenerator graphvizGenerator = new GraphvizGenerator();

        final Stream<GraphElement> graphElements = ontology.axioms().flatMap( axiom -> axiom.accept( visitor ) );
        final GraphvizDocument graphvizDocument = graphvizGenerator.apply( graphElements );
        final String result = graphvizDocument.apply( GraphvizDocument.DEFAULT_CONFIGURATION );

        final Consumer<OutputStream> contentProvider = outputStream -> {
            try {
                outputStream.write( result.getBytes() );
                outputStream.flush();
                outputStream.close();
            } catch ( final IOException exception ) {
                System.err.println( "Error while writing to output: " + exception.getMessage() );
            }
        };

        final File workingDir = new File( "/home/tex/git/owl-diagram/src/main/resources/" );
        executeDot( contentProvider, "/home/tex/git/owl-diagram/src/main/resources/test.svg", workingDir );
    }
}
