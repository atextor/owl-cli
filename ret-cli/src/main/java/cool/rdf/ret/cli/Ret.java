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

package cool.rdf.ret.cli;

import ch.qos.logback.classic.Level;
import cool.rdf.ret.core.Version;
import io.vavr.collection.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.util.logging.LogManager;

import static cool.rdf.ret.cli.Ret.COMMAND_NAME;

/**
 * The main class for the command line interface
 */
@CommandLine.Command( name = COMMAND_NAME,
    description = "Command line tool for ontology engineering",
    subcommands = { CommandLine.HelpCommand.class },
    headerHeading = "@|bold Usage|@:%n%n",
    descriptionHeading = "%n@|bold Description|@:%n%n",
    parameterListHeading = "%n@|bold Parameters|@:%n",
    optionListHeading = "%n@|bold Options|@:%n",
    footer = "%nSee the online documentation: https://atextor.de/owl-cli/"
)
public class Ret implements Runnable {
    /**
     * The name of the top level command
     */
    public static final String COMMAND_NAME = "ret";

    private static final Logger LOG = LoggerFactory.getLogger( Ret.class );

    private static void printError( final CommandLine commandLine, final Exception exception ) {
        final Level logLevel = ( (LoggingMixin) commandLine.getMixins().values().iterator().next() ).calcLogLevel();
        if ( logLevel.equals( Level.DEBUG ) || logLevel.equals( Level.TRACE ) ) {
            LOG.debug( exception.getMessage(), exception );
        } else {
            final PrintWriter writer = commandLine.getErr();
            writer.println( "Error: " + exception.getMessage() );
        }
    }

    private static final CommandLine.IParameterExceptionHandler PARAMETER_EXCEPTION_HANDLER =
        ( exception, args ) -> {
            final CommandLine cmd = exception.getCommandLine();
            printError( cmd, exception );
            cmd.getErr().println( cmd.getHelp().fullSynopsis() );
            return 1;
        };

    private static final CommandLine.IExecutionExceptionHandler EXECUTION_EXCEPTION_HANDLER =
        ( exception, commandLine, parseResult ) -> {
            printError( commandLine, exception );
            return 1;
        };

    private final CommandLine commandLine = new CommandLine( this );

    @SuppressWarnings( "unused" )
    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    @SuppressWarnings( "unused" )
    @CommandLine.Option( names = { "-h", "--help" }, usageHelp = true, description = "Show short help" )
    private boolean helpRequested;

    @SuppressWarnings( "unused" )
    @CommandLine.Option( names = { "--version" }, description = "Show current version" )
    private boolean version;

    @SuppressWarnings( "unused" )
    @CommandLine.Option( names = { "--disable-color", "-D" }, description = "Disable colored output" )
    private boolean disableColor;

    /**
     * The command's main function
     *
     * @param args the arguments
     */
    public static void main( final String[] args ) {
        // Check disabling color switch before PicoCLI initialization
        boolean disableColor = false;
        for ( final String arg : args ) {
            if ( arg.equals( "--disable-color" ) || arg.equals( "-D" ) ) {
                disableColor = true;
                break;
            }
        }

        if ( disableColor ) {
            System.setProperty( "picocli.ansi", "false" );
        }

        LogManager.getLogManager().reset();
        final List<AbstractCommand> commands = List.of(
            new RetDiagram(),
            new RetWrite(),
            new RetInfer()
        );
        final CommandLine cmd = commands.foldLeft( new Ret().commandLine, CommandLine::addSubcommand )
            .setParameterExceptionHandler( PARAMETER_EXCEPTION_HANDLER )
            .setExecutionExceptionHandler( EXECUTION_EXCEPTION_HANDLER )
            .setCaseInsensitiveEnumValuesAllowed( true )
            .setExecutionStrategy( LoggingMixin::executionStrategy );
        commands.forEach( command -> command.registerTypeConverters( cmd ) );

        final int resultCode = cmd.execute( args );
        System.exit( resultCode );
    }

    @Override
    public void run() {
        if ( helpRequested ) {
            System.exit( 0 );
        }

        if ( version ) {
            System.out.printf( "%s %s%n  commit: %s%n  build date: %s%n", COMMAND_NAME,
                Version.VERSION, Version.COMMIT_ID.substring( 0, 7 ), Version.BUILD_DATE );
            System.exit( 0 );
        }

        System.out.println( commandLine.getHelp().fullSynopsis() );
    }
}
