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

package cool.rdf.ret.write;

import cool.rdf.ret.core.model.RdfModel;
import cool.rdf.turtle.formatter.TurtleFormatter;
import io.vavr.control.Try;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    public void testWriteTurtle() {
        final Configuration configuration = Configuration.builder()
            .inputFormat( Configuration.Format.TURTLE )
            .outputFormat( Configuration.Format.TURTLE ).build();

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Try<Void> result = writer.write( turtleInputStream(), out, configuration );

        assertThat( result ).hasSize( 1 );
        assertThatCode( () -> RdfModel.fromDocument( out.toString(), Lang.TURTLE ) ).doesNotThrowAnyException();
        assertThatThrownBy( () -> RdfModel.fromDocument( out.toString(), Lang.NTRIPLES ) ).hasMessageContaining( "line:" );
        assertThatThrownBy( () -> RdfModel.fromDocument( out.toString(), Lang.RDFXML ) ).hasMessageContaining( "line:" );
    }

    @Test
    public void testWriteNTriples() {
        final Configuration configuration = Configuration.builder()
            .inputFormat( Configuration.Format.TURTLE )
            .outputFormat( Configuration.Format.NTRIPLE ).build();

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Try<Void> result = writer.write( turtleInputStream(), out, configuration );

        assertThat( result ).hasSize( 1 );
        assertThatCode( () -> RdfModel.fromDocument( out.toString(), Lang.TURTLE ) ).doesNotThrowAnyException();
        // N-TRIPLES is also valid Turtle
        assertThatCode( () -> RdfModel.fromDocument( out.toString(), Lang.NTRIPLES ) ).doesNotThrowAnyException();
        assertThatThrownBy( () -> RdfModel.fromDocument( out.toString(), Lang.RDFXML ) ).hasMessageContaining( "line:" );
    }

    @Test
    public void testWriteRdfXml() {
        final Configuration configuration = Configuration.builder()
            .inputFormat( Configuration.Format.TURTLE )
            .outputFormat( Configuration.Format.RDFXML ).build();

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Try<Void> result = writer.write( turtleInputStream(), out, configuration );

        assertThat( result ).hasSize( 1 );
        assertThatThrownBy( () -> RdfModel.fromDocument( out.toString(), Lang.TURTLE ) ).hasMessageContaining( "line:" );
        assertThatThrownBy( () -> RdfModel.fromDocument( out.toString(), Lang.NTRIPLES ) ).hasMessageContaining( "line:" );
        assertThatCode( () -> RdfModel.fromDocument( out.toString(), Lang.RDFXML ) ).doesNotThrowAnyException();
    }

    @Test
    public void testReadFromUrl() throws IOException {
        final URL url = new URL( "https://raw.githubusercontent.com/atextor/turtle-formatting/main/turtle-formatting.ttl" );
        final Configuration configuration = Configuration.builder()
            .inputFormat( Configuration.Format.TURTLE )
            .outputFormat( Configuration.Format.TURTLE ).build();

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Try<Void> result = writer.write( url, out, configuration );

        assertThat( result ).hasSize( 1 );
        assertThatCode( () -> RdfModel.fromDocument( out.toString(), Lang.TURTLE ) ).doesNotThrowAnyException();
    }

    @Test
    public void testParsingEscapedUri() {
        final String modelString = """
            @prefix dc: <http://purl.org/spar/datacite/> .
            @prefix doi: <https://doi.org/> .
            @prefix : <http://example.org#> .
            @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .

            :something a :publication ;
                rdfs:label "Paper title" ;
                dc:hasIdentifier doi:10.1137\\/1.9781611970937 .
            """;
        assertThatCode( () -> {
            final Model model = ModelFactory.createDefaultModel();
            final InputStream stream = new ByteArrayInputStream( modelString.getBytes( StandardCharsets.UTF_8 ) );
            model.read( stream, TurtleFormatter.DEFAULT_EMPTY_BASE, "TURTLE" );
        } ).doesNotThrowAnyException();
    }
}
