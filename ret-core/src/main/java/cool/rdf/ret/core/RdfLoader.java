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

package cool.rdf.ret.core;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Convenience class for loading RDF models
 */
public class RdfLoader {
    private static final Logger LOG = LoggerFactory.getLogger( RdfLoader.class );

    public static final String DEFAULT_EMPTY_PREFIX = "urn:ret:empty";

    /**
     * Parses a string containing an RDF document in a given syntax into a model
     *
     * @param document the string containing literal RDF (i.e., not a file name or URL)
     * @param syntax the RDF syntax
     * @return the loaded model
     */
    public static Model load( final String document, final Lang syntax ) {
        final InputStream input = new ByteArrayInputStream( document.getBytes( StandardCharsets.UTF_8 ) );
        final Model result = load( input, syntax );
        try {
            input.close();
        } catch ( final IOException e ) {
            // Ignore
        }
        return result;
    }

    /**
     * Loads an RDF model in a given syntax from an input stream. The stream is not closed.
     *
     * @param input the input stream
     * @param syntax the syntax
     * @return the model
     */
    public static Model load( final InputStream input, final Lang syntax ) {
        return load( input, syntax, DEFAULT_EMPTY_PREFIX );
    }

    /**
     * Loads an RDF model in a given syntax from an input stream, with a given base URI. The stream is not closed.
     *
     * @param input the input stream
     * @param syntax the syntax
     * @param base the base URI
     * @return the model
     */
    public static Model load( final InputStream input, final Lang syntax, final String base ) {
        LOG.debug( "Loading {} model from {}", syntax, input );
        return RDFParser.create()
            .source( input )
            .lang( syntax )
            .base( base )
            .toModel();
    }

    /**
     * Parses a string containing an RDF/Turtle document into a model
     *
     * @param document the string containing literal RDF (i.e., not a file name or URL)
     * @return the loaded model
     */
    public static Model loadTurtle( final String document ) {
        return load( document, Lang.TURTLE );
    }
}
