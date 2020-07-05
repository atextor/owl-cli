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

package de.atextor.owlcli;

import de.atextor.owlcli.diagram.diagram.Configuration;
import io.vavr.control.Try;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class AbstractCommand {

    protected void exitWithErrorMessage( final Throwable throwable ) {
        System.err.println( "Error: " + throwable.getMessage() );
        System.exit( 1 );
    }

    protected Try<OutputStream> openOutput( final @Nonnull String input, final String output,
                                            final Configuration.Format targetFormat ) {
        if ( output != null ) {
            // Output is given as - --> write to stdout
            if ( output.equals( "-" ) ) {
                return Try.success( System.out );
            }

            // Output is given as something else --> open as file
            try {
                return Try.success( new FileOutputStream( output ) );
            } catch ( final FileNotFoundException exception ) {
                return Try.failure( exception );
            }
        }

        if ( input.equals( "-" ) ) {
            // Input is stdin, outout is not given -> write to stdout
            return Try.success( System.out );
        }

        // Input is something else, output is not given -> interpret input as filename,
        // change input's file extension to target format and use as output file name
        final String outputFilename = input.replaceFirst( "[.][^.]+$",
            "." + targetFormat.toString().toLowerCase() );
        if ( outputFilename.equals( input ) ) {
            return Try.failure( new ErrorMessage( "Can't determine an ouput filename" ) );
        }

        try {
            return Try.success( new FileOutputStream( outputFilename ) );
        } catch ( final FileNotFoundException exception ) {
            return Try.failure( exception );
        }
    }

    protected Try<InputStream> openInput( final String input ) {
        if ( input.equals( "-" ) ) {
            return Try.success( System.in );
        }
        try {
            final File inputFile = new File( input );
            if ( !inputFile.exists() ) {
                return Try.failure( new FileNotFoundException( input ) );
            }
            return Try.success( new FileInputStream( inputFile ) );
        } catch ( final FileNotFoundException exception ) {
            return Try.failure( exception );
        }
    }

    /**
     * Loads an ontology from an input stream
     *
     * @param inputStream the input stream
     * @return {@link Try.Success} with the {@link OWLOntology} on success, and a {@link Try.Failure} with the
     * {@link OWLOntologyCreationException} otherwise.
     */
    public Try<OWLOntology> loadOntology( final InputStream inputStream ) {
        final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        final OWLOntology ontology;
        try {
            ontology = manager.loadOntologyFromOntologyDocument( inputStream );
            return Try.success( ontology );
        } catch ( final OWLOntologyCreationException exception ) {
            return Try.failure( exception );
        }
    }
}