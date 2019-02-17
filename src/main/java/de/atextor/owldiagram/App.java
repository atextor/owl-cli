package de.atextor.owldiagram;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import de.atextor.owldiagram.diagram.Configuration;
import de.atextor.owldiagram.diagram.DiagramGenerator;
import de.atextor.owldiagram.diagram.GraphvizDocument;
import io.vavr.control.Try;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class App {
    private static final Configuration config = GraphvizDocument.DEFAULT_CONFIGURATION;

    private static class Arguments {
        @Parameter( names = { "--help", "-h" }, description = "Prints the arguments", help = true )
        private boolean help;

        @Parameter( description = "input [output]", required = true, variableArity = true )
        private List<String> inputOutput;

        @Parameter( names = { "--fontname" }, description = "Default font" )
        private String fontname = config.fontname;

        @Parameter( names = { "--fontsize" }, description = "Default font size" )
        private int fontsize = config.fontsize;

        @Parameter( names = { "--nodefontname" }, description = "Font for nodes" )
        private String nodeFontName = config.nodeFontname;

        @Parameter( names = { "--nodefontsize" }, description = "Font size for nodes" )
        private int nodeFontsize = config.nodeFontsize;

        @Parameter( names = { "--nodeshape" }, description = "Node shape" )
        private String nodeShape = config.nodeShape;

        @Parameter( names = { "--nodemargin" }, description = "Node margin" )
        private double nodeMargin = config.nodeMargin;

        @Parameter( names = { "--nodestyle" }, description = "Node style" )
        private String nodeStyle = config.nodeStyle;

        @Parameter( names = { "--format" }, description = "Output file format, one of: png svg" )
        private String format = config.format.toString().toLowerCase();
    }

    private static Try<OutputStream> openOutput( final List<String> inputOutput, final String targetFormat ) {
        final String inputFilename = inputOutput.get( 0 );

        if ( inputOutput.size() == 2 ) {
            final String outputFilename = inputOutput.get( 1 );
            // Output is given as - --> write to stdout
            if ( outputFilename.equals( "-" ) ) {
                return Try.success( System.out );
            }

            // Output is given as something else --> open as file
            try {
                return Try.success( new FileOutputStream( outputFilename ) );
            } catch ( final FileNotFoundException exception ) {
                return Try.failure( exception );
            }
        }

        if ( inputFilename.equals( "-" ) ) {
            // Input is stdin, outout is not given -> write to stdout
            if ( inputOutput.size() == 1 ) {
                return Try.success( System.out );
            }
        }

        // Input is something else, output is not given -> interpret input as filename,
        // change input's file extension to target format and use as output file name
        final String outputFilename = inputFilename.replaceFirst( "[.][^.]+$", "." + targetFormat );
        try {
            return Try.success( new FileOutputStream( outputFilename ) );
        } catch ( final FileNotFoundException exception ) {
            return Try.failure( exception );
        }
    }

    private static Try<InputStream> openInput( final String input ) {
        if ( input.equals( "-" ) ) {
            return Try.success( System.in );
        }
        try {
            return Try.success( new FileInputStream( input ) );
        } catch ( final FileNotFoundException exception ) {
            return Try.failure( exception );
        }
    }

    private static Configuration buildConfigurationFromArguments( final Arguments arguments ) {
        return Configuration.builder()
                .fontname( arguments.fontname )
                .fontsize( arguments.fontsize )
                .nodeFontname( arguments.nodeFontName )
                .nodeFontsize( arguments.nodeFontsize )
                .nodeShape( arguments.nodeShape )
                .nodeMargin( arguments.nodeMargin )
                .nodeStyle( arguments.nodeStyle )
                .build();
    }

    public static void main( final String[] args ) {
        final Arguments arguments = new Arguments();

        final JCommander jCommander = JCommander.newBuilder()
                .addObject( arguments )
                .build();
        jCommander.parse( args );

        if ( arguments.help || arguments.inputOutput.size() > 2 ) {
            jCommander.usage();
            System.exit( 0 );
        }

        final Configuration configuration = buildConfigurationFromArguments( arguments );
        openOutput( arguments.inputOutput, arguments.format ).flatMap( output ->
                openInput( arguments.inputOutput.get( 0 ) ).flatMap( input ->
                        new DiagramGenerator().generate( input, output, configuration ) )
        ).onFailure( throwable -> System.err.println( "Error: " + throwable.getMessage() ) );
    }
}
