package de.atextor.owldiagram;

import de.atextor.owldiagram.diagram.DiagramGenerator;
import de.atextor.owldiagram.diagram.GraphvizDocument;

import java.io.File;

public class App {
    }

    public static void main( final String[] args ) {


        final File workingDir = new File( "/home/tex/git/owl-diagram/src/main/resources/" );
        final String output = "/home/tex/git/owl-diagram/src/main/resources/test.svg";

        final DiagramGenerator generator = new DiagramGenerator();
        generator.generate( App.class.getResourceAsStream( "/test.owl" ), GraphvizDocument.DEFAULT_CONFIGURATION,
                output, workingDir );
    }
}
