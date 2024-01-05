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

package de.atextor.ret.diagram.owl;

import io.vavr.control.Try;

import java.util.function.Function;

/**
 * SVG post processor that embeds the 'owlcli' font used for the element type and disjoint union symbols
 * as a data URI. This is to make sure that the symbols are rendered the same in all browsers.
 */
public class FontEmbedder implements Function<Try<String>, Try<String>> {
    /*
     * This is the encoded symbol font that contains exactly the glyphs used in the diagrams.
     * The steps to obtain this are:
     * 1. Install fonttools (pip3 install fonttools) to get the pyftsubset command
     * 2. Create a template font from an existing font file that contains glyphs for the used code points:
     * pyftsubset somefont.otf --output-file=owlcli.otf --unicodes-file=<(echo U+02B24 U+025AC U+025C6 U+026AC U+026AD)
     * 3. Adjust the glyphs using fontforge, File->Generate Fonts... save as .woff
     * 4. cat owlcli.woff | base64 -w 0
     */
    private final static String FONT =
        "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";

    private final static String CSS_BLOCK =
        "<style>@font-face{font-family:\"owlcli\";src:url(\"data:application/font-woff;charset=utf-8;base64,"
            + FONT + "\");}</style>";

    @Override
    public Try<String> apply( final Try<String> svgDocument ) {
        return svgDocument.flatMap( content -> {
            final int svgStart = content.indexOf( "<svg" );
            if ( svgStart == -1 ) {
                return Try.failure( new IndexOutOfBoundsException() );
            }
            final int svgEnd = content.indexOf( ">", svgStart + 1 );
            if ( svgEnd == -1 ) {
                return Try.failure( new IndexOutOfBoundsException() );
            }

            final String result = content.substring( 0, svgEnd + 1 ) + CSS_BLOCK +
                content.substring( svgEnd + 1, content.length() - 1 );
            return Try.success( result );
        } );
    }
}
