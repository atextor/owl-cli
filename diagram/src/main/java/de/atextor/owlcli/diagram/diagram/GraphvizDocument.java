/*
 * This file is part of OWL-CLI.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright (c) 2020, Andreas Textor.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www
 * .gnu.org/licenses/.
 */

package de.atextor.owlcli.diagram.diagram;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Simple model for a Graphviz document, consisting of sets of edges and nodes
 */
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
            margin = "${nodeMargin}"
            style = "${nodeStyle}"
            height = 0.3
            width = 0.2
          ]

          ${statements}

        }
        """ );

    record Statement(String content) {
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
            Stream.concat( getNodeStatements().stream(), other.getNodeStatements().stream() )
                .collect( Collectors.toList() ),
            Stream.concat( getEdgeStatements().stream(), other.getEdgeStatements().stream() )
                .collect( Collectors.toList() ) );
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
            "nodeMargin", configuration.nodeMargin,
            "nodeStyle", configuration.nodeStyle,
            "statements", Stream.concat( nodeStatements.stream(), edgeStatements.stream() )
                .map( Statement::toFragment )
                .collect( Collectors.joining( "   \n" ) ) ) );
    }

    @Override
    public String toString() {
        return "GraphvizDocument{" +
            "nodeStatements=" + nodeStatements +
            ", edgeStatements=" + edgeStatements +
            '}';
    }
}
