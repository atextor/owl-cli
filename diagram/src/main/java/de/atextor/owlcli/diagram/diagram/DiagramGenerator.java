/*
 * This file is part of OWL-CLI.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright (c) 2020, Andreas Textor.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www
 * .gnu.org/licenses/.
 */

package de.atextor.owlcli.diagram.diagram;

import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.mappers.MappingConfiguration;
import de.atextor.owlcli.diagram.mappers.OWLOntologyMapper;
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
     * @param configuration the configuration to provide to the Graphviz Genenerator
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

    Try<Void> executeDot( final ThrowingConsumer<OutputStream, IOException> contentProvider,
                          final OutputStream output,
                          final File workingDir,
                          final Configuration configuration ) {
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

            if ( !graphvizStderr.isEmpty() ) {
                LOG.debug( "Dot returned an error: {}", graphvizStderr );
                return Try.failure( new RuntimeException( "An error occured while running dot. This is most likely "
                    + "due to a bug in owl-cli." ) );
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
            return postprocess( svgOutput ).flatMap( processedOutput ->
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

    private Try<InputStream> postprocess( final String dotOutput ) {
        return svgPostProcessors.stream().sequential().reduce( Function.identity(), Function::andThen )
            .apply( Try.success( dotOutput ) )
            .map( string -> IOUtils.toInputStream( string, StandardCharsets.UTF_8 ) );
    }

    /**
     * Performs diagram generation for an input ontology. The result is either written to a given {@link OutputStream}
     * or a given {@link Path}.
     *
     * @param ontology      the input ontology
     * @param output        the output to write
     * @param configuration the configuration for the diagram generation
     * @return {@link io.vavr.control.Try.Success} on success
     */
    public Try<Void> generate( final OWLOntology ontology, final OutputStream output,
                               final Configuration configuration ) {
        LOG.info( "Applying ontology mappers" );
        final Stream<GraphElement> ontologyGraphRepresenation = ontologyMapper.apply( ontology ).stream();
        LOG.info( "Generating Graphviz document" );
        final GraphvizDocument graphvizDocument = graphvizGenerator.apply( ontologyGraphRepresenation );
        final String graphvizGraph = graphvizDocument.apply( configuration );
        LOG.trace( "Generated Graphviz document: {}", graphvizGraph );

        final ThrowingConsumer<OutputStream, IOException> contentProvider = outputStream -> {
            outputStream.write( graphvizGraph.getBytes() );
            outputStream.flush();
            if ( outputStream != System.out ) {
                outputStream.close();
            }
        };

        LOG.debug( "Calling dot binary" );
        return executeDot( contentProvider, output, new File( System.getProperty( "user.dir" ) ), configuration );
    }
}
