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

package de.atextor.ret.core;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class RdfLoader {
    public static Model load( final String document, final Lang syntax ) {
        final InputStream input = new ByteArrayInputStream( document.getBytes( StandardCharsets.UTF_8 ) );
        final Model result = RDFParser.create()
            .source( input )
            .lang( syntax )
            .toModel();
        try {
            input.close();
        } catch ( final IOException e ) {
            // Ignore
        }
        return result;
    }

    public static Model loadTurtle( final String document ) {
        return load( document, Lang.TURTLE );
    }
}
