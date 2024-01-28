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

import de.atextor.turtle.formatter.FormattingStyle;
import lombok.Builder;

/**
 * The configuration for writing RDF document
 */
@Builder
public class Configuration {
    /**
     * The format used to write the document
     */
    @SuppressWarnings( "CanBeFinal" )
    @Builder.Default
    public Format outputFormat = Format.TURTLE;

    /**
     * The format used to parse the input
     */
    @SuppressWarnings( "CanBeFinal" )
    @Builder.Default
    public Format inputFormat = Format.TURTLE;

    /**
     * The formatting style to use
     */
    @Builder.Default
    public FormattingStyle formattingStyle = FormattingStyle.DEFAULT;

    @Override
    public String toString() {
        return "Configuration{" +
            "outputFormat=" + outputFormat +
            ", inputFormat=" + inputFormat +
            ", formattingStyle=" + formattingStyle +
            '}';
    }

    /**
     * The possible input/output formats
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
