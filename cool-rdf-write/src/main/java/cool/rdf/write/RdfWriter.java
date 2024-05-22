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

package cool.rdf.write;

import cool.rdf.core.model.RdfModel;
import cool.rdf.formatter.FormattingStyle;
import cool.rdf.formatter.TurtleFormatter;
import io.vavr.control.Try;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * The RDF Writer is used to serialize RDF documents into various formats, while using configurable formatting for RDF/Turtle
 * specifically, using the {@link TurtleFormatter}.
 */
public class RdfWriter {
    /**
     * The default configuration
     */
    public static final Configuration DEFAULT_CONFIGURATION = Configuration.builder().build();

    private static final Logger LOG = LoggerFactory.getLogger( RdfWriter.class );

    /**
     * Writes an RDF document given by an input URL, to an output stream using a writing/formatting configuration
     *
     * @param inputUrl the input URL
     * @param output the output stream
     * @param configuration the configuration
     * @return {@link io.vavr.control.Try.Success} if writing succeeded
     */
    public Try<Void> write( final URL inputUrl, final OutputStream output, final Configuration configuration ) {
        if ( inputUrl.getProtocol().equals( "http" ) || inputUrl.getProtocol().equals( "https" ) ) {
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

        try {
            return write( inputUrl.openStream(), output, configuration );
        } catch ( final IOException exception ) {
            return Try.failure( exception );
        }
    }

    /**
     * Writes an RDF document given by an input stream, to an output stream using a writing/formatting configuration
     *
     * @param input the input stream
     * @param output the output stream
     * @param configuration the configuration
     * @return {@link io.vavr.control.Try.Success} if writing succeeded
     */
    public Try<Void> write( final InputStream input, final OutputStream output, final Configuration configuration ) {
        LOG.debug( "Load model" );
        final Model model = RdfModel.fromDocument( input, configurationFormatToJenaSyntax( configuration.inputFormat ),
            configuration.formattingStyle.emptyRdfBase );
        try {
            if ( configuration.outputFormat == Configuration.Format.TURTLE ) {
                return writeTurtle( model, output, configuration.formattingStyle );
            }
            LOG.debug( "Writing model using Jena" );
            model.write( output, configurationFormatToJenaFormat( configuration.outputFormat ) );
        } catch ( final Exception exception ) {
            LOG.debug( "Failure during RDF I/O", exception );
            return Try.failure( exception );
        }
        return Try.success( null );
    }

    /**
     * Writes an RDF model to and output stream in RDF/Turtle format, using a formatting configuration
     *
     * @param model the model
     * @param output the output stream
     * @param style the formatting style
     * @return {@link io.vavr.control.Try.Success} if writing succeeded
     */
    public Try<Void> writeTurtle( final Model model, final OutputStream output, final FormattingStyle style ) {
        LOG.debug( "Create turtle formatter" );
        final TurtleFormatter formatter = new TurtleFormatter( style );
        LOG.debug( "Writing model using TurtleFormatter" );
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

    private Lang configurationFormatToJenaSyntax( final Configuration.Format format ) {
        LOG.debug( "Determining syntax for {}", format );
        final Lang result = switch ( format ) {
            case TURTLE -> Lang.TURTLE;
            case RDFXML -> Lang.RDFXML;
            case NTRIPLE -> Lang.NTRIPLES;
            case N3 -> Lang.N3;
        };
        LOG.debug( "Target syntax: {}", result );
        return result;
    }
}
