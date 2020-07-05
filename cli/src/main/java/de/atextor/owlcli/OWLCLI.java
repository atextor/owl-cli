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

import io.vavr.control.Try;
import picocli.CommandLine;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

@CommandLine.Command( name = "owlcli",
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

    CommandLine commandLine = new CommandLine( this );

    @CommandLine.Option( names = { "--help" }, usageHelp = true, description = "Show short help" )
    private boolean helpRequested;

    @CommandLine.Option( names = { "--version" }, description = "Show current version" )
    private boolean version;

    public static void main( final String[] args ) {
        final int exitCode = new OWLCLI().commandLine
            .addSubcommand( new OWLCLIDiagramCommand() )
            .setParameterExceptionHandler( exceptionHandler )
            .execute( args );
        System.exit( exitCode );
    }

    private Try<Properties> applicationProperties() {
        final Properties properties = new Properties();
        try ( final InputStream inputStream = getClass().getResourceAsStream( "/application.properties" ) ) {
            properties.load( inputStream );
        } catch ( final IOException e ) {
            return Try.failure( e );
        }

        return Try.success( properties );
    }

    @Override
    public void run() {
        if ( helpRequested ) {
            System.exit( 0 );
        }

        if ( version ) {
            applicationProperties().forEach( properties ->
                System.out.printf( "owl-cli version: %s build date: %s%n",
                    properties.get( "application.version" ), properties.get( "application.buildDate" ) ) );

            System.exit( 0 );
        }

        System.out.println( commandLine.getHelp().fullSynopsis() );
    }
}
