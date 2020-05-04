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

import com.google.common.collect.Ordering;
import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.GraphVisitor;
import de.atextor.owlcli.diagram.graph.Node;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Serializes a graph model (i.e. a Stream of {@link GraphElement}s) to a {@link GraphvizDocument} (DOT format).
 */
public class GraphvizGenerator implements Function<Stream<GraphElement>, GraphvizDocument> {
    private final GraphVisitor<GraphvizDocument> graphVisitor;

    /**
     * Initialize the GraphvizGenerator with the necessary configuration
     *
     * @param configuration the given configuration
     */
    GraphvizGenerator( final Configuration configuration ) {
        final Node.Visitor<GraphvizDocument> nodeTypeToGraphviz =
            new GraphvizNodeVisitor( configuration.format, configuration.resourceDirectoryName );

        final Function<Edge.Decorated, GraphvizDocument> decoratedEdgeToGraphviz = edge -> {
            final String label = edge.getLabel();
            final String edgeStyle =
                edgeTypeToGraphviz( edge.getType() )
                    .map( style -> style + ", fontsize=" + configuration.fontsize )
                    .orElse( "" );
            return GraphvizDocument.withEdge( new GraphvizDocument.Statement(
                String.format( "%s -> %s [label=\"%s\", %s]", edge.getFrom().getId(), edge.getTo()
                    .getId(), label, edgeStyle ) ) );
        };

        final Function<Edge.Plain, GraphvizDocument> plainEdgeToGraphviz = edge -> {
            final String edgeStyle = edgeTypeToGraphviz( edge.getType() )
                .map( style -> style + ", fontsize=" + configuration.fontsize )
                .orElse( "" );
            return GraphvizDocument.withEdge( new GraphvizDocument.Statement(
                String.format( "%s -> %s [%s]", edge.getFrom().getId(), edge.getTo().getId(), edgeStyle ) ) );
        };

        graphVisitor = new GraphVisitor<>( nodeTypeToGraphviz, plainEdgeToGraphviz, decoratedEdgeToGraphviz );
    }

    private Optional<String> edgeTypeToGraphviz( final Edge.Type type ) {
        return switch ( type ) {
            case DEFAULT_ARROW:
                yield Optional.of( "arrowhead = normal" );
            case DASHED_ARROW:
                yield Optional.of( "arrowhead = normal, style = dashed" );
            case HOLLOW_ARROW:
                yield Optional.of( "arrowhead = empty" );
            case DOUBLE_ENDED_HOLLOW_ARROW:
                yield Optional.of( "dir = both, arrowhead = empty, arrowtail = empty" );
            case NO_ARROW:
                yield Optional.of( "arrowhead = none" );
        };
    }

    /**
     * Takes {@link GraphElement}s and turns them into the equivalent {@link GraphvizDocument}
     *
     * @param graphElements the input graph elements
     * @return the resulting GraphvizDocument
     */
    @Override
    public GraphvizDocument apply( final Stream<GraphElement> graphElements ) {
        return graphElements
            .map( graphElement -> graphElement.accept( graphVisitor ) )
            .reduce( GraphvizDocument.BLANK, GraphvizDocument::merge );
    }

    static class GraphvizNodeVisitor implements Node.Visitor<GraphvizDocument> {
        private enum Symbol {
            CLASS( "#CFA500", "●" ),
            DATA_PROPERTY( "#38A14A", "▬" ),
            OBJECT_PROPERTY( "#0079BA", "▬" ),
            ANNOTATION_PROPERTY( "#D17A00", "▬" ),
            INDIVIDUAL( "#874B82", "◆" ),
            DATA_TYPE( "#AD3B45", "●" );

            String color;
            String symbol;

            Symbol( final String color, final String symbol ) {
                this.color = color;
                this.symbol = symbol;
            }

            String getNodeValue( final String elementName ) {
                return String.format( "<FONT POINT-SIZE=\"16\" COLOR=\"%s\"><B>%s</B></FONT>  %s",
                    color, symbol, elementName );
            }
        }

