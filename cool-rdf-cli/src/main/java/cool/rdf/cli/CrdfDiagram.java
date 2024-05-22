/*
 * Copyright 2024 Andreas Textor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cool.rdf.cli;

import cool.rdf.core.Version;
import cool.rdf.diagram.owl.Configuration;
import cool.rdf.diagram.owl.DiagramGenerator;
import cool.rdf.diagram.owl.GraphvizDocument;
import cool.rdf.diagram.owl.mappers.DefaultMappingConfiguration;
import cool.rdf.diagram.owl.mappers.MappingConfiguration;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cool.rdf.cli.CrdfDiagram.COMMAND_NAME;

/**
 * The 'diagram' subcommand
 */
@SuppressWarnings( "SpellCheckingInspection" )
@CommandLine.Command( name = COMMAND_NAME,
    description = "Generate automatically-layouted diagrams for an ontology",
    descriptionHeading = "%n@|bold Description|@:%n%n",
    parameterListHeading = "%n@|bold Parameters|@:%n",
    optionListHeading = "%n@|bold Options|@:%n",
    footer = "%nSee the online documentation for details:%n" +
        "https://atextor.de/owl-cli/main/" + Version.VERSION + "/usage.html#diagram-command"
)
public class CrdfDiagram extends AbstractCommand implements Runnable {
    /**
     * The name of this subcommand
     */
    public static final String COMMAND_NAME = "diagram";

    private static final Logger LOG = LoggerFactory.getLogger( CrdfDiagram.class );

    private static final Configuration config = GraphvizDocument.DEFAULT_CONFIGURATION;

    @SuppressWarnings( "unused" )
    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--fontname" }, description = "The font to use (Default: ${DEFAULT-VALUE})" )
    private String fontname = config.fontname;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--fontsize" }, description = "Default font size (Default: ${DEFAULT-VALUE})" )
    private int fontsize = config.fontsize;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--nodefontname" }, description = "Font for nodes (Default: ${DEFAULT-VALUE})" )
    private String nodeFontName = config.nodeFontname;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--nodefontsize" }, description = "Font size for nodes (Default: ${DEFAULT-VALUE})" )
    private int nodeFontsize = config.nodeFontsize;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--nodeshape" }, description = "Node shape (Default: ${DEFAULT-VALUE})" )
    private String nodeShape = config.nodeShape;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--nodemargin" }, description = "Node margin (Default: ${DEFAULT-VALUE})" )
    private String nodeMargin = config.nodeMargin;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--nodestyle" }, description = "Node style (Default: ${DEFAULT-VALUE})" )
    private String nodeStyle = config.nodeStyle;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--format" },
        description = "Output file format, one of ${COMPLETION-CANDIDATES} (Default: ${DEFAULT-VALUE})" )
    private Configuration.Format format = config.format;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--direction" },
        description = "Diagram layout direction, one of ${COMPLETION-CANDIDATES} (Default: ${DEFAULT-VALUE})" )
    private Configuration.LayoutDirection layoutDirection = config.layoutDirection;

    @SuppressWarnings( { "SpellCheckingInspection", "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--dotbinary" }, description = "Path to dot binary (Default: ${DEFAULT-VALUE})" )
    private String dotBinary = config.dotBinary;

    @SuppressWarnings( { "SpellCheckingInspection", "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--fgcolor" }, description = "Foreground color (Default: ${DEFAULT-VALUE})" )
    private String fgColor = config.fgColor;

    @SuppressWarnings( { "SpellCheckingInspection", "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--bgcolor" }, description = "Background color (Default: ${DEFAULT-VALUE})" )
    private String bgColor = config.bgColor;

    @SuppressWarnings( "unused" )
    @CommandLine.Parameters( paramLabel = "INPUT", description = "File name or - for stdin", arity = "1",
        index = "0" )
    private String input;

    @SuppressWarnings( "unused" )
    @CommandLine.Parameters( paramLabel = "OUTPUT",
        description = "File name or - for stdout. If left out, the input file name is used, e.g. foo.ttl -> " +
            "foo.svg or stdout if INPUT is -.",
        arity = "0..1", index = "1" )
    private String output;

    @Override
    public void run() {
        final MappingConfiguration mappingConfig = DefaultMappingConfiguration.builder().build();
        openInput( input ).flatMap( inputStream -> {
                try {
                    final Configuration.LayoutDirection configureLayoutdirection;
                    final InputStream configuredInput;
                    if ( layoutDirection == Configuration.LayoutDirection.DETECT ) {
                        final byte[] bytes = IOUtils.toByteArray( inputStream );
                        configuredInput = new ByteArrayInputStream( bytes );
                        final String ontologyString = new String( bytes );
                        final Pattern pattern = Pattern.compile( Configuration.LayoutDirection.HINT_PREFIX + "([a-z_]*)" );
                        final Matcher matcher = pattern.matcher( ontologyString );

                        if ( matcher.find() ) {
                            // Either we found a #pragma in the file
                            configureLayoutdirection = Configuration.LayoutDirection.valueOf( matcher.group( 1 ).toUpperCase() );
                            if ( configureLayoutdirection == Configuration.LayoutDirection.DETECT ) {
                                throw new ErrorMessage( "pragma must not set layout direction to 'detect'" );
                            }
                        } else {
                            // Or we use whatever was set via command line
                            configureLayoutdirection = layoutDirection;
                        }
                    } else {
                        configureLayoutdirection = layoutDirection;
                        configuredInput = inputStream;
                    }

                    final Configuration configuration = Configuration.builder()
                        .fontname( fontname )
                        .fontsize( fontsize )
                        .nodeFontname( nodeFontName )
                        .nodeFontsize( nodeFontsize )
                        .nodeShape( nodeShape )
                        .nodeMargin( nodeMargin )
                        .nodeStyle( nodeStyle )
                        .format( format )
                        .layoutDirection( configureLayoutdirection )
                        .dotBinary( dotBinary )
                        .fgColor( fgColor )
                        .bgColor( bgColor )
                        .build();

                    return loadOntology( configuredInput ).flatMap( ontology ->
                        openOutput( input, Optional.ofNullable( output ), format.toString() ).flatMap( outputStream ->
                            new DiagramGenerator( configuration, mappingConfig )
                                .generate( ontology, outputStream, configuration ) ) );
                } catch ( final IOException exception ) {
                    throw new ErrorMessage( "Could not open input" );
                } catch ( final IllegalArgumentException exception ) {
                    throw new RuntimeException( "Illegal pragma" );
                }
            }
        ).onFailure( throwable -> exitWithErrorMessage( LOG, loggingMixin, throwable ) );
    }

    @Override
    public String commandName() {
        return COMMAND_NAME;
    }
}
