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

package cool.rdf.ret.infer;

import de.atextor.turtle.formatter.TurtleFormatter;
import lombok.Builder;

/**
 * The configuration for inferring from models
 */
@Builder
public class Configuration {
    /**
     * The format used to parse the input
     */
    @SuppressWarnings( "CanBeFinal" )
    @Builder.Default
    public Format inputFormat = Format.TURTLE;

    /**
     * The RDF base URL
     */
    @SuppressWarnings( "CanBeFinal" )
    @Builder.Default
    public String base = TurtleFormatter.DEFAULT_EMPTY_BASE;

    /**
     * The possible input formats
     */
    public enum Format {
        /**
         * RDF/Turtle
         */
        TURTLE,

        /**
         * RDF/XML
         */
        RDFXML,

        /**
         * N-Triple
         */
        NTRIPLE,

        /**
         * N3 format
         */
        N3;

        @Override
        public String toString() {
            return switch ( this ) {
                case TURTLE -> "turtle";
                case RDFXML -> "rdfxml";
                case NTRIPLE -> "ntriple";
                case N3 -> "n3";
            };
        }
    }
}
