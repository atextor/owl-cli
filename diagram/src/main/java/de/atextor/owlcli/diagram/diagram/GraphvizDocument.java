package de.atextor.owlcli.diagram.diagram;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Getter
@FieldDefaults( makeFinal = true, level = AccessLevel.PRIVATE )
public class GraphvizDocument implements Function<Configuration, String> {
    public static final GraphvizDocument BLANK = new GraphvizDocument();
    public static final Configuration DEFAULT_CONFIGURATION = Configuration.builder().build();
    private static final Template GRAPHVIZ_TEMPLATE = new Template( """
        digraph G {
          rankdir = ${rankdir}

          fontname = "${fontname}"
          fontsize = ${fontsize}

          node [
            fontname = "${nodeFontname}"
            fontsize = ${nodeFontsize}
            shape = "${nodeShape}"
            margin = ${nodeMargin}
            style = "${nodeStyle}"
          ]

          ${statements}

        }
        """
    );

    @Value
    static class Statement {
        String content;

        String toFragment() {
            return content + "\n";
        }
    }

    private List<Statement> nodeStatements;
    private List<Statement> edgeStatements;

    private GraphvizDocument() {
        this( Collections.emptyList(), Collections.emptyList() );
    }

    static GraphvizDocument withNode( final Statement nodeStatement ) {
        return withNodes( Collections.singletonList( nodeStatement ) );
    }

    static GraphvizDocument withNodes( final List<Statement> nodeStatements ) {
        return new GraphvizDocument( nodeStatements, Collections.emptyList() );
    }

    static GraphvizDocument withEdge( final Statement edgeStatement ) {
        return withEdges( Collections.singletonList( edgeStatement ) );
    }

    static GraphvizDocument withEdges( final List<Statement> edgeStatements ) {
        return new GraphvizDocument( Collections.emptyList(), edgeStatements );
    }

    GraphvizDocument merge( final GraphvizDocument other ) {
        return new GraphvizDocument(
            Stream.concat( getNodeStatements().stream(), other.getNodeStatements().stream() ).collect( Collectors.toList() ),
            Stream.concat( getEdgeStatements().stream(), other.getEdgeStatements().stream() ).collect( Collectors.toList() ) );
    }

    @Override
    public String apply( final Configuration configuration ) {
        return GRAPHVIZ_TEMPLATE.apply( Map.of(
            "rankdir", configuration.layoutDirection == Configuration.LayoutDirection.TOP_TO_BOTTOM ? "TB" : "LR",
            "fontname", configuration.fontname,
            "fontsize", configuration.fontsize,
            "nodeFontname", configuration.nodeFontname,
            "nodeFontsize", configuration.nodeFontsize,
            "nodeShape", configuration.nodeShape,
            "margin", configuration.nodeMargin,
            "style", configuration.nodeStyle,
            "statements", Stream.concat( nodeStatements.stream(), edgeStatements.stream() )
                .map( Statement::toFragment )
                .collect( Collectors.joining( "   \n" ) ) ) );
    }
}
