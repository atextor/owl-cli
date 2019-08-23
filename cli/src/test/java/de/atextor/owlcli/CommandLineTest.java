package de.atextor.owlcli;

import org.junit.jupiter.api.Test;

import static de.atextor.owlcli.MainClassRunner.run;
import static org.assertj.core.api.Assertions.assertThat;

public class CommandLineTest {
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
}
