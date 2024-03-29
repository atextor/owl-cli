/*
 * Copyright 2021 Andreas Textor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.atextor.owlcli;

import de.atextor.owlcli.diagram.diagram.Configuration;
import de.atextor.owlcli.diagram.diagram.DiagramGenerator;
import de.atextor.owlcli.diagram.diagram.GraphvizDocument;
import de.atextor.owlcli.diagram.mappers.DefaultMappingConfiguration;
import de.atextor.owlcli.diagram.mappers.MappingConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@CommandLine.Command( name = "diagram",
    description = "Generate automatically-layouted diagrams for an ontology",
    descriptionHeading = "%n@|bold Description|@:%n%n",
    parameterListHeading = "%n@|bold Parameters|@:%n",
    optionListHeading = "%n@|bold Options|@:%n",
    footer = "%nSee the online documentation for details:%n" +
        "https://atextor.de/owl-cli/main/" + OWLCLIConfig.VERSION + "/usage.html#diagram-command"
)
public class OWLCLIDiagramCommand extends AbstractCommand implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger( OWLCLIDiagramCommand.class );

    private static final Configuration config = GraphvizDocument.DEFAULT_CONFIGURATION;

    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    @CommandLine.Option( names = { "--fontname" }, description = "The font to use (Default: ${DEFAULT-VALUE})" )
    private String fontname = config.fontname;

    @CommandLine.Option( names = { "--fontsize" }, description = "Default font size (Default: ${DEFAULT-VALUE})" )
    private int fontsize = config.fontsize;

    @CommandLine.Option( names = { "--nodefontname" }, description = "Font for nodes (Default: ${DEFAULT-VALUE})" )
    private String nodeFontName = config.nodeFontname;

    @CommandLine.Option( names = { "--nodefontsize" }, description = "Font size for nodes (Default: ${DEFAULT-VALUE})" )
    private int nodeFontsize = config.nodeFontsize;

    @CommandLine.Option( names = { "--nodeshape" }, description = "Node shape (Default: ${DEFAULT-VALUE})" )
    private String nodeShape = config.nodeShape;

    @CommandLine.Option( names = { "--nodemargin" }, description = "Node margin (Default: ${DEFAULT-VALUE})" )
    private String nodeMargin = config.nodeMargin;

    @CommandLine.Option( names = { "--nodestyle" }, description = "Node style (Default: ${DEFAULT-VALUE})" )
    private String nodeStyle = config.nodeStyle;

    @CommandLine.Option( names = { "--format" },
        description = "Output file format, one of ${COMPLETION-CANDIDATES} (Default: ${DEFAULT-VALUE})" )
    private Configuration.Format format = config.format;

    @CommandLine.Option( names = { "--direction" },
        description = "Diagram layout direction, one of ${COMPLETION-CANDIDATES} (Default: ${DEFAULT-VALUE})" )
    private Configuration.LayoutDirection layoutDirection = config.layoutDirection;

    @CommandLine.Option( names = { "--dotbinary" }, description = "Path to dot binary (Default: ${DEFAULT-VALUE})" )
    private String dotBinary = config.dotBinary;

    @CommandLine.Option( names = { "--fgcolor" }, description = "Foreground color (Default: ${DEFAULT-VALUE})" )
    private String fgColor = config.fgColor;

    @CommandLine.Option( names = { "--bgcolor" }, description = "Background color (Default: ${DEFAULT-VALUE})" )
    private String bgColor = config.bgColor;

    @CommandLine.Parameters( paramLabel = "INPUT", description = "File name or - for stdin", arity = "1",
        index = "0" )
    private String input;

    @CommandLine.Parameters( paramLabel = "OUTPUT",
        description = "File name or - for stdout. If left out, the input file name is used, e.g. foo.ttl -> " +
            "foo.svg or stdout if INPUT is -.",
        arity = "0..1", index = "1" )
    private String output;

    @Override
    public void run() {
        final Configuration configuration = Configuration.builder()
            .fontname( fontname )
            .fontsize( fontsize )
            .nodeFontname( nodeFontName )
            .nodeFontsize( nodeFontsize )
            .nodeShape( nodeShape )
            .nodeMargin( nodeMargin )
            .nodeStyle( nodeStyle )
            .format( format )
            .layoutDirection( layoutDirection )
            .dotBinary( dotBinary )
            .fgColor( fgColor )
            .bgColor( bgColor )
            .build();

        final MappingConfiguration mappingConfig = DefaultMappingConfiguration.builder().build();
        openInput( input ).flatMap( inputStream ->
            loadOntology( inputStream ).flatMap( ontology ->
                openOutput( input, output, format.toString() ).flatMap( outputStream ->
                    new DiagramGenerator( configuration, mappingConfig )
                        .generate( ontology, outputStream, configuration ) ) )
        ).onFailure( throwable -> exitWithErrorMessage( LOG, loggingMixin, throwable ) );
    }
}