        final Template literalNodeTemplate = new Template( """
            ${nodeId} [label="${value}"] """ );
        final Template htmlLabelNodeTemplate = new Template( """
            ${nodeId} [label=<${value}>] """ );
        final Template invisibleNodeTemplate = new Template( """
            ${nodeId} [label="", width="0", style="invis"] """ );
        Configuration.Format format;
        String resourceDirectoryname;

        GraphvizNodeVisitor( final Configuration.Format format, final String resourceDirectoryname ) {
            this.format = format;
            this.resourceDirectoryname = resourceDirectoryname;
        }

        @Override
        public GraphvizDocument visit( final Node.Class class_ ) {
            return generateHtmlLabelNode( class_.getId(), Symbol.CLASS.getNodeValue( class_.getName() ) );
        }

        @Override
        public GraphvizDocument visit( final Node.DataProperty dataProperty ) {
            return generateHtmlLabelNode( dataProperty.getId(), Symbol.DATA_PROPERTY
                .getNodeValue( dataProperty.getName() ) );
        }

        @Override
        public GraphvizDocument visit( final Node.ObjectProperty objectProperty ) {
            return generateHtmlLabelNode( objectProperty.getId(), Symbol.OBJECT_PROPERTY
                .getNodeValue( objectProperty.getName() ) );
        }

        @Override
        public GraphvizDocument visit( final Node.AnnotationProperty annotationProperty ) {
            return generateHtmlLabelNode( annotationProperty.getId(), Symbol.ANNOTATION_PROPERTY
                .getNodeValue( annotationProperty.getName() ) );
        }

        @Override
        public GraphvizDocument visit( final Node.Individual individual ) {
            return generateHtmlLabelNode( individual.getId(), Symbol.INDIVIDUAL.getNodeValue( individual.getName() ) );
        }

        @Override
        public GraphvizDocument visit( final Node.Literal literal ) {
            return generateLiteralNode( literal.getId(), literal.getValue() );
        }

        @Override
        public GraphvizDocument visit( final Node.PropertyChain propertyChain ) {
            final String operator = String
                .format( " <FONT COLOR=\"#0A5EA8\"><B>%s</B></FONT> ", Node.PropertyChain.OPERATOR_SYMBOL );
            final String[] parts = propertyChain.getValue().split( " " + Node.PropertyChain.OPERATOR_SYMBOL + " " );
            final String label = Arrays.stream( parts )
                .map( part -> String.format( "<FONT COLOR=\"#000000\">%s</FONT>", part ) )
                .collect( Collectors.joining( operator ) );
            return generateHtmlLabelNode( propertyChain.getId(), label );
        }

        @Override
        public GraphvizDocument visit( final Node.Datatype datatype ) {
            return generateHtmlLabelNode( datatype.getId(), Symbol.DATA_TYPE.getNodeValue( datatype.getName() ) );
        }

        @Override
        public GraphvizDocument visit( final Node.ExistentialRestriction existentialRestriction ) {
            return generateHtmlLabelNode( existentialRestriction.getId(),
                "P <FONT COLOR=\"#B200B2\"><B>some</B></FONT> C" );
        }

        @Override
        public GraphvizDocument visit( final Node.ValueRestriction valueRestriction ) {
            return generateHtmlLabelNode( valueRestriction.getId(),
                "P <FONT COLOR=\"#B200B2\"><B>value</B></FONT> v" );
        }

        @Override
        public GraphvizDocument visit( final Node.UniversalRestriction universalRestriction ) {
            return generateHtmlLabelNode( universalRestriction.getId(),
                "P <FONT COLOR=\"#B200B2\"><B>only</B></FONT>  C" );
        }

        @Override
        public GraphvizDocument visit( final Node.Intersection intersection ) {
            final String label = """
                <table border="0">
                   <tr>
                     <td border="0" fixedsize="true" width="24" height="24" align="center"><FONT POINT-SIZE="24"><B>⨅</B></FONT></td>
                   </tr>
                 </table>""";
            return generateHtmlLabelNode( intersection.getId(), " " + label );
        }

