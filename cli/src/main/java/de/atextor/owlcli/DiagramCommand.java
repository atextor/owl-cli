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

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import de.atextor.owlcli.diagram.diagram.Configuration;
import de.atextor.owlcli.diagram.diagram.DiagramGenerator;
import de.atextor.owlcli.diagram.diagram.GraphvizDocument;
import de.atextor.owlcli.diagram.mappers.DefaultMappingConfiguration;
import de.atextor.owlcli.diagram.mappers.MappingConfiguration;

import java.util.List;

/**
 * The command that takes an OWL ontology as input and genrates a diagram as output.
 * This wraps the diagram module in the project and calls its main functionality, {@link DiagramGenerator}.
 */
public class DiagramCommand extends CommandBase<DiagramCommand.Arguments> {
    private static final Configuration config = GraphvizDocument.DEFAULT_CONFIGURATION;

    public DiagramCommand() {
        super( new Arguments() );
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
    public void run() {
        if ( arguments.inputOutput == null || arguments.inputOutput.size() > 2 ) {
            exitWithErrorMessage( new ErrorMessage( "Error: Invalid number of input/output arguments" ) );
        }

        final Configuration configuration = buildConfigurationFromArguments( arguments );
        final MappingConfiguration mappingConfig = DefaultMappingConfiguration.builder().build();
        openInput( arguments.inputOutput.get( 0 ) ).flatMap( input ->
            loadOntology( input ).flatMap( ontology ->
                openOutput( arguments.inputOutput, arguments.format ).flatMap( output ->
                    new DiagramGenerator( configuration, mappingConfig ).generate( ontology, output, configuration ) ) )
        ).onFailure( this::exitWithErrorMessage );
    }

    @Override
    String getCommandName() {
        return "diagram";
    }

    @Override
    String getHelp() {
        return """
            Input can be a relative or absolute filename, or - for stdin.
            Output can be a relative or absolute filename, or - for stdout. If left out, the
            output filename is the input filename with its file extension changed, e.g. foo.owl -> foo.svg.
            """;
    }

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
        public String nodeMargin = config.nodeMargin;

        @Parameter( names = { "--nodestyle" }, description = "Node style" )
        public String nodeStyle = config.nodeStyle;

        @Parameter( names = { "--format" }, description = "Output file format", converter = FormatParser.class )
        public Configuration.Format format = config.format;

        @Parameter( names = { "--direction" }, description = "Diagram layout direction", converter =
            LayoutDirectionParser.class )
        public Configuration.LayoutDirection layoutDirection = config.layoutDirection;

        @Parameter( names = { "--dotbinary" }, description = "Path to dot binary" )
        public String dotBinary = config.dotBinary;
    }
}
