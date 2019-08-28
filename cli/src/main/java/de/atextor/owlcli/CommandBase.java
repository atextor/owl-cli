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
import java.util.function.Consumer;

abstract public class CommandBase<T> implements Consumer<T> {
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

    abstract T getArguments();

    abstract String getCommandName();
}