        @Override
        public GraphvizDocument visit( final Node.Union union ) {
            final String label = """
                <table border="0">
                   <tr>
                     <td border="0" fixedsize="true" width="24" height="24" align="center"><FONT POINT-SIZE="24"><B>⨆</B></FONT></td>
                   </tr>
                 </table>""";
            return generateHtmlLabelNode( union.getId(), " " + label );
        }

        @Override
        public GraphvizDocument visit( final Node.Disjointness disjointness ) {
            final String label = """
                <table border="0">
                   <tr>
                     <td border="0" fixedsize="true" width="30" height="24" align="center"><FONT POINT-SIZE="26"><B>  ⚬⚬</B></FONT></td>
                   </tr>
                 </table>""";
            return generateHtmlLabelNode( disjointness.getId(), label );
        }

        @Override
        public GraphvizDocument visit( final Node.DisjointUnion disjointUnion ) {
            final String label = """
                <table border="0">
                   <tr>
                     <td border="0" fixedsize="true" width="24" height="24" align="center"><FONT POINT-SIZE="30"><B>⚭</B></FONT></td>
                   </tr>
                 </table>""";
            return generateHtmlLabelNode( disjointUnion.getId(), " " + label );
        }

        @Override
        public GraphvizDocument visit( final Node.Equality equality ) {
            return generateHtmlLabelNode( equality.getId(), "<FONT POINT-SIZE=\"16\">=</FONT>" );
        }

        @Override
        public GraphvizDocument visit( final Node.Inverse inverse ) {
            return generateHtmlLabelNode( inverse.getId(), "<FONT COLOR=\"#B200B2\"><B>inverse</B></FONT>" );
        }

        @Override
        public GraphvizDocument visit( final Node.Inequality inequality ) {
            return generateHtmlLabelNode( inequality.getId(), "<FONT POINT-SIZE=\"16\">≠</FONT>" );
        }

        @Override
        public GraphvizDocument visit( final Node.ClosedClass closedClass ) {
            return generateHtmlLabelNode( closedClass.getId(), "<FONT POINT-SIZE=\"16\">{}</FONT>" );
        }

        @Override
        public GraphvizDocument visit( final Node.Complement complement ) {
            return generateHtmlLabelNode( complement.getId(), "<FONT COLOR=\"#00B2B2\"><B>not</B></FONT>" );
        }

        @Override
        public GraphvizDocument visit( final Node.Self self ) {
            return generateHtmlLabelNode( self.getId(), "P <FONT COLOR=\"#B200B2\"><B>self</B></FONT>" );
        }

        @Override
        public GraphvizDocument visit( final Node.ObjectMinimalCardinality objectMinimalCardinality ) {
            return generateHtmlLabelNode( objectMinimalCardinality.getId(),
                String.format( "P <FONT COLOR=\"#B200B2\"><B>min</B></FONT>  %d",
                    objectMinimalCardinality.getCardinality() ) );
        }

        @Override
        public GraphvizDocument visit( final Node.ObjectQualifiedMinimalCardinality objectQualifiedMinimalCardinality ) {
            return generateHtmlLabelNode( objectQualifiedMinimalCardinality.getId(),
                String.format( "P <FONT COLOR=\"#B200B2\"><B>min</B></FONT>  %d C",
                    objectQualifiedMinimalCardinality.getCardinality() ) );
        }

        @Override
        public GraphvizDocument visit( final Node.ObjectMaximalCardinality objectMaximalCardinality ) {
            return generateHtmlLabelNode( objectMaximalCardinality.getId(),
                String.format( "P <FONT COLOR=\"#B200B2\"><B>max</B></FONT>  %d",
                    objectMaximalCardinality.getCardinality() ) );
        }

        @Override
        public GraphvizDocument visit( final Node.ObjectQualifiedMaximalCardinality objectQualifiedMaximalCardinality ) {
            return generateHtmlLabelNode( objectQualifiedMaximalCardinality.getId(),
                String.format( "P <FONT COLOR=\"#B200B2\"><B>max</B></FONT>  %d C",
                    objectQualifiedMaximalCardinality.getCardinality() ) );
        }

