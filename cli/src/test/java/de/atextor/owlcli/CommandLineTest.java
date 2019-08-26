package de.atextor.owlcli;

import org.junit.jupiter.api.Test;

import static de.atextor.owlcli.MainClassRunner.run;
import static org.assertj.core.api.Assertions.assertThat;

public class CommandLineTest {
    @Test
    public void testNoArguments() {
        final Runnable command = () -> App.main( new String[]{} );
        final MainClassRunner.ExecutionResult result = run( command );

        assertThat( result.getExitStatus() ).isEqualTo( 0 );
        assertThat( result.getStdOut() ).contains( "Usage: " );
        assertThat( result.getStdErr() ).isEmpty();
    }

    @Test
    public void testHelp() {
        final Runnable command = () -> App.main( new String[]{ "-h" } );
        final MainClassRunner.ExecutionResult result = run( command );

        assertThat( result.getExitStatus() ).isEqualTo( 0 );
        assertThat( result.getStdOut() ).contains( "Usage: " );
        assertThat( result.getStdErr() ).isEmpty();
    }

    @Test
    public void testHelp2() {
        final Runnable command = () -> App.main( new String[]{ "--help" } );
        final MainClassRunner.ExecutionResult result = run( command );

        assertThat( result.getExitStatus() ).isEqualTo( 0 );
        assertThat( result.getStdOut() ).contains( "Usage: " );
        assertThat( result.getStdErr() ).isEmpty();
    }

    @Test
    public void testInvalidArguments() {
        final Runnable command = () -> App.main( new String[]{ "definitelynotavalidargument" } );
        final MainClassRunner.ExecutionResult result = run( command );

        assertThat( result.getExitStatus() ).isEqualTo( 1 );
        assertThat( result.getStdOut() ).isEmpty();
        assertThat( result.getStdErr() ).contains( "Error: " );
    }

    @Test
    public void testDiagramWithoutParameters() {
        final Runnable command = () -> App.main( new String[]{ "diagram" } );
        final MainClassRunner.ExecutionResult result = run( command );

        assertThat( result.getExitStatus() ).isEqualTo( 1 );
        assertThat( result.getStdOut() ).isEmpty();
        assertThat( result.getStdErr() ).contains( "Error: " );
    }

    @Test
    public void testDiagramWithHelp() {
        final Runnable command = () -> App.main( new String[]{ "diagram", "--help" } );
        final MainClassRunner.ExecutionResult result = run( command );

        assertThat( result.getExitStatus() ).isEqualTo( 0 );
        assertThat( result.getStdOut() ).isNotEmpty();
        assertThat( result.getStdErr() ).isEmpty();
    }

    @Test
    public void testDiagramWithInvalidInput() {
        final Runnable command = () -> App.main( new String[]{ "diagram", "definitelynotexistingfile" } );
        final MainClassRunner.ExecutionResult result = run( command );

        assertThat( result.getExitStatus() ).isEqualTo( 1 );
        assertThat( result.getStdOut() ).isEmpty();
        assertThat( result.getStdErr() ).contains( "Error: " );
    }
}
