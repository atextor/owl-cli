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
        public boolean help;

        @Parameter( description = "input [output]", required = true, variableArity = true )
        public List<String> inputOutput;

        @Parameter( names = { "--fontname" }, description = "Default font" )
        public String fontname = config.fontname;

        @Parameter( names = { "--fontsize" }, description = "Default font size" )
        public int fontsize = config.fontsize;

        @Parameter( names = { "--nodefontname" }, description = "Font for nodes" )
        public String nodeFontName = config.nodeFontname;

        @Parameter( names = { "--nodefontsize" }, description = "Font size for nodes" )
        public int nodeFontsize = config.nodeFontsize;

        @Parameter( names = { "--nodeshape" }, description = "Node shape" )
        public String nodeShape = config.nodeShape;

        @Parameter( names = { "--nodemargin" }, description = "Node margin" )
        public double nodeMargin = config.nodeMargin;

        @Parameter( names = { "--nodestyle" }, description = "Node style" )
        public String nodeStyle = config.nodeStyle;

        @Parameter( names = { "--format" }, description = "Output file format", converter = FormatParser.class )
        public Configuration.Format format = config.format;

        @Parameter( names = { "--direction" }, description = "Diagram layout direction", converter =
            LayoutDirectionParser.class )
        public Configuration.LayoutDirection layoutDirection = config.layoutDirection;

        @Parameter( names = { "--dotbinary" }, description = "Path to dot binary" )
        public String dotBinary = config.dotBinary;

        @Parameter( names = { "--resourcedir-name" }, description = "Name of the resource directory" )
        public String resourceDirectoryName = config.resourceDirectoryName;
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
            .resourceDirectoryName( arguments.resourceDirectoryName )
            .build();
    }

    @Override
    public void accept( final Arguments arguments ) {
        if ( arguments.help ) {
            System.out.println( "Input can be a relative or absolute filename, or - for stdin." );
            System.out.println( "Output can be a relative or absolute filename, or - for stdout. If left out, the " +
                "output filename is the input filename with its file extension changed, e.g. foo.owl -> foo.svg." );
            System.exit( 0 );
        }

        if ( arguments.inputOutput == null || arguments.inputOutput.size() > 2 ) {
            exitWithErrorMessage( new ErrorMessage( "Error: Invalid number of input/output arguments" ) );
        }

        final Configuration configuration = buildConfigurationFromArguments( arguments );
        final MappingConfiguration mappingConfig = DefaultMappingConfiguration.builder().build();
        openInput( arguments.inputOutput.get( 0 ) ).flatMap( input ->
            openOutput( arguments.inputOutput, arguments.format ).flatMap( output ->
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