        @Override
        public GraphvizDocument visit( final Node.ObjectExactCardinality objectExactCardinality ) {
            return generateHtmlLabelNode( objectExactCardinality.getId(),
                String.format( "P <FONT COLOR=\"#B200B2\"><B>exactly</B></FONT>  %d",
                    objectExactCardinality.getCardinality() ) );
        }

        @Override
        public GraphvizDocument visit( final Node.ObjectQualifiedExactCardinality objectQualifiedExactCardinality ) {
            return generateHtmlLabelNode( objectQualifiedExactCardinality.getId(),
                String.format( "P <FONT COLOR=\"#B200B2\"><B>exactly</B></FONT>  %d C",
                    objectQualifiedExactCardinality.getCardinality() ) );
        }

        @Override
        public GraphvizDocument visit( final Node.DataMinimalCardinality dataMinimalCardinality ) {
            return generateHtmlLabelNode( dataMinimalCardinality.getId(),
                String.format( "P <FONT COLOR=\"#B200B2\"><B>min</B></FONT>  %d",
                    dataMinimalCardinality.getCardinality() ) );
        }

        @Override
        public GraphvizDocument visit( final Node.DataMaximalCardinality dataMaximalCardinality ) {
            return generateHtmlLabelNode( dataMaximalCardinality.getId(),
                String.format( "P <FONT COLOR=\"#B200B2\"><B>max</B></FONT>  %d",
                    dataMaximalCardinality.getCardinality() ) );
        }

        @Override
        public GraphvizDocument visit( final Node.DataExactCardinality dataExactCardinality ) {
            return generateHtmlLabelNode( dataExactCardinality.getId(),
                String.format( "P <FONT COLOR=\"#B200B2\"><B>exactly</B></FONT>  %d",
                    dataExactCardinality.getCardinality() ) );
        }

        @Override
        public GraphvizDocument visit( final Node.Invisible invisible ) {
            return generateInvisibleNode( invisible.getId() );
        }

        @Override
        public GraphvizDocument visit( final Node.IRIReference iriReference ) {
            return generateLiteralNode( iriReference.getId(), iriReference.getIri().toString() );
        }

        @Override
        public GraphvizDocument visit( final Node.PropertyMarker propertyMarker ) {
            final Ordering<Node.PropertyMarker.Kind> markerOrder = Ordering.explicit( List.of(
                Node.PropertyMarker.Kind.FUNCTIONAL,
                Node.PropertyMarker.Kind.INVERSE_FUNCTIONAL,
                Node.PropertyMarker.Kind.TRANSITIVE,
                Node.PropertyMarker.Kind.SYMMETRIC,
                Node.PropertyMarker.Kind.ASYMMETRIC,
                Node.PropertyMarker.Kind.REFLEXIVE,
                Node.PropertyMarker.Kind.IRREFLEXIVE ) );

            final String value = propertyMarker.getKind().stream()
                .sorted( markerOrder )
                .map( kind -> kind.toString().toLowerCase().replace( "_", " " ) )
                .collect( Collectors.joining( "\\n" ) );
            return generateLiteralNode( propertyMarker.getId(), value );
        }

        private GraphvizDocument generateInvisibleNode( final Node.Id nodeId ) {
            return GraphvizDocument.withNode( new GraphvizDocument.Statement( invisibleNodeTemplate.apply(
                Map.of( "nodeId", nodeId.getId() ) ) ) );
        }

        private GraphvizDocument generateLiteralNode( final Node.Id nodeId, final String value ) {
            return GraphvizDocument.withNode( new GraphvizDocument.Statement( literalNodeTemplate.apply(
                Map.of( "nodeId", nodeId.getId(),
                    "value", value ) ) ) );
        }

        private GraphvizDocument generateHtmlLabelNode( final Node.Id nodeId, final String value ) {
            return GraphvizDocument.withNode( new GraphvizDocument.Statement( htmlLabelNodeTemplate.apply(
                Map.of( "nodeId", nodeId.getId(),
                    "value", value ) ) ) );
        }
    }
}
