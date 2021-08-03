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

package de.atextor.owlcli.write;

import io.vavr.control.Try;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

public class RdfWriterTest {
    private final RdfWriter writer = new RdfWriter();

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
    public void testWriteTurtle() {
        final Configuration configuration = Configuration.builder()
            .inputFormat( Configuration.Format.TURTLE )
            .outputFormat( Configuration.Format.TURTLE ).build();

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Try<Void> result = writer.write( turtleInputStream(), out, configuration );

        assertThat( result ).hasSize( 1 );
        assertThat( canBeParsedAs( out.toString(), "TURTLE", 8 ) ).isTrue();
        assertThat( canBeParsedAs( out.toString(), "N-TRIPLE", 8 ) ).isFalse();
        assertThat( canBeParsedAs( out.toString(), "RDF/XML", 8 ) ).isFalse();
    }

    @Test
    public void testWriteNTriples() {
        final Configuration configuration = Configuration.builder()
            .inputFormat( Configuration.Format.TURTLE )
            .outputFormat( Configuration.Format.NTRIPLE ).build();

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Try<Void> result = writer.write( turtleInputStream(), out, configuration );

        assertThat( result ).hasSize( 1 );
        // N-TRIPLES is also valid Turtle
        assertThat( canBeParsedAs( out.toString(), "TURTLE", 8 ) ).isTrue();
        assertThat( canBeParsedAs( out.toString(), "N-TRIPLE", 8 ) ).isTrue();
        assertThat( canBeParsedAs( out.toString(), "RDF/XML", 8 ) ).isFalse();
    }

    @Test
    public void testWriteRdfXml() {
        final Configuration configuration = Configuration.builder()
            .inputFormat( Configuration.Format.TURTLE )
            .outputFormat( Configuration.Format.RDFXML ).build();

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Try<Void> result = writer.write( turtleInputStream(), out, configuration );

        assertThat( result ).hasSize( 1 );
        assertThat( canBeParsedAs( out.toString(), "TURTLE", 8 ) ).isFalse();
        assertThat( canBeParsedAs( out.toString(), "N-TRIPLE", 8 ) ).isFalse();
        assertThat( canBeParsedAs( out.toString(), "RDF/XML", 8 ) ).isTrue();
    }

    @Test
    @Disabled("purl.org seems unreliable, disable flaky test until better solution is found")
    public void testReadFromUrl() throws IOException {
        final URL url = new URL( "http://purl.org/atextor/ontology/turtle-formatting" );
        final Configuration configuration = Configuration.builder()
            .inputFormat( Configuration.Format.TURTLE )
            .outputFormat( Configuration.Format.TURTLE ).build();

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Try<Void> result = writer.write( url, out, configuration );

        assertThat( result ).hasSize( 1 );
        assertThat( parseModel( out.toString(), "TURTLE" ) ).isNotNull();
    }
}
