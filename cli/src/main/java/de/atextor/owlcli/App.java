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
import de.atextor.owlcli.diagram.diagram.Configuration;
import io.vavr.control.Try;

public class App extends CommandBase<App.Arguments> {
    private JCommander jCommander;

    static class Arguments {
        @Parameter( names = { "--help", "-h" }, description = "Prints the arguments", help = true )
        private boolean help;
    }

    private void setjCommander( final JCommander jCommander ) {
        this.jCommander = jCommander;
    }

    private Try<Void> parseCommandLineArguments( final String[] args ) {
        try {
            jCommander.parse( args );
            return Try.success( null );
        } catch ( final IllegalArgumentException exception ) {
            if ( exception.getMessage().contains( Configuration.Format.class.getSimpleName() ) ) {
                return Try.failure( new RuntimeException( "Invalid format" ) );
            }
            if ( exception.getMessage().contains( Configuration.LayoutDirection.class.getSimpleName() ) ) {
                return Try.failure( new RuntimeException( "Invalid layout direction" ) );
            }
            return Try.failure( exception );
        } catch ( final ParameterException exception ) {
            return Try.failure( new RuntimeException( "Invalid parameters. Start with --help for more information." ) );
        }
    }

    @Override
    Arguments getArguments() {
        return new Arguments();
    }

    @Override
    String getCommandName() {
        return "main";
    }

    @Override
    public void accept( final Arguments arguments ) {
        if ( arguments.help ) {
            jCommander.usage();
            System.exit( 0 );
        }
    }

    public static void main( final String[] args ) {
        final Arguments arguments = new App().getArguments();

        final DiagramCommand diagramCommand = new DiagramCommand();
        final DiagramCommand.Arguments diagramArguments = diagramCommand.getArguments();

        final JCommander jCommander = JCommander.newBuilder()
            .addObject( arguments )
            .addCommand( diagramCommand.getCommandName(), diagramArguments )
            .build();
        final IUsageFormatter usageFormatter = new CustomUsageFormatter( jCommander );
        jCommander.setUsageFormatter( usageFormatter );

        final App app = new App();
        app.setjCommander( jCommander );

        if ( args.length == 0 ) {
            jCommander.usage();
            System.exit( 0 );
        }

        app.parseCommandLineArguments( args ).onFailure( app::exitWithErrorMessage );
        app.accept( arguments );

        if ( jCommander.getParsedCommand().equals( diagramCommand.getCommandName() ) ) {
            new DiagramCommand().accept( diagramArguments );
            System.exit( 0 );
        }

        jCommander.usage();
        System.exit( 0 );
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
