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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RdfWriter {
    public static final Configuration DEFAULT_CONFIGURATION = Configuration.builder().build();

    private static final Logger LOG = LoggerFactory.getLogger( RdfWriter.class );

    public Try<Void> write( final URL inputUrl, final OutputStream output, final Configuration configuration ) {
        try {
            final HttpClient client = HttpClient.newBuilder()
                .followRedirects( HttpClient.Redirect.ALWAYS )
                .build();
            final HttpRequest request = HttpRequest.newBuilder()
                .uri( inputUrl.toURI() )
                .build();
            final HttpResponse<String> response = client.send( request, HttpResponse.BodyHandlers.ofString() );
            if ( response.statusCode() == HttpURLConnection.HTTP_OK ) {
                final ByteArrayInputStream inputStream = new ByteArrayInputStream( response.body().getBytes() );
                return write( inputStream, output, configuration );
            }
            return Try.failure( new RuntimeException( "Got unexpected HTTP response: " + response.statusCode() ) );
        } catch ( final Exception exception ) {
            LOG.debug( "Failure during reading from URL: {}", inputUrl );
            return Try.failure( exception );
        }
    }

    public Try<Void> write( final InputStream input, final OutputStream output, final Configuration configuration ) {
        final Model model = ModelFactory.createDefaultModel();

        try {
            model.read( input, configuration.base, configurationFormatToJenaFormat( configuration.inputFormat ) );
            model.write( output, configurationFormatToJenaFormat( configuration.outputFormat ) );
        } catch ( final Exception exception ) {
            LOG.debug( "Failure during RDF I/O", exception );
            return Try.failure( exception );
        }
        return Try.success( null );
    }

    /**
     * Builds a RDF format string as expected by the lang parameter of {@link Model#read(InputStream, String, String)}
     *
     * @param format the format
     * @return the format identifier for the Jena parser
     */
    private String configurationFormatToJenaFormat( final Configuration.Format format ) {
        return switch ( format ) {
            case TURTLE -> "TURTLE";
            case RDFXML -> "RDF/XML";
            case NTRIPLE -> "N-TRIPLE";
            case N3 -> "N3";
        };
    }
}
