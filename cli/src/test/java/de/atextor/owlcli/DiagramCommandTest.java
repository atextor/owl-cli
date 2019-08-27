package de.atextor.owlcli;

import org.apache.commons.io.FileUtils;
import org.assertj.core.util.Files;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static de.atextor.owlcli.MainClassRunner.run;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class DiagramCommandTest {
    private byte[] fileContent( final File file ) {
        try {
            return FileUtils.readFileToByteArray( file );
        } catch ( final IOException exception ) {
            fail( "", exception );
        }
        return null;
    }

    @Test
    public void testWithoutParameters() {
        final Runnable command = () -> App.main( new String[]{ "diagram" } );
        final MainClassRunner.ExecutionResult result = run( command );

        assertThat( result.getExitStatus() ).isEqualTo( 1 );
        assertThat( result.getStdOut() ).isEmpty();
        assertThat( result.getStdErr() ).contains( "Error: " );
    }

    @Test
    public void testWithHelp() {
        final Runnable command = () -> App.main( new String[]{ "diagram", "--help" } );
        final MainClassRunner.ExecutionResult result = run( command );

        assertThat( result.getExitStatus() ).isEqualTo( 0 );
        assertThat( result.getStdOut() ).isNotEmpty();
        assertThat( result.getStdErr() ).isEmpty();
    }

    @Test
    public void testWithInvalidInput() {
        final Runnable command = () -> App.main( new String[]{ "diagram", "definitelynotexistingfile" } );
        final MainClassRunner.ExecutionResult result = run( command );

        assertThat( result.getExitStatus() ).isEqualTo( 1 );
        assertThat( result.getStdOut() ).isEmpty();
        assertThat( result.getStdErr() ).contains( "Error: " );
    }

    protected void testDiagramGeneration( final String testFileName ) throws IOException {
        final File tempDir = Files.newTemporaryFolder();
        assertThat( tempDir ).isEmptyDirectory();

        try {
            final URL input = DiagramCommandTest.class.getResource( "/" + testFileName + ".ttl" );
            final File output = tempDir.toPath().resolve( testFileName + ".ttl" ).toFile();
            FileUtils.copyURLToFile( input, output );
            assertThat( output ).isFile();
            assertThat( fileContent( output ) ).isNotEmpty();

            final Runnable command = () -> App.main( new String[]{ "diagram", output.getAbsolutePath() } );
            final MainClassRunner.ExecutionResult result = run( command );

            assertThat( result.getExitStatus() ).isEqualTo( 0 );
            assertThat( result.getStdOut() ).isEmpty();
            assertThat( result.getStdErr() ).isEmpty();

            final File writtenFile = tempDir.toPath().resolve( testFileName + ".svg" ).toFile();
            assertThat( writtenFile ).isFile();
            final byte[] fileContent = fileContent( writtenFile );
            assertThat( fileContent ).isNotEmpty();
            assertThat( fileContent ).contains( "<svg".getBytes() );
        } finally {
            FileUtils.deleteDirectory( tempDir );
        }
    }

    @Test
    public void testClassAssertion() throws IOException {
        testDiagramGeneration( "test-class-assertion" );
    }
}
