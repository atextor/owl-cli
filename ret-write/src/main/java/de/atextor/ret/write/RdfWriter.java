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

package de.atextor.ret.write;

import de.atextor.turtle.formatter.FormattingStyle;
import de.atextor.turtle.formatter.TurtleFormatter;
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
            model.read( input, configuration.formattingStyle.emptyRdfBase,
                configurationFormatToJenaFormat( configuration.inputFormat ) );
            if ( configuration.outputFormat == Configuration.Format.TURTLE ) {
                return writeTurtle( model, output, configuration.formattingStyle );
            }
            model.write( output, configurationFormatToJenaFormat( configuration.outputFormat ) );
        } catch ( final Exception exception ) {
            LOG.debug( "Failure during RDF I/O", exception );
            return Try.failure( exception );
        }
        return Try.success( null );
    }

    public Try<Void> writeTurtle( final Model model, final OutputStream output, final FormattingStyle style ) {
        final TurtleFormatter formatter = new TurtleFormatter( style );
        formatter.accept( model, output );
        return Try.success( null );
    }

    /**
     * Builds an RDF format string as expected by the lang parameter of {@link Model#read(InputStream, String, String)}
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
