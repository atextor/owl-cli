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

import com.beust.jcommander.DefaultUsageFormatter;
import com.beust.jcommander.IUsageFormatter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.google.common.collect.Sets;
import io.vavr.control.Try;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

public class App extends CommandBase<App.Arguments> {
    private JCommander jCommander;

    public App() {
        super( new Arguments() );
    }

    public static void main( final String[] args ) {
        final App app = new App();
        final Arguments arguments = app.getArguments();
        final Set<CommandBase<?>> commandsExceptHelp = Set.of( new DiagramCommand() );
        final Set<CommandBase<?>> commands = Sets.union( commandsExceptHelp,
            Set.of( new HelpCommand( commandsExceptHelp ) ) );

        final JCommander jCommander = JCommander.newBuilder().addObject( arguments ).build();
        commands.forEach( command -> jCommander.addCommand( command.getCommandName(), command.getArguments() ) );
        final IUsageFormatter usageFormatter = new CustomUsageFormatter( jCommander );
        jCommander.setUsageFormatter( usageFormatter );

        app.setjCommander( jCommander );

        if ( args.length == 0 ) {
            app.printHelpAndExit();
        }

        app.parseCommandLineArguments( args ).onFailure( app::exitWithErrorMessage );
        app.run();

        commands.forEach( command -> {
            if ( jCommander.getParsedCommand().equals( command.getCommandName() ) ) {
                command.run();
                System.exit( 0 );
            }
        } );

        app.printHelpAndExit();
    }

    private void setjCommander( final JCommander jCommander ) {
        this.jCommander = jCommander;
    }

    private Try<Void> parseCommandLineArguments( final String[] args ) {
        try {
            jCommander.parse( args );
            return Try.success( null );
        } catch ( final IllegalArgumentException exception ) {
            if ( exception.getMessage().startsWith( "No enum constant" ) ) {
                return Try.failure( new RuntimeException( "Invalid argument: " + exception.getMessage()
                    .substring( exception.getMessage().lastIndexOf( '.' ) + 1 ).toLowerCase() ) );
            }
            return Try.failure( exception );
        } catch ( final ParameterException exception ) {
            return Try.failure( new RuntimeException( "Invalid parameters. Start with --help for more information." ) );
        }
    }

    @Override
    String getCommandName() {
        return "main";
    }

    @Override
    String getHelp() {
        return "Command line tool for ontology engineering";
    }

    private void printHelpAndExit() {
        System.out.println( getHelp() );
        jCommander.usage();
        System.out.println( "See the online documentation: https://atextor.de/owl-cli/" );
        System.exit( 0 );
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
        if ( arguments.help ) {
            printHelpAndExit();
        }

        applicationProperties().forEach( properties ->
            System.out.printf( "owl-cli version: %s build date: %s%n",
                properties.get( "application.version" ), properties.get( "application.buildDate" ) ) );

        System.exit( 0 );
    }

    static class Arguments {
        @Parameter( names = "--help", description = "Prints the arguments", help = true )
        private boolean help;

        @Parameter( names = "--version", description = "Show current version", help = true )
        private boolean version;
    }

    private static class CustomUsageFormatter extends DefaultUsageFormatter {
        public CustomUsageFormatter( final JCommander commander ) {
            super( commander );
        }

        @Override
        public void usage( final StringBuilder out, final String indent ) {
            final StringBuilder builder = new StringBuilder();
            super.usage( builder, indent );
            out.append( builder.toString().replace( "<main class>", "owl" ) );
        }
    }
}
