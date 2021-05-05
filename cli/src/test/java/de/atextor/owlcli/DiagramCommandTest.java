/*
 * This file is part of OWL-CLI.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright (c) 2020, Andreas Textor.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www
 * .gnu.org/licenses/.
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
