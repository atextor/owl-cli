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

import io.vavr.collection.List;
import org.apache.jena.sys.JenaSystem;
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
    static {
        JenaSystem.setSubsystemRegistry( new StaticJenaSubsystemRegistry() );
        JenaSystem.init();
    }

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
