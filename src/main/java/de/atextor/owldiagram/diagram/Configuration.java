package de.atextor.owldiagram.diagram;

import lombok.Builder;

@Builder
class Configuration {
    @Builder.Default
    String fontname = "Verdana";
    @Builder.Default
    int fontsize = 12;
    @Builder.Default
    String nodeFontname = "Verdana";
    @Builder.Default
    int nodeFontsize = 12;
    @Builder.Default
    String nodeShape = "box";
    @Builder.Default
    double nodeMargin = 0.01;
    @Builder.Default
    String nodeStyle = "rounded";

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
