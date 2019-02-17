package de.atextor.owldiagram.diagram;

import lombok.Builder;

@Builder
public class Configuration {
    public enum Format {
        PNG, SVG;

        public String getExtension() {
            return toString().toLowerCase();
        }
    }

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
    public double nodeMargin = 0.01;
    @Builder.Default
    public String nodeStyle = "rounded";
    @Builder.Default
    public Format format = Format.SVG;

    String toFragment() {
        return "  fontname = \"" + fontname + "\"\n" +
                "  fontsize = " + fontsize + "\n" +
                "\n" +
                "  node [\n" +
                "    fontname = \"" + nodeFontname + "\"\n" +
                "    fontsize = " + nodeFontsize + "\n" +
                "    shape = \"" + nodeShape + "\"\n" +
                "    margin = " + nodeMargin + "\n" +
                "    style = \"" + nodeStyle + "\"\n" +
                "  ]\n";
    }
}
