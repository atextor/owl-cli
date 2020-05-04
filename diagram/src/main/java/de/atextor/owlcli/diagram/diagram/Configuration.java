package de.atextor.owlcli.diagram.diagram;

import lombok.Builder;

@Builder
public class Configuration {
    public enum Format {
        PNG, SVG;

        public String getExtension() {
            return toString().toLowerCase();
        }
    }

    public enum LayoutDirection {
        TOP_TO_BOTTOM,
        LEFT_TO_RIGHT
    }

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
    @Builder.Default
    public String resourceDirectoryName = "static";
}
