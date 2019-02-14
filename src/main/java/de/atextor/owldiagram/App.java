package de.atextor.owldiagram;

import de.atextor.owldiagram.diagram.DiagramGenerator;
import de.atextor.owldiagram.diagram.GraphvizDocument;
import io.vavr.control.Try;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class App {
    private static Try<OutputStream> open( final String fileName ) {
        try {
            return Try.success( new FileOutputStream( fileName ) );
        } catch ( final FileNotFoundException exception ) {
            return Try.failure( exception );
        }
    }

    public static void main( final String[] args ) {


        final Try<OutputStream> outputStream = open( "/home/tex/git/owl-diagram/src/main/resources/test.svg" );

        final Try<Void> result = outputStream.flatMap( output -> {
            final DiagramGenerator generator = new DiagramGenerator();
            return generator.generate( App.class.getResourceAsStream( "/test.owl" ), output,
                    GraphvizDocument.DEFAULT_CONFIGURATION );
        } );

        result.onFailure( throwable -> {
            System.err.println( "Error: " + throwable.getMessage() );
        } );
    }
}
