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

import lombok.Builder;

/**
 * The configuration that controls the visual output
 */
@Builder
public class Configuration {
    /**
     * The name (and possibly path) to the GraphViz dot binary
     */
    @SuppressWarnings( "CanBeFinal" )
    @Builder.Default
    public String dotBinary = "dot";

    /**
     * The default font name
     */
    @SuppressWarnings( "CanBeFinal" )
    @Builder.Default
    public String fontname = "Verdana";

    /**
     * The default font size
     */
    @Builder.Default
    public int fontsize = 12;

    /**
     * The name of the font for nodes
     */
    @SuppressWarnings( "CanBeFinal" )
    @Builder.Default
    public String nodeFontname = "Verdana";

    /**
     * The size of the font for nodes
     */
    @SuppressWarnings( "CanBeFinal" )
    @Builder.Default
    public int nodeFontsize = 12;

    /**
     * The default node shape, see <a href="https://graphviz.org/doc/info/shapes.html">Node Shapes</a> for the possible options
     */
    @Builder.Default
    public String nodeShape = "box";

    /**
     * The margin values to be used for shapes, see <a href="https://graphviz.org/docs/attrs/margin/">margin</a> for syntax
     */
    @SuppressWarnings( "CanBeFinal" )
    @Builder.Default
    public String nodeMargin = "0.05,0.0";

    /**
     * The style to use for nodes, see <a href="https://graphviz.org/docs/attr-types/style/">style</a> for details
     */
    @Builder.Default
    public String nodeStyle = "rounded";

    /**
     * The background color for nodes
     */
    @SuppressWarnings( "CanBeFinal" )
    @Builder.Default
    public String bgColor = "white";

    /**
     * The foreground color (lines and text)
     */
    @SuppressWarnings( "CanBeFinal" )
    @Builder.Default
    public String fgColor = "black";

    /**
     * The output format
     */
    @Builder.Default
    public Format format = Format.SVG;

    /**
     * The diagram layout direction
     */
    @SuppressWarnings( "CanBeFinal" )
    @Builder.Default
    public LayoutDirection layoutDirection = LayoutDirection.LEFT_TO_RIGHT;

    /**
     * The possible formats for diagram generation
     */
    public enum Format {
        /**
         * PNG format
         */
        PNG,
        /**
         * SVG format
         */
        SVG;

        /**
         * The file extension according to the format, e.g. "png" or "svg"
         *
         * @return the file extension
         */
        public String getExtension() {
            return toString().toLowerCase();
        }

        @Override
        public String toString() {
            return switch ( this ) {
                case PNG -> "png";
                case SVG -> "svg";
            };
        }
    }

    /**
     * The possible directions in which the generated diagrams are aligned
     */
    public enum LayoutDirection {
        /**
         * Diagrams are aligned vertically, with the root node on top
         */
        TOP_TO_BOTTOM,

        /**
         * Diagrams are aligned horizontally, with the root node on the left
         */
        LEFT_TO_RIGHT,

        /**
         * Check if the file contains the {@link #HINT_PREFIX} followed by either "top_to_bottom" or "left_to_right"
         */
        DETECT;

        public static final String HINT_PREFIX = "#pragma diagram: ";

        @Override
        public String toString() {
            return switch ( this ) {
                case TOP_TO_BOTTOM -> "top_to_bottom";
                case LEFT_TO_RIGHT -> "left_to_right";
                case DETECT -> "detect";
            };
        }
    }
}
