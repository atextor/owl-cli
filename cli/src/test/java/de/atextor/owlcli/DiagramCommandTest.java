package de.atextor.owlcli;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import static de.atextor.owlcli.MainClassRunner.run;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class DiagramCommandTest {
    @TempDir
    File tempDir;

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

    @ParameterizedTest
    @ArgumentsSource( ResourceArgumentsProvider.class )
    public void testDiagramGeneration( final String testFileName ) throws IOException {
        final URL input = DiagramCommandTest.class.getResource( "/" + testFileName + ".ttl" );
        final File output = tempDir.toPath().resolve( testFileName + ".ttl" ).toFile();

        FileUtils.copyURLToFile( input, output );
        assertThat( output ).isFile();
        assertThat( fileContent( output ) ).isNotEmpty();

        final Runnable command = () -> App.main( new String[]{ "diagram", output.getAbsolutePath() } );
        final MainClassRunner.ExecutionResult result = run( command );

        System.out.println( result.getStdOut() );
        System.out.println( result.getStdErr() );

        assertThat( result.getExitStatus() ).isEqualTo( 0 );
        assertThat( result.getStdOut() ).isEmpty();
        assertThat( result.getStdErr() ).isEmpty();

        final Path workingDirectory = tempDir.toPath();
        final Path resourceDirectory = workingDirectory.resolve( "static" );
        assertThat( resourceDirectory.toFile().isDirectory() );

        final File writtenFile = workingDirectory.resolve( testFileName + ".svg" ).toFile();
        assertThat( writtenFile ).isFile();
        assertThat( fileContent( writtenFile ) ).contains( "<svg".getBytes() );
    }
}
