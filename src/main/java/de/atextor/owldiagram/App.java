package de.atextor.owldiagram;

import de.atextor.owldiagram.diagram.DiagramGenerator;
import de.atextor.owldiagram.diagram.GraphvizDocument;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class App {
    }

    public static void main( final String[] args ) {


        try {
            final OutputStream output = new FileOutputStream( "/home/tex/git/owl-diagram/src/main/resources/test.svg" );
            final DiagramGenerator generator = new DiagramGenerator();
            generator.generate( App.class.getResourceAsStream( "/test.owl" ), output,
                    GraphvizDocument.DEFAULT_CONFIGURATION
            );
        } catch ( final FileNotFoundException exception ) {
            exception.printStackTrace();
        }

    }
}
