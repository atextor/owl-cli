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

public class GraphvizGenerator implements Function<Stream<GraphElement>, GraphvizDocument> {
    private final GraphVisitor<GraphvizDocument> graphVisitor;

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
            default:
                yield Optional.empty();
        };
    }

    @Override
    public GraphvizDocument apply( final Stream<GraphElement> graphElements ) {
        return graphElements
            .map( graphElement -> graphElement.accept( graphVisitor ) )
            .reduce( GraphvizDocument.BLANK, GraphvizDocument::merge );
    }

    static class GraphvizNodeVisitor implements Node.Visitor<GraphvizDocument> {
        final Template namedNodeTemplate = new Template( """
            ${nodeId} [label=<
              <table border="0">
                <tr>
                  <td border="0" fixedsize="true" width="24" height="24"><img src="${directory}/${resource}" /></td>
                  <td>${nodeName}</td>
                </tr>
              </table> >]""" );
        final Template anonymousNodeTemplate = new Template( """
            ${nodeId} [label=<
              <table border="0">
                <tr>
                  <td border="0" fixedsize="true" width="24" height="24"><img src="${directory}/${resource}" scale="true" /></td>
                </tr>
              </table> >]""" );
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
            return generateNamedNode( class_, Resource.OWL_CLASS );
        }

        @Override
        public GraphvizDocument visit( final Node.DataProperty dataProperty ) {
            return generateNamedNode( dataProperty, Resource.OWL_DATA_PROPERTY );
        }

        @Override
        public GraphvizDocument visit( final Node.ObjectProperty objectProperty ) {
            return generateNamedNode( objectProperty, Resource.OWL_OBJECT_PROPERTY );
        }

        @Override
        public GraphvizDocument visit( final Node.AnnotationProperty annotationProperty ) {
            return generateNamedNode( annotationProperty, Resource.OWL_ANNOTATION_PROPERTY );
        }

        @Override
        public GraphvizDocument visit( final Node.Individual individual ) {
            return generateNamedNode( individual, Resource.OWL_INDIVIDUAL );
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
            return generateNamedNode( datatype, Resource.OWL_DATATYPE );
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
                "P <FONT COLOR=\"#B200B2\"><B>only</B></FONT> C" );
        }

        @Override
        public GraphvizDocument visit( final Node.Intersection intersection ) {
            return generateAnonymousNode( intersection.getId(), Resource.OWL_INTERSECTION );
        }

        @Override
        public GraphvizDocument visit( final Node.Union union ) {
            return generateAnonymousNode( union.getId(), Resource.OWL_UNION );
        }

        @Override
        public GraphvizDocument visit( final Node.Disjointness disjointness ) {
            return generateAnonymousNode( disjointness.getId(), Resource.OWL_DISJOINTNESS );
        }

        @Override
        public GraphvizDocument visit( final Node.DisjointUnion disjointUnion ) {
            return generateAnonymousNode( disjointUnion.getId(), Resource.OWL_DISJOINT_UNION );
        }

        @Override
        public GraphvizDocument visit( final Node.Equality equality ) {
            return generateHtmlLabelNode( equality.getId(), "<B>=</B>" );
        }

        @Override
        public GraphvizDocument visit( final Node.Inverse inverse ) {
            return generateAnonymousNode( inverse.getId(), Resource.OWL_INVERSE );
        }

        @Override
        public GraphvizDocument visit( final Node.Inequality inequality ) {
            return generateHtmlLabelNode( inequality.getId(), "<B>≠</B>" );
        }

        @Override
        public GraphvizDocument visit( final Node.ClosedClass closedClass ) {
            return generateAnonymousNode( closedClass.getId(), Resource.OWL_CLOSEDCLASS );
        }

        @Override
        public GraphvizDocument visit( final Node.Complement complement ) {
            return generateHtmlLabelNode( complement.getId(), "<FONT COLOR=\"#00B2B2\"><B>not</B></FONT>" );
        }

        @Override
        public GraphvizDocument visit( final Node.Self self ) {
            return generateAnonymousNode( self.getId(), Resource.OWL_SELF );
        }

        @Override
        public GraphvizDocument visit( final Node.ObjectMinimalCardinality objectMinimalCardinality ) {
            return generateHtmlLabelNode( objectMinimalCardinality.getId(),
                String.format( "P <FONT COLOR=\"#B200B2\"><B>min</B></FONT> %d",
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

        private GraphvizDocument generateNamedNode( final Node.NamedNode node, final Resource symbol ) {
            return GraphvizDocument.withNode( new GraphvizDocument.Statement( namedNodeTemplate.apply(
                Map.of( "nodeId", node.getId().getId(),
                    "directory", resourceDirectoryname,
                    "resource", symbol.getResourceName( format ),
                    "nodeName", node.getName() ) ) ) );
        }

        private GraphvizDocument generateAnonymousNode( final Node.Id nodeId, final Resource symbol ) {
            return GraphvizDocument.withNode( new GraphvizDocument.Statement( anonymousNodeTemplate.apply(
                Map.of( "nodeId", nodeId.getId(),
                    "directory", resourceDirectoryname,
                    "resource", symbol.getResourceName( format ) ) ) ) );
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
