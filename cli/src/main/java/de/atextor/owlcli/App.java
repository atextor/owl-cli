package de.atextor.owlcli;

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

    private String getHelp() {
        final StringBuilder builder = new StringBuilder();
        jCommander.usage( builder );
        return builder.toString().replace( "<main class>", "owl" );
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
            System.out.println( getHelp() );
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

        final App app = new App();
        app.setjCommander( jCommander );
        app.parseCommandLineArguments( args ).onFailure( app::exitWithErrorMessage );

        app.accept( arguments );

        if ( jCommander.getParsedCommand().equals( diagramCommand.getCommandName() ) ) {
            new DiagramCommand().accept( diagramArguments );
            System.exit( 0 );
        }

        System.out.println( app.getHelp() );
    }
}
