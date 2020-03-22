package de.atextor.owlcli.diagram.mappers;

import com.google.common.collect.Sets;
import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.Graph;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.NodeType;
import de.atextor.owlcli.diagram.graph.PlainEdge;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNaryAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLPropertyAssertionObject;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OWLAxiomMapper implements OWLAxiomVisitorEx<Stream<GraphElement>> {
    private final MappingConfiguration mappingConfig;

    public OWLAxiomMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLSubClassOfAxiom axiom ) {
        final OWLClassExpressionVisitorEx<Graph> mapper = mappingConfig.getOwlClassExpressionMapper();

        final Graph superClassGraph = axiom.getSuperClass().accept( mapper );
        final Graph subClassGraph = axiom.getSubClass().accept( mapper );

        final Edge edge = new PlainEdge( Edge.Type.HOLLOW_ARROW, subClassGraph.getNode().getId(),
            superClassGraph.getNode().getId() );

        return superClassGraph.and( subClassGraph ).and( edge ).toUniqueStream();
    }

    private <P extends OWLPropertyExpression, O extends OWLPropertyAssertionObject> Stream<GraphElement>
    visit( final OWLPropertyAssertionAxiom<P, O> axiom, final Node thirdNode, final Edge.Type toThirdNodeEdgeType ) {

        final Graph subjectGraph = axiom.getSubject().accept( mappingConfig.getOwlIndividualMapper() );
        final Graph propertyGraph = axiom.getProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Graph objectGraph = axiom.getObject().accept( mappingConfig.getOwlObjectMapper() );

        final Edge subectToThirdNode = new PlainEdge( toThirdNodeEdgeType, subjectGraph.getNode().getId(),
            thirdNode.getId() );
        final Edge thirdNodeToObject = new PlainEdge( Edge.Type.DEFAULT_ARROW, thirdNode.getId(),
            objectGraph.getNode().getId() );
        final Edge thirdNodeToProperty = new PlainEdge( Edge.Type.DASHED_ARROW, thirdNode.getId(),
            propertyGraph.getNode().getId() );

        return subjectGraph
            .and( propertyGraph )
            .and( objectGraph )
            .and( thirdNode )
            .and( subectToThirdNode )
            .and( thirdNodeToObject )
            .and( thirdNodeToProperty ).toStream();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLNegativeObjectPropertyAssertionAxiom axiom ) {
        return visit( axiom, new NodeType.Complement( mappingConfig.getIdentifierMapper()
            .getSyntheticId() ), Edge.Type.DEFAULT_ARROW );
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLAsymmetricObjectPropertyAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLReflexiveObjectPropertyAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLDisjointClassesAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLDataPropertyDomainAxiom axiom ) {
        final Graph domainGraph = axiom.getDomain().accept( mappingConfig.getOwlClassExpressionMapper() );
        final Graph propertyGraph = axiom.getProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Node domainNode = new NodeType.Domain( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final Edge fromDomainNodeToDomain = new PlainEdge( Edge.Type.HOLLOW_ARROW, domainNode.getId(), domainGraph
            .getNode().getId() );
        final Edge fromDomainNodeToProperty = new PlainEdge( Edge.Type.DEFAULT_ARROW, domainNode.getId(), propertyGraph
            .getNode().getId() );
        return domainGraph.and( propertyGraph ).and( domainNode ).and( fromDomainNodeToDomain )
            .and( fromDomainNodeToProperty ).toUniqueStream();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLObjectPropertyDomainAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLEquivalentObjectPropertiesAxiom axiom ) {
        return visit( axiom, mappingConfig.getOwlObjectMapper() );
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLNegativeDataPropertyAssertionAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLDifferentIndividualsAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLDisjointDataPropertiesAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLDisjointObjectPropertiesAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLObjectPropertyRangeAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLObjectPropertyAssertionAxiom axiom ) {
        return visit( axiom, new NodeType.Invisible( mappingConfig.getIdentifierMapper()
            .getSyntheticId() ), Edge.Type.NO_ARROW );
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLFunctionalObjectPropertyAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLSubObjectPropertyOfAxiom axiom ) {
        final OWLPropertyExpressionVisitorEx<Graph> mapper = mappingConfig.getOwlPropertyExpressionMapper();

        final Graph superPropertyGraph = axiom.getSuperProperty().accept( mapper );
        final Graph subPropertyGraph = axiom.getSubProperty().accept( mapper );

        final Edge edge = new PlainEdge( Edge.Type.HOLLOW_ARROW, subPropertyGraph.getNode().getId(),
            superPropertyGraph.getNode().getId() );

        return superPropertyGraph.and( subPropertyGraph ).and( edge ).toUniqueStream();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLDisjointUnionAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLSymmetricObjectPropertyAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLDataPropertyRangeAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLFunctionalDataPropertyAxiom axiom ) {
        return Stream.empty();
    }

    /**
     * Shared logic for axioms that generate sets of nodes that are pairwise equivalent,
     * e.g. {@link org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom}s.
     *
     * @param axiom   The axiom to generate results for
     * @param visitor The visitor that handles the type of axiom
     * @param <O>     The type of object the axiom describes
     * @param <A>     The axiom type
     * @param <V>     The type of visitor that handles the axiom type
     * @return The visitor's result
     */
    private <O extends OWLObject, A extends OWLNaryAxiom<O>, V extends OWLObjectVisitorEx<Graph>>
    Stream<GraphElement> visit( final A axiom, final V visitor ) {

        final Map<O, Graph> operands = axiom.operands().collect( Collectors.toMap( Function.identity(),
            object -> object.accept( visitor ) ) );

        // Create all combinations of operands, but (1) keep every combination only once,
        // regardless of direction and (2) remove those combinations where both elements are the same
        final Set<List<O>> combinations = Sets.cartesianProduct( Arrays.asList( operands.keySet(),
            operands.keySet() ) ).stream().map( expressionsList -> {
                final List<O> newList = new ArrayList<>( expressionsList );
                newList.sort( Comparator.comparing( o -> operands.get( o ).getNode().getId().getId() ) );
                return newList;
            }
        ).filter( expressionsList -> {
            final Iterator<O> iterator = expressionsList.iterator();
            return !iterator.next().equals( iterator.next() );
        } ).collect( Collectors.toSet() );

        // For each of the combinations, create a corresponding edge
        final Stream<Edge> edges = combinations.stream().map( expressionsList -> {
            final Iterator<O> iterator = expressionsList.iterator();
            final Graph graph1 = operands.get( iterator.next() );
            final Graph graph2 = operands.get( iterator.next() );
            return new PlainEdge( Edge.Type.DOUBLE_ENDED_HOLLOW_ARROW, graph1.getNode().getId(),
                graph2.getNode().getId() );
        } );

        return Stream.concat( operands.values().stream().flatMap( Graph::toStream ), edges );
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLEquivalentDataPropertiesAxiom axiom ) {
        return visit( axiom, mappingConfig.getOwlObjectMapper() );
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLClassAssertionAxiom axiom ) {
        final OWLIndividual individual = axiom.getIndividual();
        final OWLClassExpression classExpression = axiom.getClassExpression();
        final Graph individualGraph = individual.accept( mappingConfig.getOwlIndividualMapper() );
        final Graph classExpressionGraph =
            classExpression.accept( mappingConfig.getOwlClassExpressionMapper() );

        final Edge edge = new PlainEdge( Edge.Type.DEFAULT_ARROW, individualGraph.getNode().getId(),
            classExpressionGraph.getNode().getId() );
        return individualGraph.and( classExpressionGraph ).and( edge ).toUniqueStream();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLEquivalentClassesAxiom axiom ) {
        return visit( axiom, mappingConfig.getOwlObjectMapper() );
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLDataPropertyAssertionAxiom axiom ) {
        return visit( axiom, new NodeType.Invisible( mappingConfig.getIdentifierMapper()
            .getSyntheticId() ), Edge.Type.NO_ARROW );
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLTransitiveObjectPropertyAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLIrreflexiveObjectPropertyAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLSubDataPropertyOfAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLInverseFunctionalObjectPropertyAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLSameIndividualAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLSubPropertyChainOfAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLInverseObjectPropertiesAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLHasKeyAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLDeclarationAxiom axiom ) {
        final OWLEntityVisitorEx<Graph> mapper = mappingConfig.getOwlEntityMapper();
        final Graph graph = axiom.getEntity().accept( mapper );
        return graph.toUniqueStream();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLDatatypeDefinitionAxiom axiom ) {
        return Stream.empty();
    }


    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLAnnotationAssertionAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLSubAnnotationPropertyOfAxiom axiom ) {
        final Graph superPropertyGraph =
            axiom.getSuperProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Graph subPropertyGraph =
            axiom.getSubProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Edge edge = new PlainEdge( Edge.Type.HOLLOW_ARROW, subPropertyGraph.getNode().getId(),
            superPropertyGraph.getNode().getId() );
        return subPropertyGraph.and( superPropertyGraph ).and( edge ).toUniqueStream();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLAnnotationPropertyDomainAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLAnnotationPropertyRangeAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull SWRLRule node ) {
        return Stream.empty();
    }

    @Override
    public <T> Stream<GraphElement> doDefault( final T object ) {
        return Stream.empty();
    }
}
