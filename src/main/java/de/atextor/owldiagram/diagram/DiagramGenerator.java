package de.atextor.owldiagram.diagram;

import de.atextor.owldiagram.graph.GraphElement;
import de.atextor.owldiagram.mappers.OWLAxiomMapper;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class DiagramGenerator {
    private final OWLAxiomMapper visitor = new OWLAxiomMapper();
    private final GraphvizGenerator graphvizGenerator = new GraphvizGenerator();

    private void writeStreamToOutput( final InputStream in, final OutputStream out ) throws IOException {
        final byte[] buffer = new byte[1024];
        for ( ; ; ) {
            final int bytesRead = in.read( buffer );
            if ( bytesRead == -1 ) {
                break;
            }
            out.write( buffer, 0, bytesRead );
        }
    }

    private void executeDot( final Consumer<OutputStream> contentProvider, final OutputStream output,
                             final File workingDir, final Configuration configuration ) {
        try {
            final String command = "dot -T" + configuration.format.getExtension();
            final Process process;
            process = Runtime.getRuntime().exec( command, null, workingDir );
            final OutputStream processStdIn = process.getOutputStream();
            final InputStream processStdOut = process.getInputStream();
            contentProvider.accept( processStdIn );
            writeStreamToOutput( processStdOut, output );
            process.waitFor();
        } catch ( final IOException exception ) {
            System.err.println( "Error while running dot: " + exception.getMessage() );
        } catch ( final InterruptedException exception ) {
            System.err.println( "Command interrupted: " + exception.getMessage() );
        }
    }

    private File setupTempDirectory( final Configuration configuration ) {
        try {
            final Path tempDir = Files.createTempDirectory( "owl-diagram" );

            for ( int i = 0; i < Resource.values().length; i++ ) {
                final Resource resource = Resource.values()[i];
                final String resourceName = resource.getResourceName() + "." + configuration.format.getExtension();
                final InputStream resourceInput =
                        DiagramGenerator.class.getResourceAsStream( "/" + resourceName );
                final File resourceFile = tempDir.resolve( resourceName ).toFile();
                final OutputStream resourceOutput = new FileOutputStream( resourceFile );
                writeStreamToOutput( resourceInput, resourceOutput );
            }

            return tempDir.toFile();
        } catch ( final IOException exception ) {
            exception.printStackTrace();
        }

        return null;
    }

    public void generate( final InputStream ontologyInputStream, final OutputStream output,
                          final Configuration configuration ) {
        final OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        final OWLOntology ontology;
        try {
            ontology = m.loadOntologyFromOntologyDocument( ontologyInputStream );
            generate( ontology, output, configuration );
        } catch ( final OWLOntologyCreationException exception ) {
            exception.printStackTrace();
        }
    }

    public void generate( final OWLOntology ontology, final OutputStream output, final Configuration configuration ) {
        final Stream<GraphElement> graphElements = ontology.axioms().flatMap( axiom -> axiom.accept( visitor ) );
        final GraphvizDocument graphvizDocument = graphvizGenerator.apply( graphElements );
        final String graphvizGraph = graphvizDocument.apply( configuration );

        final Consumer<OutputStream> contentProvider = outputStream -> {
            try {
                outputStream.write( graphvizGraph.getBytes() );
                outputStream.flush();
                outputStream.close();
            } catch ( final IOException exception ) {
                System.err.println( "Error while writing to output: " + exception.getMessage() );
            }
        };

        final File workingDir = setupTempDirectory( configuration );
        executeDot( contentProvider, output, workingDir, configuration );
    }
}
