/*
 * Copyright 2024 Andreas Textor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cool.rdf.diagram.owl;

import cool.rdf.diagram.owl.graph.GraphElement;
import cool.rdf.diagram.owl.mappers.MappingConfiguration;
import cool.rdf.diagram.owl.mappers.OWLOntologyMapper;
import io.vavr.control.Try;
import org.apache.commons.io.IOUtils;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Main diagram generator class: Loads an ontology, initializes and calls the {@link GraphvizGenerator} to transform
 * the ontology into a Graphviz document, writes the document to a file and calls the Graphviz (dot) binary.
 */
public class DiagramGenerator {
    private final OWLOntologyMapper ontologyMapper;

    private final Function<Stream<GraphElement>, GraphvizDocument> graphvizGenerator;

    private final List<Function<Try<String>, Try<String>>> svgPostProcessors = List.of( new FontEmbedder() );

    private static final Logger LOG = LoggerFactory.getLogger( DiagramGenerator.class );

    /**
     * Constructor. Initializes the Diagram generator with the necessary configuration.
     *
     * @param configuration the configuration to provide to the Graphviz Generator
     * @param mappingConfig the mapping configuration to fine-tune the ontology mapping operation
     */
    public DiagramGenerator( final Configuration configuration, final MappingConfiguration mappingConfig ) {
        ontologyMapper = new OWLOntologyMapper( mappingConfig );
        graphvizGenerator = new GraphvizGenerator( configuration );
    }

    private Try<Void> writeStreamToOutput( final InputStream in, final OutputStream out ) {
        try {
            in.transferTo( out );
            return Try.success( null );
        } catch ( final IOException exception ) {
            return Try.failure( exception );
        }
    }

    Try<Void> executeDot( final ThrowingConsumer<OutputStream, IOException> contentProvider, final OutputStream output,
        final File workingDir, final Configuration configuration ) {
        final String command = configuration.dotBinary + " -T" + configuration.format.getExtension();
        final Process process;
        try {
            LOG.info( "Running dot: {}", command );
            process = Runtime.getRuntime().exec( command, null, workingDir );

            final OutputStream processStdIn = process.getOutputStream();
            final InputStream processStdOut = process.getInputStream();
            final InputStream processStdErr = process.getErrorStream();

            contentProvider.accept( processStdIn );
            final byte[] graphvizStdout = IOUtils.toByteArray( processStdOut );
            final String graphvizStderr = IOUtils.toString( processStdErr, StandardCharsets.UTF_8 );

            if ( !graphvizStderr.isEmpty() &&
                !graphvizStderr.startsWith( "Warning:" ) &&
                !graphvizStderr.contains( "Pango-WARNING" ) ) {
                LOG.debug( "Dot returned an error: {}", graphvizStderr );
                return Try.failure( new RuntimeException( "An error occurred while running dot. This is most likely "
                    + "due to a bug in owl-cli. Captured message was: " + graphvizStderr ) );
            }

            if ( configuration.format == Configuration.Format.PNG ) {
                output.write( graphvizStdout );
                output.flush();
                if ( output != System.out ) {
                    output.close();
                }
                return Try.success( null );
            }

            final String svgOutput = new String( graphvizStdout, StandardCharsets.UTF_8 );
            return postProcess( svgOutput ).flatMap( processedOutput ->
                writeStreamToOutput( processedOutput, output ).flatMap( writingResult -> {
                    LOG.debug( "Writing to output {}", output );
                    try {
                        process.waitFor();
                        return Try.success( null );
                    } catch ( final InterruptedException exception ) {
                        return Try.failure( exception );
                    }
                } ) );
        } catch ( final IOException exception ) {
            return Try.failure( exception );
        }
    }

    private Try<InputStream> postProcess( final String dotOutput ) {
        return svgPostProcessors.stream().sequential().reduce( Function.identity(), Function::andThen )
            .apply( Try.success( dotOutput ) )
            .map( string -> IOUtils.toInputStream( string, StandardCharsets.UTF_8 ) );
    }

    /**
     * Performs diagram generation for an input ontology. The result is either written to a given {@link OutputStream}
     * or a given {@link Path}.
     *
     * @param ontology the input ontology
     * @param output the output to write
     * @param configuration the configuration for the diagram generation
     * @return {@link io.vavr.control.Try.Success} on success
     */
    public Try<Void> generate( final OWLOntology ontology, final OutputStream output,
        final Configuration configuration ) {
        LOG.info( "Applying ontology mappers" );
        final Stream<GraphElement> ontologyGraphRepresentation = ontologyMapper.apply( ontology ).stream();
        LOG.info( "Generating Graphviz document" );
        final GraphvizDocument graphvizDocument = graphvizGenerator.apply( ontologyGraphRepresentation );
        final String graphvizGraph = graphvizDocument.apply( configuration );
        LOG.trace( "Generated Graphviz document: {}", graphvizGraph );

        final ThrowingConsumer<OutputStream, IOException> contentProvider = outputStream -> {
            outputStream.write( graphvizGraph.getBytes( StandardCharsets.UTF_8 ) );
            outputStream.flush();
            if ( outputStream != System.out ) {
                outputStream.close();
            }
        };

        LOG.debug( "Calling dot binary" );
        return executeDot( contentProvider, output, new File( System.getProperty( "user.dir" ) ), configuration );
    }
}
