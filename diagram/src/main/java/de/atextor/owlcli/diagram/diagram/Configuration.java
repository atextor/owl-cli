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

package de.atextor.owlcli.diagram.diagram;

import lombok.Builder;

/**
 * The configuration that controls the visual output
 */
@Builder
public class Configuration {
    @Builder.Default
    public String dotBinary = "dot";

    @Builder.Default
    public String fontname = "Verdana";

    @Builder.Default
    public int fontsize = 12;

    @Builder.Default
    public String nodeFontname = "Verdana";

    @Builder.Default
    public int nodeFontsize = 12;

    @Builder.Default
    public String nodeShape = "box";

    @Builder.Default
    public String nodeMargin = "0.05,0.0";

    @Builder.Default
    public String nodeStyle = "rounded";

    @Builder.Default
    public Format format = Format.SVG;

    @Builder.Default
    public LayoutDirection layoutDirection = LayoutDirection.LEFT_TO_RIGHT;

    public enum Format {
        PNG,
        SVG;

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

    public enum LayoutDirection {
        TOP_TO_BOTTOM,
        LEFT_TO_RIGHT;

        @Override
        public String toString() {
            return switch ( this ) {
                case TOP_TO_BOTTOM -> "top_to_bottom";
                case LEFT_TO_RIGHT -> "left_to_right";
            };
        }
    }
}
