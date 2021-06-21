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

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

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
        final Runnable command = () -> OWLCLI.main( new String[]{ "diagram" } );
        final MainClassRunner.ExecutionResult result = run( command );

        assertThat( result.exitStatus() ).isEqualTo( 1 );
        assertThat( result.stdOut() ).isEmpty();
        assertThat( result.stdErr() ).contains( "Error: " );
    }

    @Test
    public void testWithInvalidInput() {
        final Runnable command = () -> OWLCLI.main( new String[]{ "diagram", "definitelynotexistingfile" } );
        final MainClassRunner.ExecutionResult result = run( command );

        assertThat( result.exitStatus() ).isEqualTo( 1 );
        assertThat( result.stdOut() ).isEmpty();
        assertThat( result.stdErr() ).contains( "Error: " );
    }

    @ParameterizedTest
    @ArgumentsSource( ResourceArgumentsProvider.class )
    public void testDiagramGeneration( final String testFileName ) throws IOException {
        final Path tempDir = Files.createTempDirectory( "owldiagram" );

        final URL input = DiagramCommandTest.class.getResource( "/" + testFileName + ".ttl" );
        final File output = tempDir.resolve( testFileName + ".ttl" ).toFile();

        FileUtils.copyURLToFile( input, output );
        assertThat( output ).isFile();
        assertThat( fileContent( output ) ).isNotEmpty();

        System.out.println( "Running: diagram " + output.getAbsolutePath() );
        final Runnable command = () -> OWLCLI.main( new String[]{ "diagram", output.getAbsolutePath() } );
        final MainClassRunner.ExecutionResult result = run( command );

        System.out.println( result.stdOut() );
        System.out.println( result.stdErr() );

        if ( result.exitStatus() != 0 ) {
            System.out.println( "Something went wrong:" );
            System.out.println( "=== stdout ===" );
            System.out.println( result.stdOut() );
            System.out.println( "=== stderr ===" );
            System.out.println( result.stdErr() );
        }
        assertThat( result.exitStatus() ).isEqualTo( 0 );
        assertThat( result.stdOut() ).isEmpty();
        assertThat( result.stdErr() ).isEmpty();

        final File writtenFile = tempDir.resolve( testFileName + ".svg" ).toFile();
        assertThat( writtenFile ).isFile();
        assertThat( fileContent( writtenFile ) ).contains( "<svg".getBytes() );

        try {
            FileUtils.deleteDirectory( tempDir.toFile() );
        } catch ( final Exception e ) {
            System.err.println( "Warning: Could not delete temp directory " + tempDir );
        }
    }
}
