package de.atextor.owlcli;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import de.atextor.owlcli.diagram.diagram.Configuration;
import de.atextor.owlcli.diagram.diagram.DiagramGenerator;
import de.atextor.owlcli.diagram.diagram.GraphvizDocument;
import de.atextor.owlcli.diagram.mappers.DefaultMappingConfiguration;
import de.atextor.owlcli.diagram.mappers.MappingConfiguration;

import java.util.List;

public class DiagramCommand extends CommandBase<DiagramCommand.Arguments> {
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

    @Parameters( commandDescription = "Generate diagrams for OWL ontologies" )
    static class Arguments {
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

    @Override
    public void accept( final Arguments arguments ) {
        if ( arguments.inputOutput.size() > 2 ) {
            exitWithErrorMessage( new ErrorMessage( "Invalid number of input/output arguments" ) );
        }

        if ( arguments.help ) {
            System.out.println( "Input can be a relative or absolute filename, or - for stdin." );
            System.out.println( "Output can be a relative or absolute filename, or - for stdout. If left out, the " +
                "output filename is the input filename with its file extension changed, e.g. foo.owl -> foo.svg." );
            System.exit( 0 );
        }

        final Configuration configuration = buildConfigurationFromArguments( arguments );
        final MappingConfiguration mappingConfig = DefaultMappingConfiguration.builder().build();
        openOutput( arguments.inputOutput, arguments.format ).flatMap( output ->
            openInput( arguments.inputOutput.get( 0 ) ).flatMap( input ->
                new DiagramGenerator( configuration, mappingConfig ).generate( input, output, configuration ) )
        ).onFailure( this::exitWithErrorMessage );
    }

    @Override
    Arguments getArguments() {
        return new DiagramCommand.Arguments();
    }

    @Override
    String getCommandName() {
        return "diagram";
    }
}
