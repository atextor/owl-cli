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

package de.atextor.owlcli.write;

import io.vavr.control.Try;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;

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

    private boolean canBeParsedAs( final String document, final String format, final int expectedNumberOfStatements ) {
        final Model model = ModelFactory.createDefaultModel();
        try {
            model.read( new StringReader( document ), "", format );
        } catch ( final Throwable t ) {
            return false;
        }
        return model.listStatements().toList().size() == expectedNumberOfStatements;
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
}
