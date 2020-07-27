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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

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
    private static final Logger LOG = LoggerFactory.getLogger( OWLCLI.class );

    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    private static final CommandLine.IParameterExceptionHandler exceptionHandler =
        ( exception, args ) -> {
            final CommandLine cmd = exception.getCommandLine();
            LOG.warn( "Error: ", exception );
            cmd.getErr().println( cmd.getHelp().fullSynopsis() );
            return 1;
        };

    private final CommandLine commandLine = new CommandLine( this );

    @CommandLine.Option( names = { "--help" }, usageHelp = true, description = "Show short help" )
    private boolean helpRequested;

    @CommandLine.Option( names = { "--version" }, description = "Show current version" )
    private boolean version;

    public static void main( final String[] args ) {
        final int exitCode = new OWLCLI().commandLine
            .addSubcommand( new OWLCLIDiagramCommand() )
            .setParameterExceptionHandler( exceptionHandler )
            .setExecutionStrategy( LoggingMixin::executionStrategy )
            .execute( args );
        System.exit( exitCode );
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
