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

package de.atextor.owlcli.write;

import de.atextor.turtle.formatter.FormattingStyle;
import de.atextor.turtle.formatter.TurtleFormatter;
import lombok.Builder;

@Builder
public class Configuration {
    @Builder.Default
    public Format outputFormat = Format.TURTLE;

    @Builder.Default
    public Format inputFormat = Format.TURTLE;

    @Builder.Default
    public String base = TurtleFormatter.EMPTY_BASE;

    @Builder.Default
    public FormattingStyle formattingStyle = FormattingStyle.DEFAULT;

    public enum Format {
        TURTLE,
        RDFXML,
        NTRIPLE,
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
