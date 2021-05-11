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

import io.vavr.collection.List;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.util.logging.LogManager;

@CommandLine.Command( name = "owl",
    description = "Command line tool for ontology engineering",
    subcommands = { CommandLine.HelpCommand.class },
    headerHeading = "@|bold Usage|@:%n%n",
    descriptionHeading = "%n@|bold Description|@:%n%n",
    parameterListHeading = "%n@|bold Parameters|@:%n",
    optionListHeading = "%n@|bold Options|@:%n",
    footer = "%nSee the online documentation: https://atextor.de/owl-cli/"
)
public class OWLCLI implements Runnable {
    private static final CommandLine.IParameterExceptionHandler exceptionHandler =
        ( exception, args ) -> {
            final CommandLine cmd = exception.getCommandLine();
            final PrintWriter writer = cmd.getErr();
            writer.println( "Error: " + exception.getMessage() );
            cmd.getErr().println( cmd.getHelp().fullSynopsis() );
            return 1;
        };

    private final CommandLine commandLine = new CommandLine( this );

    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    @CommandLine.Option( names = { "--help" }, usageHelp = true, description = "Show short help" )
    private boolean helpRequested;

    @CommandLine.Option( names = { "--version" }, description = "Show current version" )
    private boolean version;

    public static void main( final String[] args ) {
        LogManager.getLogManager().reset();
        final List<AbstractCommand> commands = List.of( new OWLCLIDiagramCommand(), new OWLCLIWriteCommand() );
        final CommandLine cmd = commands.foldLeft( new OWLCLI().commandLine, CommandLine::addSubcommand )
            .setParameterExceptionHandler( exceptionHandler )
            .setExecutionStrategy( LoggingMixin::executionStrategy );
        commands.forEach( command -> command.registerTypeConverters( cmd ) );
        System.exit( cmd.execute( args ) );
    }

    @Override
    public void run() {
        if ( helpRequested ) {
            System.exit( 0 );
        }

        if ( version ) {
            System.out.printf( "owl-cli version: %s build date: %s%n", OWLCLIConfig.VERSION, OWLCLIConfig.BUILD_DATE );
            System.exit( 0 );
        }

        System.out.println( commandLine.getHelp().fullSynopsis() );
    }
}
