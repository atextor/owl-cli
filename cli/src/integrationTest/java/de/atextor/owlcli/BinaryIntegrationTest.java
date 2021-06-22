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
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * This test runs several subcommands and see if they work correctly. This is done by calling the built native
 * binary with the corresponding command line switches. This is important, because this tests whether the binary
 * was built and starts correctly.
 */
public class BinaryIntegrationTest {
    private final Runtime runtime = Runtime.getRuntime();

    static String owl;

    @BeforeAll
    public static void setup() {
        owl = System.getProperty( "owlBinary" );
    }

    @Test
    public void testWithoutArguments() throws IOException, InterruptedException {
        final Process process = runtime.exec( owl );
        final String stdout = IOUtils.toString( process.getInputStream(), StandardCharsets.UTF_8 );
        final String stderr = IOUtils.toString( process.getErrorStream(), StandardCharsets.UTF_8 );
        process.waitFor();

        assertThat( process.exitValue() ).isEqualTo( 0 );
        assertThat( stdout ).contains( "Usage: owl [-v] [--help] [--version] [COMMAND]" );
        assertThat( stderr ).isEmpty();
    }

    @Test
    public void testHelp() throws InterruptedException, IOException {
        final Process process = runtime.exec( owl + " --help" );
        final String stdout = IOUtils.toString( process.getInputStream(), StandardCharsets.UTF_8 );
        final String stderr = IOUtils.toString( process.getErrorStream(), StandardCharsets.UTF_8 );
        process.waitFor();

        assertThat( process.exitValue() ).isEqualTo( 0 );
        assertThat( stdout ).contains( "Usage: owl [-v] [--help] [--version] [COMMAND]" );
        assertThat( stdout ).contains( "See the online documentation" );
        assertThat( stderr ).isEmpty();
    }

    @Test
    public void testInvalidArguments() throws IOException, InterruptedException {
        final Process process = runtime.exec( owl + " definitelynotavalidargument" );
        final String stdout = IOUtils.toString( process.getInputStream(), StandardCharsets.UTF_8 );
        final String stderr = IOUtils.toString( process.getErrorStream(), StandardCharsets.UTF_8 );
        process.waitFor();

        assertThat( process.exitValue() ).isEqualTo( 1 );
        assertThat( stdout ).isEmpty();
        assertThat( stderr ).contains( "Error: " );
    }

    @Test
    public void testHelpDiagram() throws IOException, InterruptedException {
        final Process process = runtime.exec( owl + " help diagram" );
        final String stdout = IOUtils.toString( process.getInputStream(), StandardCharsets.UTF_8 );
        final String stderr = IOUtils.toString( process.getErrorStream(), StandardCharsets.UTF_8 );
        process.waitFor();

        assertThat( process.exitValue() ).isEqualTo( 0 );
        assertThat( stdout ).contains( "Usage: owl diagram" );
        assertThat( stdout ).contains( "--direction=" );
        assertThat( stderr ).isEmpty();
    }

    @Test
    public void testDiagramWithoutArguments() throws IOException, InterruptedException {
        final Process process = runtime.exec( owl + " diagram" );
        final String stdout = IOUtils.toString( process.getInputStream(), StandardCharsets.UTF_8 );
        final String stderr = IOUtils.toString( process.getErrorStream(), StandardCharsets.UTF_8 );
        process.waitFor();

        assertThat( process.exitValue() ).isEqualTo( 1 );
        assertThat( stdout ).isEmpty();
        assertThat( stderr ).contains( "Error: " );
    }

    @Test
    public void testDiagramWithInvalidArguments() throws IOException, InterruptedException {
        final Process process = runtime.exec( owl + " diagram definitelynotavalidargument" );
        final String stdout = IOUtils.toString( process.getInputStream(), StandardCharsets.UTF_8 );
        final String stderr = IOUtils.toString( process.getErrorStream(), StandardCharsets.UTF_8 );
        process.waitFor();

        assertThat( process.exitValue() ).isEqualTo( 1 );
        assertThat( stdout ).isEmpty();
        assertThat( stderr ).contains( "Error: " );
    }

    @ParameterizedTest
    @ArgumentsSource( ResourceArgumentsProvider.class )
    public void testDiagramGeneration( final String testFileName ) throws IOException, InterruptedException {
        final Path tempDir = Files.createTempDirectory( "owldiagram" );

        final URL input = DiagramCommandTest.class.getResource( "/" + testFileName + ".ttl" );
        final File output = tempDir.resolve( testFileName + ".ttl" ).toFile();

        assertThat(input).isNotNull();
        FileUtils.copyURLToFile( input, output );
        assertThat( output ).isFile();
        assertThat( fileContent( output ) ).isNotEmpty();

        final String command = owl + " diagram " + output.getAbsolutePath();
        final Process process = runtime.exec( command );
        final String stdout = IOUtils.toString( process.getInputStream(), StandardCharsets.UTF_8 );
        final String stderr = IOUtils.toString( process.getErrorStream(), StandardCharsets.UTF_8 );
        process.waitFor();

        if ( process.exitValue() != 0 ) {
            System.out.println( "Something went wrong for " + command );
            System.out.println( "=== stdout ===" );
            System.out.println( stdout );
            System.out.println( "=== stderr ===" );
            System.out.println( stderr );
        }
        assertThat( process.exitValue() ).isEqualTo( 0 );
        assertThat( stdout ).isEmpty();
        assertThat( stderr ).isEmpty();

        final File writtenFile = tempDir.resolve( testFileName + ".svg" ).toFile();
        assertThat( writtenFile ).isFile();
        assertThat( fileContent( writtenFile ) ).contains( "<svg".getBytes() );

        try {
            FileUtils.deleteDirectory( tempDir.toFile() );
        } catch ( final Exception e ) {
            System.err.println( "Warning: Could not delete temp directory " + tempDir );
        }
    }

    private byte[] fileContent( final File file ) {
        try {
            return FileUtils.readFileToByteArray( file );
        } catch ( final IOException exception ) {
            fail( "", exception );
        }
        return null;
    }
}
