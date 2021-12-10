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

package de.atextor.owlcli.infer;

import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Inferrer {
    public static final Configuration DEFAULT_CONFIGURATION = Configuration.builder().build();

    private static final Logger LOG = LoggerFactory.getLogger( Inferrer.class );

    public Try<Void> infer( final URL inputUrl, final OutputStream output, final Configuration configuration ) {
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
                return infer( inputStream, output, configuration );
            }
            return Try.failure( new RuntimeException( "Got unexpected HTTP response: " + response.statusCode() ) );
        } catch ( final Exception exception ) {
            LOG.debug( "Failure during reading from URL: {}", inputUrl );
            return Try.failure( exception );
        }
    }

    public Try<Void> infer( final InputStream input, final OutputStream output, final Configuration configuration ) {
        return Try.success( null );
    }
}
