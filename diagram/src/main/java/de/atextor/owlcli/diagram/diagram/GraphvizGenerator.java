package de.atextor.owlcli.diagram.diagram;

import de.atextor.owlcli.diagram.graph.DecoratedEdge;
import de.atextor.owlcli.diagram.graph.Decoration;
import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.GraphVisitor;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.NodeType;
import de.atextor.owlcli.diagram.graph.PlainEdge;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class GraphvizGenerator implements Function<Stream<GraphElement>, GraphvizDocument> {
    private final Decoration.Visitor<String> decorationToGraphvizFragment;
    private final GraphVisitor<GraphvizDocument> graphVisitor;

    GraphvizGenerator( final Configuration configuration ) {
        decorationToGraphvizFragment = new GraphvizDecorationVisitor( configuration.format,
            configuration.resourceDirectoryName );
        final NodeType.Visitor<GraphvizDocument> nodeTypeToGraphviz =
            new GraphvizNodeTypeVisitor( configuration.format, configuration.resourceDirectoryName );

        final Function<DecoratedEdge, GraphvizDocument> decoratedEdgeToGraphviz = edge -> {
            final String decoration = edge.getDecoration().accept( decorationToGraphvizFragment );
            final String edgeStyle = edgeTypeToGraphviz( edge.getType() );
            return GraphvizDocument.withEdge( new GraphvizDocument.Statement(
                String.format( "%s -> %s [%s, %s]", edge.getFrom().getId(), edge.getTo().getId(), decoration,
                    edgeStyle ) ) );
        };

        final Function<PlainEdge, GraphvizDocument> plainEdgeToGraphviz = edge -> {
            final String edgeStyle = edgeTypeToGraphviz( edge.getType() );
            return GraphvizDocument.withEdge( new GraphvizDocument.Statement(
                String.format( "%s -> %s [%s]", edge.getFrom().getId(), edge.getTo().getId(), edgeStyle ) ) );
        };

        graphVisitor = new GraphVisitor<>( nodeTypeToGraphviz, plainEdgeToGraphviz, decoratedEdgeToGraphviz );
    }

    private String edgeTypeToGraphviz( final Edge.Type type ) {
        return switch ( type ) {
            case DEFAULT_ARROW:
                yield "arrowhead = normal";
            case DASHED_ARROW:
                yield "arrowhead = normal, style = dashed";
            case HOLLOW_ARROW:
                yield "arrowhead = empty";
            case DOUBLE_ENDED_HOLLOW_ARROW:
                yield "dir = both, arrowhead = empty, arrowtail = empty";
            case NO_ARROW:
                yield "arrowhead = none";
            default:
                yield "";
        };
    }

    @Override
    public GraphvizDocument apply( final Stream<GraphElement> graphElements ) {
        return graphElements
            .map( graphElement -> graphElement.accept( graphVisitor ) )
            .reduce( GraphvizDocument.BLANK, GraphvizDocument::merge );
    }

    static class GraphvizDecorationVisitor implements Decoration.Visitor<String> {
        Configuration.Format format;
        String resourceDirectoryName;

        private final Template imageLabelTemplate = new Template( """
            label=<
              <table border="0">
                <tr>
                  <td border="0" fixedsize="true" width="24" height="24">
                    <img src="${resourceDirectoryName}/${imageName}" />
                  </td>
                </tr>
              </table> >
            """ );

        GraphvizDecorationVisitor( final Configuration.Format format, final String resourceDirectoryName ) {
            this.format = format;
            this.resourceDirectoryName = resourceDirectoryName;
        }

        @Override
        public String visit( final Decoration.Label label ) {
            return "label=\"" + label.getText() + "\"";
        }

        @Override
        public String visit( final Decoration.ClassSymbol classSymbol ) {
            return generateImageLabel( Resource.EDGE_C );
        }

        @Override
        public String visit( final Decoration.ObjectSymbol objectSymbol ) {
            return generateImageLabel( Resource.EDGE_R );
        }

        @Override
        public String visit( final Decoration.DataSymbol dataSymbol ) {
            return generateImageLabel( Resource.EDGE_U );
        }

        @Override
        public String visit( final Decoration.DataRangeSymbol dataRangeSymbol ) {
            return generateImageLabel( Resource.EDGE_R );
        }

        @Override
        public String visit( final Decoration.IndividualSymbol individualSymbol ) {
            return generateImageLabel( Resource.EDGE_O );
        }

        @Override
        public String visit( final Decoration.LiteralSymbol literalSymbol ) {
            return generateImageLabel( Resource.LITERAL );
        }

        private String generateImageLabel( final Resource image ) {
            return imageLabelTemplate.apply( Map.of(
                "resourceDirectoryName", resourceDirectoryName,
                "imageName", image.getResourceName( format )
            ) );
        }
    }

    static class GraphvizNodeTypeVisitor implements NodeType.Visitor<GraphvizDocument> {
        Configuration.Format format;
        String resourceDirectoryname;

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

        final Template cardinalityNodeTemplate = new Template( """
            ${nodeId} [label=<
              <table border="0">
                <tr>
                  <td border="0" fixedsize="true" width="16" height="16"><img src="${directory}/${prefixResource}" /></td>
                  <td>${cardinality}</td>
                  <td><img src="${directory}/${postfixResource}" /></td>
                </tr>
              </table> >]""" );

        final Template literalNodeTemplate = new Template( """
            ${nodeId} [label="${value}"] """ );

        final Template invisibleNodeTemplate = new Template( """
            ${nodeId} [label="", width="0", style="invis"] """ );

        GraphvizNodeTypeVisitor( final Configuration.Format format, final String resourceDirectoryname ) {
            this.format = format;
            this.resourceDirectoryname = resourceDirectoryname;
        }

        @Override
        public GraphvizDocument visit( final NodeType.Class class_ ) {
            return generateNamedNode( class_, Resource.OWL_CLASS );
        }

        @Override
        public GraphvizDocument visit( final NodeType.DataProperty dataProperty ) {
            return generateNamedNode( dataProperty, Resource.OWL_OBJECT_PROPERTY );
        }

        @Override
        public GraphvizDocument visit( final NodeType.ObjectProperty objectProperty ) {
            return generateNamedNode( objectProperty, Resource.OWL_DATA_PROPERTY );
        }

        @Override
        public GraphvizDocument visit( final NodeType.AnnotationProperty annotationProperty ) {
            return generateNamedNode( annotationProperty, Resource.OWL_ANNOTATION_PROPERTY );
        }

        @Override
        public GraphvizDocument visit( final NodeType.Individual individual ) {
            return generateNamedNode( individual, Resource.OWL_INDIVIDUAL );
        }

        @Override
        public GraphvizDocument visit( final NodeType.Literal literal ) {
            return generateLiteralNode( literal.getId(), literal.getValue() );
        }

        @Override
        public GraphvizDocument visit( final NodeType.Datatype datatype ) {
            return generateNamedNode( datatype, Resource.OWL_DATATYPE );
        }

        @Override
        public GraphvizDocument visit( final NodeType.ExistentialRestriction existentialRestriction ) {
            return generateAnonymousNode( existentialRestriction.getId(), Resource.OWL_SOMEVALUES );
        }

        @Override
        public GraphvizDocument visit( final NodeType.ValueRestriction valueRestriction ) {
            return generateAnonymousNode( valueRestriction.getId(), Resource.OWL_HASVALUE );
        }

        @Override
        public GraphvizDocument visit( final NodeType.UniversalRestriction universalRestriction ) {
            return generateAnonymousNode( universalRestriction.getId(), Resource.OWL_ALLVALUES );
        }

        @Override
        public GraphvizDocument visit( final NodeType.Intersection intersection ) {
            return generateAnonymousNode( intersection.getId(), Resource.OWL_INTERSECTION );
        }

        @Override
        public GraphvizDocument visit( final NodeType.Union union ) {
            return generateAnonymousNode( union.getId(), Resource.OWL_UNION );
        }

        @Override
        public GraphvizDocument visit( final NodeType.Disjointness disjointness ) {
            return generateAnonymousNode( disjointness.getId(), Resource.OWL_DISJOINTNESS );
        }

        @Override
        public GraphvizDocument visit( final NodeType.DisjointUnion disjointUnion ) {
            return generateAnonymousNode( disjointUnion.getId(), Resource.OWL_DISJOINT_UNION );
        }

        @Override
        public GraphvizDocument visit( final NodeType.Equality equality ) {
            return generateAnonymousNode( equality.getId(), Resource.EQ );
        }

        @Override
        public GraphvizDocument visit( final NodeType.Inequality inequality ) {
            return generateAnonymousNode( inequality.getId(), Resource.NEQ );
        }

        @Override
        public GraphvizDocument visit( final NodeType.ClosedClass closedClass ) {
            return generateAnonymousNode( closedClass.getId(), Resource.OWL_CLOSEDCLASS );
        }

        @Override
        public GraphvizDocument visit( final NodeType.Complement complement ) {
            return generateAnonymousNode( complement.getId(), Resource.OWL_COMPLEMENT );
        }

        @Override
        public GraphvizDocument visit( final NodeType.Self self ) {
            return generateAnonymousNode( self.getId(), Resource.OWL_SELF );
        }

        @Override
        public GraphvizDocument visit( final NodeType.ObjectMinimalCardinality objectMinimalCardinality ) {
            return generateCardinalityNode( objectMinimalCardinality, Resource.GET, Resource.R );
        }

        @Override
        public GraphvizDocument visit( final NodeType.ObjectQualifiedMinimalCardinality objectQualifiedMinimalCardinality ) {
            return generateCardinalityNode( objectQualifiedMinimalCardinality, Resource.GET, Resource.R_C );
        }

        @Override
        public GraphvizDocument visit( final NodeType.ObjectMaximalCardinality objectMaximalCardinality ) {
            return generateCardinalityNode( objectMaximalCardinality, Resource.LET, Resource.R );
        }

        @Override
        public GraphvizDocument visit( final NodeType.ObjectQualifiedMaximalCardinality objectQualifiedMaximalCardinality ) {
            return generateCardinalityNode( objectQualifiedMaximalCardinality, Resource.LET, Resource.R_C );
        }

        @Override
        public GraphvizDocument visit( final NodeType.ObjectExactCardinality objectExactCardinality ) {
            return generateCardinalityNode( objectExactCardinality, Resource.EQ, Resource.R );
        }

        @Override
        public GraphvizDocument visit( final NodeType.ObjectQualifiedExactCardinality objectQualifiedExactCardinality ) {
            return generateCardinalityNode( objectQualifiedExactCardinality, Resource.EQ, Resource.R_C );
        }

        @Override
        public GraphvizDocument visit( final NodeType.DataMinimalCardinality dataMinimalCardinality ) {
            return generateCardinalityNode( dataMinimalCardinality, Resource.GET, Resource.U );
        }

        @Override
        public GraphvizDocument visit( final NodeType.DataMaximalCardinality dataMaximalCardinality ) {
            return generateCardinalityNode( dataMaximalCardinality, Resource.LET, Resource.U );
        }

        @Override
        public GraphvizDocument visit( final NodeType.DataExactCardinality dataExactCardinality ) {
            return generateCardinalityNode( dataExactCardinality, Resource.EQ, Resource.U );
        }

        @Override
        public GraphvizDocument visit( final NodeType.Invisible invisible ) {
            return generateInvisibleNode( invisible.getId() );
        }

        @Override
        public GraphvizDocument visit( final NodeType.IRIReference iriReference ) {
            return generateLiteralNode( iriReference.getId(), iriReference.getIri().toString() );
        }

        private GraphvizDocument generateNamedNode( final NodeType.NamedNode node, final Resource symbol ) {
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

        private GraphvizDocument generateCardinalityNode( final NodeType.CardinalityNode node,
                                                          final Resource symbolPrefix,
                                                          final Resource symbolPostfix ) {
            return GraphvizDocument.withNode( new GraphvizDocument.Statement( cardinalityNodeTemplate.apply(
                Map.of( "nodeId", node.getId().getId(),
                    "directory", resourceDirectoryname,
                    "prefixResource", symbolPrefix.getResourceName( format ),
                    "postfixResource", symbolPostfix.getResourceName( format ),
                    "cardinality", node.getCardinality() ) ) ) );
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
    }
}
