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
import io.vavr.control.Either;
import io.vavr.control.Try;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

abstract public class CommandBase<T> implements Runnable {
    T arguments;

    protected CommandBase( final T arguments ) {
        this.arguments = arguments;
    }

    protected void exitWithErrorMessage( final Throwable throwable ) {
        System.err.println( "Error: " + throwable.getMessage() );
        System.exit( 1 );
    }

    protected Try<Either<OutputStream, Path>> openOutput( final List<String> inputOutput,
                                                          final Configuration.Format targetFormat ) {
        final String inputFilename = inputOutput.get( 0 );

        if ( inputOutput.size() == 2 ) {
            final String outputFilename = inputOutput.get( 1 );
            // Output is given as - --> write to stdout
            if ( outputFilename.equals( "-" ) ) {
                return Try.success( Either.left( System.out ) );
            }

            // Output is given as something else --> open as file
            try {
                return Try.success( Either.left( new FileOutputStream( outputFilename ) ) );
            } catch ( final FileNotFoundException exception ) {
                return Try.failure( exception );
            }
        }

        if ( inputFilename.equals( "-" ) ) {
            // Input is stdin, outout is not given -> write to stdout
            if ( inputOutput.size() == 1 ) {
                return Try.success( Either.left( System.out ) );
            }
        }

        // Input is something else, output is not given -> interpret input as filename,
        // change input's file extension to target format and use as output file name
        final String outputFilename = inputFilename.replaceFirst( "[.][^.]+$",
            "." + targetFormat.toString().toLowerCase() );
        if ( outputFilename.equals( inputFilename ) ) {
            return Try.failure( new ErrorMessage( "Can't determine an ouput filename" ) );
        }

        return Try.success( Either.right( Paths.get( outputFilename ) ) );
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

    public T getArguments() {
        return arguments;
    }

    abstract String getCommandName();
}
