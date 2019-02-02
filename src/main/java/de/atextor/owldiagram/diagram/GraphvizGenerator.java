package de.atextor.owldiagram.diagram;

import de.atextor.owldiagram.graph.DecoratedEdge;
import de.atextor.owldiagram.graph.Decoration;
import de.atextor.owldiagram.graph.GraphElement;
import de.atextor.owldiagram.graph.GraphVisitor;
import de.atextor.owldiagram.graph.Node;
import de.atextor.owldiagram.graph.NodeType;
import de.atextor.owldiagram.graph.PlainEdge;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GraphvizGenerator implements Function<Stream<GraphElement>, GraphvizDocument> {
    private final Decoration.Visitor<String> decorationToGraphvizFragment = new Decoration.Visitor<String>() {
        @Override
        public String visit( final Decoration.Label label ) {
            return "label=\"" + label.getText() + "\"";
        }

        @Override
        public String visit( final Decoration.ClassSymbol classSymbol ) {
            return generateImageLabel( "edge-c" );
        }

        @Override
        public String visit( final Decoration.AbstractRoleSymbol abstractRoleSymbol ) {
            return generateImageLabel( "edge-r" );
        }

        @Override
        public String visit( final Decoration.ConcreteRoleSymbol concreteRoleSymbol ) {
            return generateImageLabel( "edge-u" );
        }

        @Override
        public String visit( final Decoration.DataRangeSymbol dataRangeSymbol ) {
            return generateImageLabel( "edge-range" );
        }

        @Override
        public String visit( final Decoration.IndividualSymbol individualSymbol ) {
            return generateImageLabel( "edge-o" );
        }

        @Override
        public String visit( final Decoration.LiteralSymbol literalSymbol ) {
            return generateImageLabel( "literal" );
        }

        private String generateImageLabel( final String imageName ) {
            return "label=<\n" +
                    "     <table border=\"0\">\n" +
                    "       <tr>\n" +
                    "         <td border=\"0\" fixedsize=\"true\" width=\"16\" height=\"16\"><img src=\"" +
                    imageName + ".svg\" /></td>\n" +
                    "       </tr>\n" +
                    "     </table> >";
        }
    };

    private final NodeType.Visitor<GraphvizDocument> nodeTypeToGraphviz = new NodeType.Visitor<GraphvizDocument>() {
        @Override
        public GraphvizDocument visit( final NodeType.Class class_ ) {
            return generateNamedNode( class_, "owl-class" );
        }

        @Override
        public GraphvizDocument visit( final NodeType.AbstractRole abstractRole ) {
            return generateNamedNode( abstractRole, "owl-object-property" );
        }

        @Override
        public GraphvizDocument visit( final NodeType.ConcreteRole concreteRole ) {
            return generateNamedNode( concreteRole, "owl-data-property" );
        }

        @Override
        public GraphvizDocument visit( final NodeType.AnnotationRole annotationRole ) {
            return generateNamedNode( annotationRole, "owl-annotation-property" );
        }

        @Override
        public GraphvizDocument visit( final NodeType.Individual individual ) {
            return generateNamedNode( individual, "owl-individual" );
        }

        @Override
        public GraphvizDocument visit( final NodeType.Datatype datatype ) {
            return generateNamedNode( datatype, "owl-datatype" );
        }

        @Override
        public GraphvizDocument visit( final NodeType.ExistentialRestriction existentialRestriction ) {
            return generateAnonymousNode( existentialRestriction.getId(), "owl-somevalues" );
        }

        @Override
        public GraphvizDocument visit( final NodeType.ValueRestriction valueRestriction ) {
            return generateAnonymousNode( valueRestriction.getId(), "owl-hasvalue" );
        }

        @Override
        public GraphvizDocument visit( final NodeType.UniversalRestriction universalRestriction ) {
            return generateAnonymousNode( universalRestriction.getId(), "owl-allvalues" );
        }

        @Override
        public GraphvizDocument visit( final NodeType.Intersection intersection ) {
            return generateAnonymousNode( intersection.getId(), "owl-intersection" );
        }

        @Override
        public GraphvizDocument visit( final NodeType.Union union ) {
            return generateAnonymousNode( union.getId(), "owl-union" );
        }

        @Override
        public GraphvizDocument visit( final NodeType.ClosedClass closedClass ) {
            return generateAnonymousNode( closedClass.getId(), "owl-closedclass" );
        }

        @Override
        public GraphvizDocument visit( final NodeType.Domain domain ) {
            return generateAnonymousNode( domain.getId(), "owl-domain" );
        }

        @Override
        public GraphvizDocument visit( final NodeType.Range range ) {
            return generateAnonymousNode( range.getId(), "owl-range" );
        }

        @Override
        public GraphvizDocument visit( final NodeType.Complement complement ) {
            return generateAnonymousNode( complement.getId(), "owl-complement" );
        }

        @Override
        public GraphvizDocument visit( final NodeType.Self self ) {
            return generateAnonymousNode( self.getId(), "owl-self" );
        }

        @Override
        public GraphvizDocument visit( final NodeType.AbstractMinimalCardinality abstractMinimalCardinality ) {
            return generateCardinalityNode( abstractMinimalCardinality, "get", "r" );
        }

        @Override
        public GraphvizDocument visit( final NodeType.AbstractQualifiedMinimalCardinality abstractQualifiedMinimalCardinality ) {
            return generateCardinalityNode( abstractQualifiedMinimalCardinality, "get", "r-c" );
        }

        @Override
        public GraphvizDocument visit( final NodeType.AbstractMaximalCardinality abstractMaximalCardinality ) {
            return generateCardinalityNode( abstractMaximalCardinality, "let", "r" );
        }

        @Override
        public GraphvizDocument visit( final NodeType.AbstractQualifiedMaximalCardinality abstractQualifiedMaximalCardinality ) {
            return generateCardinalityNode( abstractQualifiedMaximalCardinality, "let", "r-c" );
        }

        @Override
        public GraphvizDocument visit( final NodeType.AbstractExactCardinality abstractExactCardinality ) {
            return generateCardinalityNode( abstractExactCardinality, "eq", "r" );
        }

        @Override
        public GraphvizDocument visit( final NodeType.AbstractQualifiedExactCardinality abstractQualifiedExactCardinality ) {
            return generateCardinalityNode( abstractQualifiedExactCardinality, "eq", "r-c" );
        }

        @Override
        public GraphvizDocument visit( final NodeType.ConcreteMinimalCardinality concreteMinimalCardinality ) {
            return generateCardinalityNode( concreteMinimalCardinality, "get", "u" );
        }

        @Override
        public GraphvizDocument visit( final NodeType.ConcreteMaximalCardinality concreteMaximalCardinality ) {
            return generateCardinalityNode( concreteMaximalCardinality, "let", "u" );
        }

        @Override
        public GraphvizDocument visit( final NodeType.ConcreteExactCardinality concreteExactCardinality ) {
            return generateCardinalityNode( concreteExactCardinality, "eq", "u" );
        }

        private GraphvizDocument generateNamedNode( final NodeType.NamedNode node, final String symbolName ) {
            return GraphvizDocument.withNode( new GraphvizDocument.Statement(
                    "  " + node.getId().getId() + " [label=<\n" +
                            "     <table border=\"0\">\n" +
                            "       <tr>\n" +
                            "         <td border=\"0\" fixedsize=\"true\" width=\"16\" height=\"16\"><img " +
                            "src=\"" + symbolName + ".svg\" /></td><td>" + node.getName() + "</td>\n" +
                            "       </tr>\n" +
                            "     </table> >]" ) );
        }

        private GraphvizDocument generateAnonymousNode( final Node.Id nodeId, final String symbolName ) {
            return GraphvizDocument.withNode( new GraphvizDocument.Statement(
                    "  " + nodeId.getId() + " [label=<\n" +
                            "     <table border=\"0\">\n" +
                            "       <tr>\n" +
                            "         <td border=\"0\" fixedsize=\"true\" width=\"16\" height=\"16\"><img " +
                            "src=\"" + symbolName + ".svg\" /></td>\n" +
                            "       </tr>\n" +
                            "     </table> >]" ) );
        }

        private GraphvizDocument generateCardinalityNode( final NodeType.CardinalityNode node,
                                                          final String symbolPrefix,
                                                          final String symbolPostfix ) {
            return GraphvizDocument.withNode( new GraphvizDocument.Statement(
                    "  " + node.getId().getId() + " [label=<\n" +
                            "     <table border=\"0\">\n" +
                            "       <tr>\n" +
                            "         <td border=\"0\" fixedsize=\"true\" width=\"16\" height=\"16\"><img " +
                            "src=\"" + symbolPrefix + ".svg\" /><td>" + node.getCardinality() + "</td><td><img " +
                            "src=\"" + symbolPostfix + ".svg\" /></td>\n" +
                            "       </tr>\n" +
                            "     </table> >]" ) );
        }
    };

    private final Function<PlainEdge, GraphvizDocument> plainEdgeToGraphviz = edge ->
            GraphvizDocument.withEdge( new GraphvizDocument.Statement(
                    edge.getFrom().getId() + " -> " + edge.getTo().getId() ) );

    private final Function<DecoratedEdge, GraphvizDocument> decoratedEdgeToGraphviz = edge -> {
        final String decoration = edge.getDecoration().accept( decorationToGraphvizFragment );
        return GraphvizDocument.withEdge( new GraphvizDocument.Statement(
                edge.getFrom().getId() + " -> " + edge.getTo().getId() + " [" + decoration + "]" ) );
    };

    private final GraphVisitor<GraphvizDocument> graphVisitor = new GraphVisitor<>(
            nodeTypeToGraphviz, plainEdgeToGraphviz, decoratedEdgeToGraphviz );

    @Override
    public GraphvizDocument apply( final Stream<GraphElement> graph ) {
        return graph.collect( Collectors.toSet() )
                .stream()
                .map( graphElement -> graphElement.accept( graphVisitor ) )
                .reduce( GraphvizDocument.BLANK, GraphvizDocument::merge );
    }
}
