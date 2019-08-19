package de.atextor.owlcli;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemOutRule;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandLineTest {
    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void testHelp() {
        exit.expectSystemExitWithStatus( 0 );
        exit.checkAssertionAfterwards( () -> assertThat( systemOutRule.getLog() ).contains( "Usage: " ) );
        App.main( new String[]{ "-h" } );
    }

    @Test
    public void testHelp2() {
        exit.expectSystemExitWithStatus( 0 );
        exit.checkAssertionAfterwards( () -> assertThat( systemOutRule.getLog() ).contains( "Usage: " ) );
        App.main( new String[]{ "--help" } );
    }

}
