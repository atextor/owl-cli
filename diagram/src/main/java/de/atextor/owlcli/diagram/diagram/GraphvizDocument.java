package de.atextor.owlcli.diagram.diagram;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Getter
@FieldDefaults( makeFinal = true, level = AccessLevel.PRIVATE )
public class GraphvizDocument implements Function<Configuration, String> {
    public static final GraphvizDocument BLANK = new GraphvizDocument();
    public static final Configuration DEFAULT_CONFIGURATION = Configuration.builder().build();

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
        final StringBuffer buffer = new StringBuffer();
        buffer.append( "digraph G {\n" );
        buffer.append( "  rankdir = " ).append( configuration.layoutDirection == Configuration.LayoutDirection.TOP_TO_BOTTOM ? "TB" : "LR" );
        buffer.append( "\n" );
        buffer.append( "  fontname = \"" ).append( configuration.fontname ).append( "\"\n" );
        buffer.append( "  fontsize = " ).append( configuration.fontsize ).append( "\n" );
        buffer.append( "\n" );
        buffer.append( "  node [\n" );
        buffer.append( "    fontname = \"" ).append( configuration.nodeFontname ).append( "\"\n" );
        buffer.append( "    fontsize = " ).append( configuration.nodeFontsize ).append( "\n" );
        buffer.append( "    shape = \"" ).append( configuration.nodeShape ).append( "\"\n" );
        buffer.append( "    margin = " ).append( configuration.nodeMargin ).append( "\n" );
        buffer.append( "    style = \"" ).append( configuration.nodeStyle ).append( "\"\n" );
        buffer.append( "  ]\n" );
        buffer.append( "\n" );
        nodeStatements.forEach( statement -> {
            buffer.append( "   " );
            buffer.append( statement.toFragment() );
        } );
        buffer.append( "\n" );
        edgeStatements.forEach( statement -> {
            buffer.append( "   " );
            buffer.append( statement.toFragment() );
        } );
        buffer.append( "}" );
        return buffer.toString();
    }
}
