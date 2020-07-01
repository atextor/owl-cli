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
import de.atextor.owlcli.diagram.graph.node.AnnotationProperty;
import de.atextor.owlcli.diagram.graph.node.Class;
import de.atextor.owlcli.diagram.graph.node.ClosedClass;
import de.atextor.owlcli.diagram.graph.node.Complement;
import de.atextor.owlcli.diagram.graph.node.DataExactCardinality;
import de.atextor.owlcli.diagram.graph.node.DataMaximalCardinality;
import de.atextor.owlcli.diagram.graph.node.DataMinimalCardinality;
import de.atextor.owlcli.diagram.graph.node.DataProperty;
import de.atextor.owlcli.diagram.graph.node.Datatype;
import de.atextor.owlcli.diagram.graph.node.DisjointUnion;
import de.atextor.owlcli.diagram.graph.node.Disjointness;
import de.atextor.owlcli.diagram.graph.node.Equality;
import de.atextor.owlcli.diagram.graph.node.ExistentialRestriction;
import de.atextor.owlcli.diagram.graph.node.IRIReference;
import de.atextor.owlcli.diagram.graph.node.Individual;
import de.atextor.owlcli.diagram.graph.node.Inequality;
import de.atextor.owlcli.diagram.graph.node.Intersection;
import de.atextor.owlcli.diagram.graph.node.Inverse;
import de.atextor.owlcli.diagram.graph.node.Invisible;
import de.atextor.owlcli.diagram.graph.node.Key;
import de.atextor.owlcli.diagram.graph.node.Literal;
import de.atextor.owlcli.diagram.graph.node.ObjectExactCardinality;
import de.atextor.owlcli.diagram.graph.node.ObjectMaximalCardinality;
import de.atextor.owlcli.diagram.graph.node.ObjectMinimalCardinality;
import de.atextor.owlcli.diagram.graph.node.ObjectProperty;
import de.atextor.owlcli.diagram.graph.node.ObjectQualifiedExactCardinality;
import de.atextor.owlcli.diagram.graph.node.ObjectQualifiedMaximalCardinality;
import de.atextor.owlcli.diagram.graph.node.ObjectQualifiedMinimalCardinality;
import de.atextor.owlcli.diagram.graph.node.PropertyChain;
import de.atextor.owlcli.diagram.graph.node.PropertyMarker;
import de.atextor.owlcli.diagram.graph.node.Rule;
import de.atextor.owlcli.diagram.graph.node.Self;
import de.atextor.owlcli.diagram.graph.node.Union;
import de.atextor.owlcli.diagram.graph.node.UniversalRestriction;
import de.atextor.owlcli.diagram.graph.node.ValueRestriction;
import org.apache.commons.text.StringEscapeUtils;

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
        final Node.Visitor<GraphvizDocument> nodeTypeToGraphviz = new GraphvizNodeVisitor( configuration );

        final Function<Edge.Decorated, GraphvizDocument> decoratedEdgeToGraphviz = edge -> {
            final String label = edge.getLabel().getLabel();
            final String edgeStyle =
                edgeTypeToGraphviz( edge.getType() )
                    .map( style -> String.format( "%s, fontsize=%d fontname=\"%s\"", style, configuration.fontsize,
                        configuration.fontname ) )
                    .orElse( "" );
            return GraphvizDocument.withEdge( new GraphvizDocument.Statement(
                String.format( "%s -> %s [label=\"%s\", %s]", edge.getFrom().getId().getId(), edge.getTo()
                    .getId().getId(), label, edgeStyle ) ) );
        };

        final Function<Edge.Plain, GraphvizDocument> plainEdgeToGraphviz = edge -> {
            final String edgeStyle = edgeTypeToGraphviz( edge.getType() )
                .map( style -> style + ", fontsize=" + configuration.fontsize )
                .orElse( "" );
            return GraphvizDocument.withEdge( new GraphvizDocument.Statement(
                String.format( "%s -> %s [%s]", edge.getFrom().getId().getId(), edge.getTo().getId()
                    .getId(), edgeStyle ) ) );
        };

        graphVisitor = new GraphVisitor<>( nodeTypeToGraphviz, plainEdgeToGraphviz, decoratedEdgeToGraphviz );
    }

    private static String escape( final String value ) {
        return StringEscapeUtils.escapeHtml4( value );
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
            CLASS( "#CFA500", "⬤", 12 ),
            DATA_PROPERTY( "#38A14A", "▬", 16 ),
            OBJECT_PROPERTY( "#0079BA", "▬", 16 ),
            ANNOTATION_PROPERTY( "#D17A00", "▬", 16 ),
            INDIVIDUAL( "#874B82", "◆", 14 ),
            DATA_TYPE( "#AD3B45", "⬤", 12 );

            String color;
            String symbol;
            int symbolSize;

            Symbol( final String color, final String symbol, final int symbolSize ) {
                this.color = color;
                this.symbol = symbol;
                this.symbolSize = symbolSize;
            }

            /**
             * Returns a DOT fragement for an element with this symbol. Note that the referenced font 'owlcli'
             * is injected during rendering using the {@link FontEmbedder}.
             *
             * @param elementName   the name of the element
             * @param configuration the diagram generation configuration
             * @return the dot fragment for this element
             */
            String getNodeValue( final String elementName, final Configuration configuration ) {
                return String.format( "<FONT POINT-SIZE=\"%d\" COLOR=\"%s\" FACE=\"owlcli\"><B>%s</B></FONT> " +
                        "<FONT POINT-SIZE=\"%s\" COLOR=\"#000000\" FACE=\"%s\">%s</FONT>",
                    symbolSize, color, symbol, configuration.fontsize, configuration.fontname, elementName );
            }
        }

        final Template literalNodeTemplate = new Template( """
            ${nodeId} [label="${value}"] """ );
        final Template htmlLabelNodeTemplate = new Template( """
            ${nodeId} [label=<${value}>] """ );
        final Template invisibleNodeTemplate = new Template( """
            ${nodeId} [label="", width="0", style="invis"] """ );
        Configuration configuration;

        GraphvizNodeVisitor( final Configuration configuration ) {
            this.configuration = configuration;
        }

        @Override
        public GraphvizDocument visit( final Class class_ ) {
            return generateHtmlLabelNode( class_.getId(), Symbol.CLASS
                .getNodeValue( class_.getName(), configuration ) );
        }

        @Override
        public GraphvizDocument visit( final DataProperty dataProperty ) {
            return generateHtmlLabelNode( dataProperty.getId(), Symbol.DATA_PROPERTY
                .getNodeValue( dataProperty.getName(), configuration ) );
        }

        @Override
        public GraphvizDocument visit( final ObjectProperty objectProperty ) {
            return generateHtmlLabelNode( objectProperty.getId(), Symbol.OBJECT_PROPERTY
                .getNodeValue( objectProperty.getName(), configuration ) );
        }

        @Override
        public GraphvizDocument visit( final AnnotationProperty annotationProperty ) {
            return generateHtmlLabelNode( annotationProperty.getId(), Symbol.ANNOTATION_PROPERTY
                .getNodeValue( annotationProperty.getName(), configuration ) );
        }

        @Override
        public GraphvizDocument visit( final Individual individual ) {
            return generateHtmlLabelNode( individual.getId(), Symbol.INDIVIDUAL
                .getNodeValue( individual.getName(), configuration ) );
        }

        @Override
        public GraphvizDocument visit( final Literal literal ) {
            return generateLiteralNode( literal.getId(), literal.getValue() );
        }

        @Override
        public GraphvizDocument visit( final PropertyChain propertyChain ) {
            final String operator = String
                .format( " <FONT COLOR=\"#0A5EA8\"><B>%s</B></FONT> ", PropertyChain.OPERATOR_SYMBOL );
            final String[] parts = propertyChain.getValue().split( " " + PropertyChain.OPERATOR_SYMBOL + " " );
            final String label = Arrays.stream( parts )
                .map( part -> String.format( "<FONT COLOR=\"#000000\">%s</FONT>", part ) )
                .collect( Collectors.joining( operator ) );
            return generateHtmlLabelNode( propertyChain.getId(), label );
        }

        @Override
        public GraphvizDocument visit( final Datatype datatype ) {
            return generateHtmlLabelNode( datatype.getId(), Symbol.DATA_TYPE
                .getNodeValue( escape( datatype.getName() ), configuration ) );
        }

        @Override
        public GraphvizDocument visit( final ExistentialRestriction existentialRestriction ) {
            return generateHtmlLabelNode( existentialRestriction.getId(),
                "P <FONT COLOR=\"#B200B2\"><B>some</B></FONT> C" );
        }

        @Override
        public GraphvizDocument visit( final ValueRestriction valueRestriction ) {
            return generateHtmlLabelNode( valueRestriction.getId(),
                "P <FONT COLOR=\"#B200B2\"><B>value</B></FONT> v" );
        }

        @Override
        public GraphvizDocument visit( final UniversalRestriction universalRestriction ) {
            return generateHtmlLabelNode( universalRestriction.getId(),
                "P <FONT COLOR=\"#B200B2\"><B>only</B></FONT>  C" );
        }

        @Override
        public GraphvizDocument visit( final Intersection intersection ) {
            return generateHtmlLabelNode( intersection.getId(), "<FONT COLOR=\"#00B2B2\"><B>and</B></FONT>" );
        }

        @Override
        public GraphvizDocument visit( final Union union ) {
            return generateHtmlLabelNode( union.getId(), "<FONT COLOR=\"#00B2B2\"><B>or</B></FONT>" );
        }

        @Override
        public GraphvizDocument visit( final Disjointness disjointness ) {
            final String label = """
                <table border="0">
                   <tr>
                     <td border="0" fixedsize="true" width="30" height="24" align="center"><FONT POINT-SIZE="26" face="owlcli">  ⚬⚬</FONT></td>
                   </tr>
                 </table>""";
            return generateHtmlLabelNode( disjointness.getId(), label );
        }

        @Override
        public GraphvizDocument visit( final DisjointUnion disjointUnion ) {
            final String label = """
                <table border="0">
                   <tr>
                     <td border="0" fixedsize="true" width="24" height="24" align="center"><FONT POINT-SIZE="30" face="owlcli">⚭</FONT></td>
                   </tr>
                 </table>""";
            return generateHtmlLabelNode( disjointUnion.getId(), " " + label );
        }

        @Override
        public GraphvizDocument visit( final Equality equality ) {
            return generateHtmlLabelNode( equality.getId(), "<FONT POINT-SIZE=\"16\">=</FONT>" );
        }

        @Override
        public GraphvizDocument visit( final Inverse inverse ) {
            return generateHtmlLabelNode( inverse.getId(), "<FONT COLOR=\"#B200B2\"><B>inverse</B></FONT>" );
        }

        @Override
        public GraphvizDocument visit( final Inequality inequality ) {
            return generateHtmlLabelNode( inequality.getId(), "<FONT POINT-SIZE=\"16\">≠</FONT>" );
        }

        @Override
        public GraphvizDocument visit( final ClosedClass closedClass ) {
            return generateHtmlLabelNode( closedClass.getId(), "<FONT POINT-SIZE=\"16\">{}</FONT>" );
        }

        @Override
        public GraphvizDocument visit( final Complement complement ) {
            return generateHtmlLabelNode( complement.getId(), "<FONT COLOR=\"#00B2B2\"><B>not</B></FONT>" );
        }

        @Override
        public GraphvizDocument visit( final Self self ) {
            return generateHtmlLabelNode( self.getId(), "P <FONT COLOR=\"#B200B2\"><B>self</B></FONT>" );
        }

        @Override
        public GraphvizDocument visit( final ObjectMinimalCardinality objectMinimalCardinality ) {
            return generateHtmlLabelNode( objectMinimalCardinality.getId(),
                String.format( "P <FONT COLOR=\"#B200B2\"><B>min</B></FONT>  %d",
                    objectMinimalCardinality.getCardinality() ) );
        }

        @Override
        public GraphvizDocument visit( final ObjectQualifiedMinimalCardinality objectQualifiedMinimalCardinality ) {
            return generateHtmlLabelNode( objectQualifiedMinimalCardinality.getId(),
                String.format( "P <FONT COLOR=\"#B200B2\"><B>min</B></FONT>  %d C",
                    objectQualifiedMinimalCardinality.getCardinality() ) );
        }

        @Override
        public GraphvizDocument visit( final ObjectMaximalCardinality objectMaximalCardinality ) {
            return generateHtmlLabelNode( objectMaximalCardinality.getId(),
                String.format( "P <FONT COLOR=\"#B200B2\"><B>max</B></FONT>  %d",
                    objectMaximalCardinality.getCardinality() ) );
        }

        @Override
        public GraphvizDocument visit( final ObjectQualifiedMaximalCardinality objectQualifiedMaximalCardinality ) {
            return generateHtmlLabelNode( objectQualifiedMaximalCardinality.getId(),
                String.format( "P <FONT COLOR=\"#B200B2\"><B>max</B></FONT>  %d C",
                    objectQualifiedMaximalCardinality.getCardinality() ) );
        }

        @Override
        public GraphvizDocument visit( final ObjectExactCardinality objectExactCardinality ) {
            return generateHtmlLabelNode( objectExactCardinality.getId(),
                String.format( "P <FONT COLOR=\"#B200B2\"><B>exactly</B></FONT>  %d",
                    objectExactCardinality.getCardinality() ) );
        }

        @Override
        public GraphvizDocument visit( final ObjectQualifiedExactCardinality objectQualifiedExactCardinality ) {
            return generateHtmlLabelNode( objectQualifiedExactCardinality.getId(),
                String.format( "P <FONT COLOR=\"#B200B2\"><B>exactly</B></FONT>  %d C",
                    objectQualifiedExactCardinality.getCardinality() ) );
        }

        @Override
        public GraphvizDocument visit( final DataMinimalCardinality dataMinimalCardinality ) {
            return generateHtmlLabelNode( dataMinimalCardinality.getId(),
                String.format( "P <FONT COLOR=\"#B200B2\"><B>min</B></FONT>  %d",
                    dataMinimalCardinality.getCardinality() ) );
        }

        @Override
        public GraphvizDocument visit( final DataMaximalCardinality dataMaximalCardinality ) {
            return generateHtmlLabelNode( dataMaximalCardinality.getId(),
                String.format( "P <FONT COLOR=\"#B200B2\"><B>max</B></FONT>  %d",
                    dataMaximalCardinality.getCardinality() ) );
        }

        @Override
        public GraphvizDocument visit( final DataExactCardinality dataExactCardinality ) {
            return generateHtmlLabelNode( dataExactCardinality.getId(),
                String.format( "P <FONT COLOR=\"#B200B2\"><B>exactly</B></FONT>  %d",
                    dataExactCardinality.getCardinality() ) );
        }

        @Override
        public GraphvizDocument visit( final Invisible invisible ) {
            return generateInvisibleNode( invisible.getId() );
        }

        @Override
        public GraphvizDocument visit( final IRIReference iriReference ) {
            return generateLiteralNode( iriReference.getId(), iriReference.getIri().toString() );
        }

        @Override
        public GraphvizDocument visit( final PropertyMarker propertyMarker ) {
            final Ordering<PropertyMarker.Kind> markerOrder = Ordering.explicit( List.of(
                PropertyMarker.Kind.FUNCTIONAL,
                PropertyMarker.Kind.INVERSE_FUNCTIONAL,
                PropertyMarker.Kind.TRANSITIVE,
                PropertyMarker.Kind.SYMMETRIC,
                PropertyMarker.Kind.ASYMMETRIC,
                PropertyMarker.Kind.REFLEXIVE,
                PropertyMarker.Kind.IRREFLEXIVE ) );

            final String value = propertyMarker.getKind().stream()
                .sorted( markerOrder )
                .map( kind -> kind.toString().toLowerCase().replace( "_", " " ) )
                .collect( Collectors.joining( "\\n" ) );
            return generateLiteralNode( propertyMarker.getId(), value );
        }

        @Override
        public GraphvizDocument visit( final Key key ) {
            return generateHtmlLabelNode( key.getId(), "<FONT COLOR=\"#B200B2\"><B>key</B></FONT>" );
        }

        @Override
        public GraphvizDocument visit( final Rule rule ) {
            final String operator = String
                .format( " <FONT COLOR=\"#B2B2B2\"><B>%s</B></FONT> ", Rule.IMPLICATION_SYMBOL );
            final String[] parts = rule.getValue().split( " " + Rule.IMPLICATION_SYMBOL + " " );
            final String label = Arrays.stream( parts )
                .map( GraphvizGenerator::escape )
                .map( part -> String.format( "<FONT COLOR=\"#000000\">%s</FONT>", part ) )
                .collect( Collectors.joining( operator ) );
            return generateHtmlLabelNode( rule.getId(), label );
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
