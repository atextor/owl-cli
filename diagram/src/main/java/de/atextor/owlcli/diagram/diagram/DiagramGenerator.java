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
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.stream.Stream;

public class DiagramGenerator {
    private final OWLOntologyMapper ontologyMapper;
    private final Function<Stream<GraphElement>, GraphvizDocument> graphvizGenerator;

    public DiagramGenerator( final Configuration configuration, final MappingConfiguration mappingConfig ) {
        ontologyMapper = new OWLOntologyMapper( mappingConfig );
        graphvizGenerator = new GraphvizGenerator( configuration );
    }

    private Try<Void> writeStreamToOutput( final InputStream in, final OutputStream out ) {
        try {
            final byte[] buffer = new byte[1024];
            for ( ; ; ) {
                final int bytesRead;
                bytesRead = in.read( buffer );
                if ( bytesRead == -1 ) {
                    return Try.success( null );
                }
                out.write( buffer, 0, bytesRead );
            }
        } catch ( final IOException exception ) {
            return Try.failure( exception );
        }
    }

    private Try<Void> executeDot( final ThrowingConsumer<OutputStream, IOException> contentProvider,
                                  final OutputStream output,
                                  final File workingDir,
                                  final Configuration configuration ) {
        final String command = configuration.dotBinary + " -T" + configuration.format.getExtension();
        final Process process;
        try {
            process = Runtime.getRuntime().exec( command, null, workingDir );
        } catch ( final IOException exception ) {
            return Try.failure( exception );
        }

        final OutputStream processStdIn = process.getOutputStream();
        final InputStream processStdOut = process.getInputStream();
        try {
            contentProvider.accept( processStdIn );
        } catch ( final IOException exception ) {
            return Try.failure( exception );
        }

        return writeStreamToOutput( processStdOut, output ).flatMap( writingResult -> {
            try {
                process.waitFor();
                return Try.success( null );
            } catch ( final InterruptedException exception ) {
                return Try.failure( exception );
            }
        } );
    }

    public Try<Void> generate( final InputStream ontologyInputStream, final Either<OutputStream, Path> output,
                               final Configuration configuration ) {
        final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        final OWLOntology ontology;
        try {
            ontology = manager.loadOntologyFromOntologyDocument( ontologyInputStream );
            return generate( ontology, output, configuration );
        } catch ( final OWLOntologyCreationException exception ) {
            return Try.failure( exception );
        }
    }

    private Try<OutputStream> openStream( final Path filePath ) {
        try {
            return Try.success( new FileOutputStream( filePath.toFile() ) );
        } catch ( final FileNotFoundException exeception ) {
            return Try.failure( exeception );
        }
    }

    public Try<Void> generate( final OWLOntology ontology, final Either<OutputStream, Path> output,
                               final Configuration configuration ) {
        final Stream<GraphElement> ontologyGraphRepresenation = ontologyMapper.apply( ontology ).stream();
        final GraphvizDocument graphvizDocument = graphvizGenerator.apply( ontologyGraphRepresenation );
        final String graphvizGraph = graphvizDocument.apply( configuration );

        final ThrowingConsumer<OutputStream, IOException> contentProvider = outputStream -> {
            outputStream.write( graphvizGraph.getBytes() );
            outputStream.flush();
            if ( outputStream != System.out ) {
                outputStream.close();
            }
        };

        return output.fold( Try::success, this::openStream ).flatMap( stream ->
            executeDot( contentProvider, stream, new File( System.getProperty( "user.dir" ) ), configuration ) );
    }
}
