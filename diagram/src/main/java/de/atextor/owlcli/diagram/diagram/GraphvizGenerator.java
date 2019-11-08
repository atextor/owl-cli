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
import java.util.stream.Collectors;
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
                edge.getFrom().getId() + " -> " + edge.getTo().getId()
                    + " [" + decoration + ", " + edgeStyle + "]" ) );
        };

        final Function<PlainEdge, GraphvizDocument> plainEdgeToGraphviz = edge -> {
            final String edgeStyle = edgeTypeToGraphviz( edge.getType() );
            return GraphvizDocument.withEdge( new GraphvizDocument.Statement(
                edge.getFrom().getId() + " -> " + edge.getTo().getId() + " [" + edgeStyle + "]" ) );
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
    public GraphvizDocument apply( final Stream<GraphElement> graph ) {
        return graph.collect( Collectors.toSet() )
            .stream()
            .map( graphElement -> graphElement.accept( graphVisitor ) )
            .reduce( GraphvizDocument.BLANK, GraphvizDocument::merge );
    }

    class GraphvizDecorationVisitor implements Decoration.Visitor<String> {
        Configuration.Format format;
        String resourceDirectoryName;

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
        public String visit( final Decoration.AbstractRoleSymbol abstractRoleSymbol ) {
            return generateImageLabel( Resource.EDGE_R );
        }

        @Override
        public String visit( final Decoration.ConcreteRoleSymbol concreteRoleSymbol ) {
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
            return "label=<\n" +
                "     <table border=\"0\">\n" +
                "       <tr>\n" +
                "         <td border=\"0\" fixedsize=\"true\" width=\"24\" height=\"24\"><img src=\"" +
                resourceDirectoryName + "/" + image.getResourceName( format ) + "\" /></td>\n" +
                "       </tr>\n" +
                "     </table> >";
        }
    }

    class GraphvizNodeTypeVisitor implements NodeType.Visitor<GraphvizDocument> {
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
        public GraphvizDocument visit( final NodeType.AbstractRole abstractRole ) {
            return generateNamedNode( abstractRole, Resource.OWL_OBJECT_PROPERTY );
        }

        @Override
        public GraphvizDocument visit( final NodeType.ConcreteRole concreteRole ) {
            return generateNamedNode( concreteRole, Resource.OWL_DATA_PROPERTY );
        }

        @Override
        public GraphvizDocument visit( final NodeType.AnnotationRole annotationRole ) {
            return generateNamedNode( annotationRole, Resource.OWL_ANNOTATION_PROPERTY );
        }

        @Override
        public GraphvizDocument visit( final NodeType.Individual individual ) {
            return generateNamedNode( individual, Resource.OWL_INDIVIDUAL );
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
        public GraphvizDocument visit( final NodeType.ClosedClass closedClass ) {
            return generateAnonymousNode( closedClass.getId(), Resource.OWL_CLOSEDCLASS );
        }

        @Override
        public GraphvizDocument visit( final NodeType.Domain domain ) {
            return generateAnonymousNode( domain.getId(), Resource.OWL_DOMAIN );
        }

        @Override
        public GraphvizDocument visit( final NodeType.Range range ) {
            return generateAnonymousNode( range.getId(), Resource.OWL_ALLVALUES );
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
        public GraphvizDocument visit( final NodeType.AbstractMinimalCardinality abstractMinimalCardinality ) {
            return generateCardinalityNode( abstractMinimalCardinality, Resource.GET, Resource.R );
        }

        @Override
        public GraphvizDocument visit( final NodeType.AbstractQualifiedMinimalCardinality abstractQualifiedMinimalCardinality ) {
            return generateCardinalityNode( abstractQualifiedMinimalCardinality, Resource.GET, Resource.R_C );
        }

        @Override
        public GraphvizDocument visit( final NodeType.AbstractMaximalCardinality abstractMaximalCardinality ) {
            return generateCardinalityNode( abstractMaximalCardinality, Resource.LET, Resource.R );
        }

        @Override
        public GraphvizDocument visit( final NodeType.AbstractQualifiedMaximalCardinality abstractQualifiedMaximalCardinality ) {
            return generateCardinalityNode( abstractQualifiedMaximalCardinality, Resource.LET, Resource.R_C );
        }

        @Override
        public GraphvizDocument visit( final NodeType.AbstractExactCardinality abstractExactCardinality ) {
            return generateCardinalityNode( abstractExactCardinality, Resource.EQ, Resource.R );
        }

        @Override
        public GraphvizDocument visit( final NodeType.AbstractQualifiedExactCardinality abstractQualifiedExactCardinality ) {
            return generateCardinalityNode( abstractQualifiedExactCardinality, Resource.EQ, Resource.R_C );
        }

        @Override
        public GraphvizDocument visit( final NodeType.ConcreteMinimalCardinality concreteMinimalCardinality ) {
            return generateCardinalityNode( concreteMinimalCardinality, Resource.GET, Resource.U );
        }

        @Override
        public GraphvizDocument visit( final NodeType.ConcreteMaximalCardinality concreteMaximalCardinality ) {
            return generateCardinalityNode( concreteMaximalCardinality, Resource.LET, Resource.U );
        }

        @Override
        public GraphvizDocument visit( final NodeType.ConcreteExactCardinality concreteExactCardinality ) {
            return generateCardinalityNode( concreteExactCardinality, Resource.EQ, Resource.U );
        }

        @Override
        public GraphvizDocument visit( final NodeType.Invisible invisible ) {
            return generateInvisibleNode( invisible.getId() );
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
    }
}
