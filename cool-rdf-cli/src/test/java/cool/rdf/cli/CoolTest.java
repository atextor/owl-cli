/*
 * Copyright 2024 Andreas Textor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cool.rdf.cli;

import cool.rdf.core.model.RdfModel;
import org.apache.commons.io.FileUtils;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@ExtendWith( TestExecutionLogger.class )
public class CoolTest {
    protected CliRunner.Result runCli( final CliRunner.ExecArguments arguments ) {
        return CliRunner.runMainClass( Cool.class, arguments );
    }

    private static Path tempDir;

    // Process return codes
    private final int OK = 0;

    private final int ERROR = 1;

    @BeforeEach
    void beforeEach() throws IOException {
        tempDir = Files.createTempDirectory( "junit" );
    }

    @AfterEach
    void afterEach() {
        try {
            FileUtils.deleteDirectory( tempDir.toFile() );
        } catch ( final IOException exception ) {
            // ignore
        }
    }

    @AfterAll
    static void afterAll() {
        if ( tempDir != null && tempDir.toFile().exists() ) {
            try {
                FileUtils.deleteDirectory( tempDir.toFile() );
            } catch ( final IOException exception ) {
                // ignore
            }
        }
    }

    @Test
    void testNoArguments() {
        final CliRunner.Result result = runCli( new CliRunner.ExecArguments( List.of( "--disable-color" ) ) );
        assertThat( result.exitStatus() ).as( "command return code" ).isEqualTo( OK );
        assertThat( result.stdOut().cleaned() ).as( "stdout" ).contains( "Usage:" );
        assertThat( result.stdErr().raw() ).as( "stderr" ).isEmpty();
    }

    @Test
    void testHelp() {
        final CliRunner.Result result = runCli( new CliRunner.ExecArguments( "--disable-color", "--help" ) );
        assertThat( result.exitStatus() ).as( "command return code" ).isEqualTo( OK );
        assertThat( result.stdOut().cleaned() ).as( "stdout" ).contains( "Usage:" );
        assertThat( result.stdErr().raw() ).as( "stderr" ).isEmpty();
    }

    @Test
    void testInvalidArguments() {
        final CliRunner.Result result = runCli( new CliRunner.ExecArguments( "--disable-color", "invalid_argument" ) );
        assertThat( result.exitStatus() ).as( "command return code" ).isEqualTo( ERROR );
        assertThat( result.stdOut().raw() ).as( "stdout" ).isEmpty();
        assertThat( result.stdErr().cleaned() ).as( "stderr" ).contains( "Error:" );
    }

    @Test
    void testHelpDiagram() {
        final CliRunner.Result result = runCli( new CliRunner.ExecArguments( "--disable-color", "help", CoolDiagram.COMMAND_NAME ) );
        assertThat( result.exitStatus() ).as( "command return code" ).isEqualTo( OK );
        assertThat( result.stdOut().cleaned() ).as( "stdout" )
            .contains( "Usage: " + Cool.COMMAND_NAME + " " + CoolDiagram.COMMAND_NAME );
        assertThat( result.stdOut().cleaned() ).as( "stdout" ).contains( "--direction=" );
        assertThat( result.stdErr().raw() ).as( "stderr" ).isEmpty();
    }

    @Test
    void testDiagramWithoutArguments() {
        final CliRunner.Result result = runCli( new CliRunner.ExecArguments( "--disable-color", CoolDiagram.COMMAND_NAME ) );
        assertThat( result.exitStatus() ).as( "command return code" ).isEqualTo( ERROR );
        assertThat( result.stdOut().raw() ).as( "stdout" ).isEmpty();
        assertThat( result.stdErr().cleaned() ).as( "stderr" ).contains( "Error:" );
        assertThat( result.stdErr().cleaned() ).as( "stderr" )
            .contains( "Usage: " + Cool.COMMAND_NAME + " " + CoolDiagram.COMMAND_NAME );
    }

    @Test
    void testDiagramWithInvalidArguments() {
        final CliRunner.Result result = runCli( new CliRunner.ExecArguments( "--disable-color", CoolDiagram.COMMAND_NAME,
            "invalid_argument" ) );
        assertThat( result.exitStatus() ).as( "command return code" ).isEqualTo( ERROR );
        assertThat( result.stdOut().raw() ).as( "stdout" ).isEmpty();
        assertThat( result.stdErr().cleaned() ).as( "stderr" ).contains( "Error:" );
    }

    @ParameterizedTest
    @ArgumentsSource( ResourceArgumentsProvider.class )
    void testDiagramGeneration( final String testFileName ) throws IOException {
        final URL input = CoolTest.class.getResource( "/" + testFileName + ".ttl" );
        final File output = tempDir.resolve( testFileName + ".ttl" ).toFile();

        assertThat( input ).isNotNull();
        FileUtils.copyURLToFile( input, output );
        assertThat( output ).isFile();
        assertThat( output ).content().isNotEmpty();

        final List<String> arguments = List.of( "--disable-color", CoolDiagram.COMMAND_NAME, output.getAbsolutePath() );
        final CliRunner.Result result = runCli( new CliRunner.ExecArguments( arguments ) );
        if ( result.stdErr().raw().length > 0 || result.exitStatus() != OK ) {
            System.out.println( "stderr:" );
            System.out.println( result.stdErr().asString() );
        }
        assertThat( result.exitStatus() ).as( "command return code" ).isEqualTo( OK );
        assertThat( result.stdOut().raw() ).as( "stdout" ).isEmpty();
        assertThat( result.stdErr().raw() ).as( "stderr" ).isEmpty();
        final File writtenFile = tempDir.resolve( testFileName + ".svg" ).toFile();
        assertThat( writtenFile ).isFile();
        assertThat( writtenFile ).content().contains( "<svg" );
    }

    @Test
    void testHelpWrite() {
        final CliRunner.Result result = runCli( new CliRunner.ExecArguments( "--disable-color", "help", CoolWrite.COMMAND_NAME ) );
        assertThat( result.exitStatus() ).as( "command return code" ).isEqualTo( OK );
        assertThat( result.stdOut().cleaned() ).as( "stdout" )
            .contains( "Usage: " + Cool.COMMAND_NAME + " " + CoolWrite.COMMAND_NAME );
        assertThat( result.stdOut().cleaned() ).as( "stdout" ).contains( "--alignObjects" );
        assertThat( result.stdErr().raw() ).as( "stderr" ).isEmpty();
    }

    @Test
    void testWriteWithoutArguments() {
        final CliRunner.Result result = runCli( new CliRunner.ExecArguments( "--disable-color", CoolWrite.COMMAND_NAME ) );
        assertThat( result.exitStatus() ).as( "command return code" ).isEqualTo( ERROR );
        assertThat( result.stdOut().raw() ).as( "stdout" ).isEmpty();
        assertThat( result.stdErr().cleaned() ).as( "stderr" ).contains( "Error:" );
        assertThat( result.stdErr().cleaned() ).as( "stderr" )
            .contains( "Usage: " + Cool.COMMAND_NAME + " " + CoolWrite.COMMAND_NAME );
    }

    @Test
    void testWriteWithInvalidArguments() {
        final CliRunner.Result result = runCli( new CliRunner.ExecArguments( "--disable-color", CoolWrite.COMMAND_NAME,
            "invalid_argument" ) );
        assertThat( result.exitStatus() ).as( "command return code" ).isEqualTo( ERROR );
        assertThat( result.stdOut().raw() ).as( "stdout" ).isEmpty();
        assertThat( result.stdErr().cleaned() ).as( "stderr" ).contains( "Error:" );
    }

    @Test
    void testWriteTurtle() {
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
        final List<String> arguments = List.of( "--disable-color", CoolWrite.COMMAND_NAME, "-" );
        final CliRunner.StreamContent stdin = new CliRunner.StreamContent( turtleDocument.getBytes() );
        final CliRunner.Result result = runCli( new CliRunner.ExecArguments( arguments, stdin ) );
        assertThat( result.exitStatus() ).as( "command return code" ).isEqualTo( OK );
        assertThat( result.stdErr().raw() ).as( "stderr" ).isEmpty();
        assertThatCode( () -> RdfModel.fromTurtle( result.stdOut().cleaned() ) ).doesNotThrowAnyException();
    }

    @Test
    void testWriteRdfXml() {
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
        final List<String> arguments = List.of( "--disable-color", CoolWrite.COMMAND_NAME, "-i", "rdfxml", "-o", "rdfxml", "-" );
        final CliRunner.StreamContent stdin = new CliRunner.StreamContent( rdfXmlDocument.getBytes() );
        final CliRunner.Result result = runCli( new CliRunner.ExecArguments( arguments, stdin ) );
        assertThat( result.exitStatus() ).as( "command return code" ).isEqualTo( OK );
        assertThat( result.stdErr().raw() ).as( "stderr" ).isEmpty();
        assertThatCode( () -> RdfModel.fromDocument( result.stdOut().cleaned(), Lang.RDFXML ) ).doesNotThrowAnyException();
    }

    @Test
    void testWriteNTriple() {
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
        final List<String> arguments = List.of( "--disable-color", CoolWrite.COMMAND_NAME, "-i", "ntriple", "-o", "ntriple", "-" );
        final CliRunner.StreamContent stdin = new CliRunner.StreamContent( ntripleDocument.getBytes() );
        final CliRunner.Result result = runCli( new CliRunner.ExecArguments( arguments, stdin ) );
        assertThat( result.exitStatus() ).as( "command return code" ).isEqualTo( OK );
        assertThat( result.stdErr().raw() ).as( "stderr" ).isEmpty();
        assertThatCode( () -> RdfModel.fromDocument( result.stdOut().cleaned(), Lang.NTRIPLES ) ).doesNotThrowAnyException();
    }

    @Test
    void testWriteTurtleWithEmptyBase() {
        final String turtleDocument = """
            @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
            @prefix : <http://test.de#> .

            :Person a rdfs:Class ;
                :foo <> .
            """;
        final List<String> arguments = List.of( "--disable-color", CoolWrite.COMMAND_NAME, "-" );
        final CliRunner.StreamContent stdin = new CliRunner.StreamContent( turtleDocument.getBytes() );
        final CliRunner.Result result = runCli( new CliRunner.ExecArguments( arguments, stdin ) );
        assertThat( result.exitStatus() ).as( "command return code" ).isEqualTo( OK );
        assertThat( result.stdErr().raw() ).as( "stderr" ).isEmpty();
        assertThatCode( () -> RdfModel.fromTurtle( result.stdOut().cleaned() ) ).doesNotThrowAnyException();
        assertThat( result.stdOut().cleaned() ).isEqualToIgnoringWhitespace( turtleDocument );
    }
}
