package de.atextor.owlcli;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import de.atextor.owlcli.diagram.diagram.Configuration;
import de.atextor.owlcli.diagram.diagram.DiagramGenerator;
import de.atextor.owlcli.diagram.diagram.GraphvizDocument;
import de.atextor.owlcli.diagram.mappers.DefaultMappingConfiguration;
import de.atextor.owlcli.diagram.mappers.MappingConfiguration;
import io.vavr.control.Try;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class App {
    private static final Configuration config = GraphvizDocument.DEFAULT_CONFIGURATION;

    private static class FormatParser implements IStringConverter<Configuration.Format> {
        @Override
        public Configuration.Format convert( final String value ) {
            return Configuration.Format.valueOf( value.toUpperCase() );
        }
    }

    private static class LayoutDirectionParser implements IStringConverter<Configuration.LayoutDirection> {
        @Override
        public Configuration.LayoutDirection convert( final String value ) {
            return Configuration.LayoutDirection.valueOf( value.toUpperCase() );
        }
    }

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

        @Parameter( names = { "--format" }, description = "Output file format", converter = FormatParser.class )
        private Configuration.Format format = config.format;

        @Parameter( names = { "--direction" }, description = "Diagram layout direction", converter =
                LayoutDirectionParser.class )
        private Configuration.LayoutDirection layoutDirection = config.layoutDirection;

        @Parameter( names = { "--dotbinary" }, description = "Path to dot binary" )
        private String dotBinary = config.dotBinary;
    }

    private static Try<OutputStream> openOutput( final List<String> inputOutput,
                                                 final Configuration.Format targetFormat ) {
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
        final String outputFilename = inputFilename.replaceFirst( "[.][^.]+$",
            "." + targetFormat.toString().toLowerCase() );
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

    private static Try<Void> parseCommandLineArguments( final String[] args, final JCommander jCommander ) {
        try {
            jCommander.parse( args );
            return Try.success( null );
        } catch ( final IllegalArgumentException exception ) {
            if ( exception.getMessage().contains( Configuration.Format.class.getSimpleName() ) ) {
                return Try.failure( new RuntimeException( "Invalid format" ) );
            }
            if ( exception.getMessage().contains( Configuration.LayoutDirection.class.getSimpleName() ) ) {
                return Try.failure( new RuntimeException( "Invalid layout direction" ) );
            }
            return Try.failure( exception );
        } catch ( final ParameterException exception ) {
            return Try.failure( new RuntimeException( "Invalid parameters. Start with --help for more information." ) );
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
            .format( arguments.format )
            .layoutDirection( arguments.layoutDirection )
            .dotBinary( arguments.dotBinary )
            .build();
    }

    private static void exitWithErrorMessage( final Throwable throwable ) {
        System.err.println( "Error: " + throwable.getMessage() );
        System.exit( 1 );
    }

    private static void exitWithHelp( final JCommander jCommander ) {
        System.out.println( "Generate diagrams for OWL ontologies" );
        System.out.println();
        jCommander.usage();
        System.out.println();
        System.out.println( "Input can be a relative or absolute filename, or - for stdin." );
        System.out.println( "Output can be a relative or absolute filename, or - for stdout. If left out, the " +
            "output filename is the input filename with its file extension changed, e.g. foo.owl -> foo.svg." );
        System.exit( 0 );
    }

    public static void main( final String[] args ) {
        final Arguments arguments = new Arguments();

        final JCommander jCommander = JCommander.newBuilder()
            .addObject( arguments )
            .build();

        parseCommandLineArguments( args, jCommander ).onFailure( App::exitWithErrorMessage );

        if ( arguments.help || arguments.inputOutput.size() > 2 ) {
            exitWithHelp( jCommander );
        }

        final Configuration configuration = buildConfigurationFromArguments( arguments );
        final MappingConfiguration mappingConfig = DefaultMappingConfiguration.builder().build();
        openOutput( arguments.inputOutput, arguments.format ).flatMap( output ->
            openInput( arguments.inputOutput.get( 0 ) ).flatMap( input ->
                new DiagramGenerator( configuration, mappingConfig ).generate( input, output, configuration ) )
        ).onFailure( App::exitWithErrorMessage );
    }
}
