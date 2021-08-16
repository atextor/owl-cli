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
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
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

    /**
     * This is set by the gradle build. If you want to run the tests in the IDE, you need to add
     * -DowlBinary=/path/to/owl to the VM arguments.
     */
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

        assertThat( input ).isNotNull();
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

    @Test
    public void testHelpWrite() throws IOException, InterruptedException {
        final Process process = runtime.exec( owl + " help write" );
        final String stdout = IOUtils.toString( process.getInputStream(), StandardCharsets.UTF_8 );
        final String stderr = IOUtils.toString( process.getErrorStream(), StandardCharsets.UTF_8 );
        process.waitFor();

        assertThat( process.exitValue() ).isEqualTo( 0 );
        assertThat( stdout ).contains( "Usage: owl write" );
        assertThat( stdout ).contains( "--alignObjects" );
        assertThat( stderr ).isEmpty();
    }

    @Test
    public void testWriteWithoutArguments() throws IOException, InterruptedException {
        final Process process = runtime.exec( owl + " write" );
        final String stdout = IOUtils.toString( process.getInputStream(), StandardCharsets.UTF_8 );
        final String stderr = IOUtils.toString( process.getErrorStream(), StandardCharsets.UTF_8 );
        process.waitFor();

        assertThat( process.exitValue() ).isEqualTo( 1 );
        assertThat( stdout ).isEmpty();
        assertThat( stderr ).contains( "Error: " );
    }

    private byte[] fileContent( final File file ) {
        try {
            return FileUtils.readFileToByteArray( file );
        } catch ( final IOException exception ) {
            fail( "", exception );
        }
        return null;
    }

    private InputStream turtleInputStream() {
        final String turtleDocument = """
            @prefix : <http://test.de#> .
            @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .

            :Person a rdfs:Class .
            :name a rdfs:Property .
            :address a rdfs:Property .
            :city a rdfs:Property .
            :Max a :Person ;
                :name "Max" ;
                :address [
                    :city "City Z"
                ] .
            """;
        return new ByteArrayInputStream( turtleDocument.getBytes() );
    }

    private InputStream rdfXmlInputStream() {
        final String rdfXmlDocument = """
            <rdf:RDF
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns="http://test.de#"
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" >
              <rdf:Description rdf:about="http://test.de#city">
                <rdf:type rdf:resource="http://www.w3.org/2000/01/rdf-schema#Property"/>
              </rdf:Description>
              <rdf:Description rdf:about="http://test.de#Max">
                <address rdf:nodeID="A0"/>
                <name>Max</name>
                <rdf:type rdf:resource="http://test.de#Person"/>
              </rdf:Description>
              <rdf:Description rdf:nodeID="A0">
                <city>City Z</city>
              </rdf:Description>
              <rdf:Description rdf:about="http://test.de#Person">
                <rdf:type rdf:resource="http://www.w3.org/2000/01/rdf-schema#Class"/>
              </rdf:Description>
              <rdf:Description rdf:about="http://test.de#name">
                <rdf:type rdf:resource="http://www.w3.org/2000/01/rdf-schema#Property"/>
              </rdf:Description>
              <rdf:Description rdf:about="http://test.de#address">
                <rdf:type rdf:resource="http://www.w3.org/2000/01/rdf-schema#Property"/>
              </rdf:Description>
            </rdf:RDF>
            """;
        return new ByteArrayInputStream( rdfXmlDocument.getBytes() );
    }

    private InputStream ntripleInputStream() {
        final String ntripleDocument = """
            <http://test.de#Person> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> .
            <http://test.de#name> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Property> .
            <http://test.de#address> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Property> .
            _:gen0 <http://test.de#city> "City Z"^^<http://www.w3.org/2001/XMLSchema#string> .
            <http://test.de#Max> <http://test.de#address> _:gen0 .
            <http://test.de#Max> <http://test.de#name> "Max"^^<http://www.w3.org/2001/XMLSchema#string> .
            <http://test.de#Max> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://test.de#Person> .
            <http://test.de#city> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Property> .
            """;
        return new ByteArrayInputStream( ntripleDocument.getBytes() );
    }

    private Model parseModel( final String document, final String format ) {
        final Model model = ModelFactory.createDefaultModel();
        try {
            model.read( new StringReader( document ), "", format );
            return model;
        } catch ( final Throwable t ) {
            return null;
        }
    }

    private boolean canBeParsedAs( final String document, final String format, final int expectedNumberOfStatements ) {
        final Model model = parseModel( document, format );
        return model != null && model.listStatements().toList().size() == expectedNumberOfStatements;
    }


    @Test
    public void testWriteTurtle() throws InterruptedException, IOException {
        final Process process = runtime.exec( owl + " write -" );
        final BufferedReader stdoutReader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
        final BufferedReader stderrReader = new BufferedReader( new InputStreamReader( process.getErrorStream() ) );
        IOUtils.copy( turtleInputStream(), process.getOutputStream() );
        process.getOutputStream().close();
        process.waitFor();

        final String stdout = IOUtils.toString( stdoutReader );
        final String stderr = IOUtils.toString( stderrReader );

        assertThat( process.exitValue() ).isEqualTo( 0 );
        assertThat( canBeParsedAs( stdout, "TURTLE", 8 ) ).isTrue();
        assertThat( stderr ).isEmpty();
    }

    @Test
    public void testWriteRdfXml() throws InterruptedException, IOException {
        final Process process = runtime.exec( owl + " write -o rdfxml -" );
        final BufferedReader stdoutReader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
        final BufferedReader stderrReader = new BufferedReader( new InputStreamReader( process.getErrorStream() ) );
        IOUtils.copy( turtleInputStream(), process.getOutputStream() );
        process.getOutputStream().close();
        process.waitFor();

        final String stdout = IOUtils.toString( stdoutReader );
        final String stderr = IOUtils.toString( stderrReader );

        assertThat( process.exitValue() ).isEqualTo( 0 );
        assertThat( canBeParsedAs( stdout, "RDF/XML", 8 ) ).isTrue();
        assertThat( stderr ).isEmpty();
    }

    @Test
    public void testWriteNtriple() throws InterruptedException, IOException {
        final Process process = runtime.exec( owl + " write -o ntriple -" );
        final BufferedReader stdoutReader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
        final BufferedReader stderrReader = new BufferedReader( new InputStreamReader( process.getErrorStream() ) );
        IOUtils.copy( turtleInputStream(), process.getOutputStream() );
        process.getOutputStream().close();
        process.waitFor();

        final String stdout = IOUtils.toString( stdoutReader );
        final String stderr = IOUtils.toString( stderrReader );

        assertThat( process.exitValue() ).isEqualTo( 0 );
        assertThat( canBeParsedAs( stdout, "N-TRIPLE", 8 ) ).isTrue();
        assertThat( stderr ).isEmpty();
    }

    @Test
    public void testReadRdfXml() throws InterruptedException, IOException {
        final Process process = runtime.exec( owl + " write -i rdfxml -o turtle -" );
        final BufferedReader stdoutReader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
        final BufferedReader stderrReader = new BufferedReader( new InputStreamReader( process.getErrorStream() ) );
        IOUtils.copy( rdfXmlInputStream(), process.getOutputStream() );
        process.getOutputStream().close();
        process.waitFor();

        final String stdout = IOUtils.toString( stdoutReader );
        final String stderr = IOUtils.toString( stderrReader );

        assertThat( process.exitValue() ).isEqualTo( 0 );
        assertThat( canBeParsedAs( stdout, "TURTLE", 8 ) ).isTrue();
        assertThat( stderr ).isEmpty();
    }

    @Test
    public void testReadNtriple() throws InterruptedException, IOException {
        final Process process = runtime.exec( owl + " write -i ntriple -o turtle -" );
        final BufferedReader stdoutReader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
        final BufferedReader stderrReader = new BufferedReader( new InputStreamReader( process.getErrorStream() ) );
        IOUtils.copy( ntripleInputStream(), process.getOutputStream() );
        process.getOutputStream().close();
        process.waitFor();

        final String stdout = IOUtils.toString( stdoutReader );
        final String stderr = IOUtils.toString( stderrReader );

        assertThat( process.exitValue() ).isEqualTo( 0 );
        assertThat( canBeParsedAs( stdout, "TURTLE", 8 ) ).isTrue();
        assertThat( stderr ).isEmpty();
    }
}
