/*
 * This file is part of OWL-CLI.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright (c) 2020, Andreas Textor.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www
 * .gnu.org/licenses/.
 */

package de.atextor.owlcli;

import de.atextor.owlcli.diagram.diagram.Configuration;
import de.atextor.owlcli.diagram.diagram.DiagramGenerator;
import de.atextor.owlcli.diagram.diagram.GraphvizDocument;
import de.atextor.owlcli.diagram.mappers.DefaultMappingConfiguration;
import de.atextor.owlcli.diagram.mappers.MappingConfiguration;
import picocli.CommandLine;

@CommandLine.Command( name = "diagram",
    description = "Generates automatically-layouted diagrams for an ontology",
    descriptionHeading = "%n@|bold Description|@:%n%n",
    parameterListHeading = "%n@|bold Parameters|@:%n",
    optionListHeading = "%n@|bold Options|@:%n",
    footer = "%nSee the online documentation for details:%n"+
        "https://atextor.de/owl-cli/main/" + OWLCLIConfig.VERSION + "/usage.html#diagram-command"
)
public class OWLCLIDiagramCommand extends AbstractCommand implements OWLCLICommand, Runnable {
    private static final Configuration config = GraphvizDocument.DEFAULT_CONFIGURATION;

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
            .build();

        final MappingConfiguration mappingConfig = DefaultMappingConfiguration.builder().build();
        openInput( input ).flatMap( inputStream ->
            loadOntology( inputStream ).flatMap( ontology ->
                openOutput( input, output, format ).flatMap( outputStream ->
                    new DiagramGenerator( configuration, mappingConfig ).generate( ontology, outputStream, configuration ) ) )
        ).onFailure( this::exitWithErrorMessage );
    }
}
