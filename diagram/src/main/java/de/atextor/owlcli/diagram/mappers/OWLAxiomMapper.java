package de.atextor.owlcli.diagram.mappers;

import com.google.common.collect.Sets;
import de.atextor.owlcli.diagram.graph.DecoratedEdge;
import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.Graph;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.NodeType;
import de.atextor.owlcli.diagram.graph.PlainEdge;
import org.semanticweb.owlapi.model.HasOperands;
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
import org.semanticweb.owlapi.model.OWLPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import org.semanticweb.owlapi.model.OWLPropertyRangeAxiom;
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
import java.util.stream.StreamSupport;

import static io.vavr.API.TODO;

public class OWLAxiomMapper implements OWLAxiomVisitorEx<Graph> {
    private final MappingConfiguration mappingConfig;

    public OWLAxiomMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public Graph visit( final @Nonnull OWLSubClassOfAxiom axiom ) {
        final OWLClassExpressionVisitorEx<Graph> mapper = mappingConfig.getOwlClassExpressionMapper();

        final Graph superClassGraph = axiom.getSuperClass().accept( mapper );
        final Graph subClassGraph = axiom.getSubClass().accept( mapper );

        final Edge edge = new PlainEdge( Edge.Type.HOLLOW_ARROW, subClassGraph.getNode().getId(),
            superClassGraph.getNode().getId() );

        return superClassGraph.and( subClassGraph ).and( edge );
    }

    private <P extends OWLPropertyExpression, O extends OWLPropertyAssertionObject> Stream<GraphElement>
    propertyStructure( final OWLPropertyAssertionAxiom<P, O> axiom, final Node thirdNode ) {

        final Graph subjectGraph = axiom.getSubject().accept( mappingConfig.getOwlIndividualMapper() );
        final Graph propertyGraph = axiom.getProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Graph objectGraph = axiom.getObject().accept( mappingConfig.getOwlObjectMapper() );

        final Edge subjectToThirdNode = new PlainEdge( Edge.Type.NO_ARROW, subjectGraph.getNode().getId(),
            thirdNode.getId() );
        final Edge thirdNodeToObject = new PlainEdge( Edge.Type.DEFAULT_ARROW, thirdNode.getId(),
            objectGraph.getNode().getId() );
        final Edge thirdNodeToProperty = new PlainEdge( Edge.Type.DASHED_ARROW, thirdNode.getId(),
            propertyGraph.getNode().getId() );

        return subjectGraph
            .and( propertyGraph )
            .and( objectGraph )
            .and( thirdNode )
            .and( subjectToThirdNode )
            .and( thirdNodeToObject )
            .and( thirdNodeToProperty ).toStream();
    }

    @Override
    public Graph visit( final @Nonnull OWLNegativeObjectPropertyAssertionAxiom axiom ) {
        final Node complement = new NodeType.Complement( mappingConfig.getIdentifierMapper()
            .getSyntheticId() );
        return Graph.of( complement ).and( propertyStructure( axiom, complement ) );
    }

    @Override
    public Graph visit( final @Nonnull OWLAsymmetricObjectPropertyAxiom axiom ) {
        return propertyMarker( axiom.getProperty(), NodeType.PropertyMarker.Kind.ASYMMETRIC );
    }

    @Override
    public Graph visit( final @Nonnull OWLReflexiveObjectPropertyAxiom axiom ) {
        return TODO();
    }

    @Override
    public Graph visit( final @Nonnull OWLDisjointClassesAxiom axiom ) {
        final Node disjointness = new NodeType.Disjointness( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return linkNodeToMultipleOthers( axiom, disjointness );
    }

    private <P extends OWLPropertyExpression, A extends OWLPropertyDomainAxiom<P>> Graph propertyDomain( final A axiom ) {
        final Graph domainGraph = axiom.getDomain().accept( mappingConfig.getOwlClassExpressionMapper() );
        final Graph propertyGraph = axiom.getProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Edge domainEdge = new DecoratedEdge( Edge.Type.DEFAULT_ARROW, propertyGraph.getNode().getId(), domainGraph
            .getNode().getId(), DecoratedEdge.DOMAIN );
        return domainGraph.and( propertyGraph ).and( domainEdge );
    }

    private <P extends OWLPropertyExpression, R extends OWLPropertyRange, A extends OWLPropertyRangeAxiom<P, R>> Graph propertyRange( final A axiom ) {
        final Graph propertyGraph = axiom.getProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Graph rangeGraph = axiom.getRange().accept( mappingConfig.getOwlObjectMapper() );
        final Edge rangeEdge = new DecoratedEdge( Edge.Type.DEFAULT_ARROW, propertyGraph.getNode().getId(), rangeGraph
            .getNode().getId(), DecoratedEdge.RANGE );
        return propertyGraph.and( rangeGraph ).and( rangeEdge );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataPropertyDomainAxiom axiom ) {
        return propertyDomain( axiom );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectPropertyDomainAxiom axiom ) {
        return propertyDomain( axiom );
    }

    @Override
    public Graph visit( final @Nonnull OWLEquivalentObjectPropertiesAxiom axiom ) {
        return pairwiseEquivalent( axiom, mappingConfig.getOwlObjectMapper() );
    }

    @Override
    public Graph visit( final @Nonnull OWLNegativeDataPropertyAssertionAxiom axiom ) {
        final Node complement = new NodeType.Complement( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return Graph.of( complement ).and( propertyStructure( axiom, complement ) );
    }

    @Override
    public Graph visit( final @Nonnull OWLDifferentIndividualsAxiom axiom ) {
        final Node inequality = new NodeType.Inequality( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return linkNodeToMultipleOthers( axiom, inequality );
    }

    @Override
    public Graph visit( final @Nonnull OWLDisjointDataPropertiesAxiom axiom ) {
        final Node disjointness = new NodeType.Disjointness( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return linkNodeToMultipleOthers( axiom, disjointness );
    }

    @Override
    public Graph visit( final @Nonnull OWLDisjointObjectPropertiesAxiom axiom ) {
        final Node disjointness = new NodeType.Disjointness( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return linkNodeToMultipleOthers( axiom, disjointness );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectPropertyRangeAxiom axiom ) {
        return propertyRange( axiom );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectPropertyAssertionAxiom axiom ) {
        final Node invisible = new NodeType.Invisible( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return Graph.of( invisible ).and( propertyStructure( axiom, invisible ) );
    }

    @Override
    public Graph visit( final @Nonnull OWLFunctionalObjectPropertyAxiom axiom ) {
        return propertyMarker( axiom.getProperty(), NodeType.PropertyMarker.Kind.FUNCTIONAL );
    }

    @Override
    public Graph visit( final @Nonnull OWLSubObjectPropertyOfAxiom axiom ) {
        final OWLPropertyExpressionVisitorEx<Graph> mapper = mappingConfig.getOwlPropertyExpressionMapper();

        final Graph superPropertyGraph = axiom.getSuperProperty().accept( mapper );
        final Graph subPropertyGraph = axiom.getSubProperty().accept( mapper );
        return subProperties( superPropertyGraph, subPropertyGraph );
    }

    private <T extends OWLObject> Graph linkNodeToMultipleOthers( final HasOperands<T> axiom,
                                                                  final Node fromNode ) {
        return axiom.operands().map( operand -> {
            final Graph operandGraph = operand.accept( mappingConfig.getOwlObjectMapper() );
            final Edge fromNodeToOperandEdge = new PlainEdge( Edge.Type.DEFAULT_ARROW, fromNode.getId(),
                operandGraph.getNode().getId() );
            return operandGraph.and( fromNodeToOperandEdge );
        } ).reduce( Graph.of( fromNode ), Graph::and );
    }

    @Override
    public Graph visit( final @Nonnull OWLDisjointUnionAxiom axiom ) {
        return linkNodeToMultipleOthers( axiom, new NodeType.DisjointUnion( mappingConfig.getIdentifierMapper()
            .getSyntheticId() ) );
    }

    @Override
    public Graph visit( final @Nonnull OWLSymmetricObjectPropertyAxiom axiom ) {
        return propertyMarker( axiom.getProperty(), NodeType.PropertyMarker.Kind.SYMMETRIC );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataPropertyRangeAxiom axiom ) {
        return propertyRange( axiom );
    }

    private Graph propertyMarker( final OWLPropertyExpression propertyExpression,
                                  final NodeType.PropertyMarker.Kind markerKind ) {
        final Node marker = new NodeType.PropertyMarker( mappingConfig.getIdentifierMapper().getSyntheticId(),
            Set.of( markerKind ) );
        final Node propertyNode = propertyExpression.accept( mappingConfig.getOwlPropertyExpressionMapper() ).getNode();
        final Edge edge = new PlainEdge( Edge.Type.DASHED_ARROW, propertyNode.getId(), marker.getId() );
        return Graph.of( marker ).and( propertyNode ).and( edge );
    }

    @Override
    public Graph visit( final @Nonnull OWLFunctionalDataPropertyAxiom axiom ) {
        return propertyMarker( axiom.getProperty(), NodeType.PropertyMarker.Kind.FUNCTIONAL );
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
     * @return the graph representing the equivalency
     */
    private <O extends OWLObject, A extends OWLNaryAxiom<O>, V extends OWLObjectVisitorEx<Graph>>
    Graph pairwiseEquivalent( final A axiom, final V visitor ) {

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
        final Stream<GraphElement> edges = combinations.stream().map( expressionsList -> {
            final Iterator<O> iterator = expressionsList.iterator();
            final Graph graph1 = operands.get( iterator.next() );
            final Graph graph2 = operands.get( iterator.next() );
            return new PlainEdge( Edge.Type.DOUBLE_ENDED_HOLLOW_ARROW, graph1.getNode().getId(),
                graph2.getNode().getId() );
        } );

        final Iterator<Graph> operandsIterator = operands.values().iterator();
        final Node firstOperand = operandsIterator.next().getNode();
        final Iterable<Graph> iterable = () -> operandsIterator;
        final Stream<Graph> remaining = StreamSupport.stream( iterable.spliterator(), false );

        return Graph.of( firstOperand ).and( remaining.flatMap( Graph::toStream ) ).and( edges );
    }

    @Override
    public Graph visit( final @Nonnull OWLEquivalentDataPropertiesAxiom axiom ) {
        return pairwiseEquivalent( axiom, mappingConfig.getOwlObjectMapper() );
    }

    @Override
    public Graph visit( final @Nonnull OWLClassAssertionAxiom axiom ) {
        final OWLIndividual individual = axiom.getIndividual();
        final OWLClassExpression classExpression = axiom.getClassExpression();
        final Graph individualGraph = individual.accept( mappingConfig.getOwlIndividualMapper() );
        final Graph classExpressionGraph =
            classExpression.accept( mappingConfig.getOwlClassExpressionMapper() );

        final Edge edge = new PlainEdge( Edge.Type.DEFAULT_ARROW, individualGraph.getNode().getId(),
            classExpressionGraph.getNode().getId() );
        return individualGraph.and( classExpressionGraph ).and( edge );
    }

    @Override
    public Graph visit( final @Nonnull OWLEquivalentClassesAxiom axiom ) {
        return pairwiseEquivalent( axiom, mappingConfig.getOwlObjectMapper() );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataPropertyAssertionAxiom axiom ) {
        final Node invisible = new NodeType.Invisible( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return Graph.of( invisible ).and( propertyStructure( axiom, invisible ) );
    }

    @Override
    public Graph visit( final @Nonnull OWLTransitiveObjectPropertyAxiom axiom ) {
        return propertyMarker( axiom.getProperty(), NodeType.PropertyMarker.Kind.TRANSITIVE );
    }

    @Override
    public Graph visit( final @Nonnull OWLIrreflexiveObjectPropertyAxiom axiom ) {
        return TODO();
    }

    private Graph subProperties( final Graph superPropertyGraph, final Graph subPropertyGraph ) {
        final Edge edge = new PlainEdge( Edge.Type.HOLLOW_ARROW, subPropertyGraph.getNode().getId(),
            superPropertyGraph.getNode().getId() );
        return superPropertyGraph.and( subPropertyGraph ).and( edge );
    }

    @Override
    public Graph visit( final @Nonnull OWLSubDataPropertyOfAxiom axiom ) {
        final OWLPropertyExpressionVisitorEx<Graph> mapper = mappingConfig.getOwlPropertyExpressionMapper();

        final Graph superPropertyGraph = axiom.getSuperProperty().accept( mapper );
        final Graph subPropertyGraph = axiom.getSubProperty().accept( mapper );
        return subProperties( superPropertyGraph, subPropertyGraph );
    }

    @Override
    public Graph visit( final @Nonnull OWLInverseFunctionalObjectPropertyAxiom axiom ) {
        return propertyMarker( axiom.getProperty(), NodeType.PropertyMarker.Kind.INVERSE_FUNCTIONAL );
    }

    @Override
    public Graph visit( final @Nonnull OWLSameIndividualAxiom axiom ) {
        final Node equality = new NodeType.Equality( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return linkNodeToMultipleOthers( axiom, equality );
    }

    @Override
    public Graph visit( final @Nonnull OWLSubPropertyChainOfAxiom axiom ) {
        return TODO();
    }

    @Override
    public Graph visit( final @Nonnull OWLInverseObjectPropertiesAxiom axiom ) {
        final Node equality = new NodeType.Inverse( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return linkNodeToMultipleOthers( axiom, equality );
    }

    @Override
    public Graph visit( final @Nonnull OWLHasKeyAxiom axiom ) {
        return TODO();
    }

    @Override
    public Graph visit( final @Nonnull OWLDeclarationAxiom axiom ) {
        return axiom.getEntity().accept( mappingConfig.getOwlEntityMapper() );
    }

    @Override
    public Graph visit( final @Nonnull OWLDatatypeDefinitionAxiom axiom ) {
        return TODO();
    }

    @Override
    public Graph visit( final @Nonnull OWLAnnotationAssertionAxiom axiom ) {
        final Graph subjectGraph = axiom.getSubject().accept( mappingConfig.getOwlAnnotationSubjectMapper() );
        final Graph propertyGraph = axiom.getProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Graph objectGraph = axiom.getValue().accept( mappingConfig.getOwlAnnotationObjectMapper() );

        final Node invisible = new NodeType.Invisible( mappingConfig.getIdentifierMapper()
            .getSyntheticId() );

        final Edge subjectToInvisible = new PlainEdge( Edge.Type.NO_ARROW, subjectGraph.getNode().getId(),
            invisible.getId() );
        final Edge invisibleToObject = new PlainEdge( Edge.Type.DEFAULT_ARROW, invisible.getId(),
            objectGraph.getNode().getId() );
        final Edge invisibleToProperty = new PlainEdge( Edge.Type.DASHED_ARROW, invisible.getId(),
            propertyGraph.getNode().getId() );

        return subjectGraph
            .and( propertyGraph )
            .and( objectGraph )
            .and( invisible )
            .and( subjectToInvisible )
            .and( invisibleToObject )
            .and( invisibleToProperty );
    }

    @Override
    public Graph visit( final @Nonnull OWLSubAnnotationPropertyOfAxiom axiom ) {
        final OWLPropertyExpressionVisitorEx<Graph> mapper = mappingConfig.getOwlPropertyExpressionMapper();
        final Graph superPropertyGraph = axiom.getSuperProperty().accept( mapper );
        final Graph subPropertyGraph = axiom.getSubProperty().accept( mapper );
        return subProperties( superPropertyGraph, subPropertyGraph );
    }

    @Override
    public Graph visit( final @Nonnull OWLAnnotationPropertyDomainAxiom axiom ) {
        final NodeType.IRIReference iriReference = new NodeType.IRIReference(
            mappingConfig.getIdentifierMapper().getSyntheticId(), axiom.getDomain() );
        final Graph propertyGraph = axiom.getProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Edge domainEdge = new DecoratedEdge( Edge.Type.DEFAULT_ARROW, propertyGraph.getNode().getId(),
            iriReference.getId(), DecoratedEdge.DOMAIN );
        return propertyGraph.and( iriReference ).and( domainEdge );
    }

    @Override
    public Graph visit( final @Nonnull OWLAnnotationPropertyRangeAxiom axiom ) {
        final NodeType.IRIReference iriReference = new NodeType.IRIReference(
            mappingConfig.getIdentifierMapper().getSyntheticId(), axiom.getRange() );
        final Graph propertyGraph = axiom.getProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Edge domainEdge = new DecoratedEdge( Edge.Type.DEFAULT_ARROW, propertyGraph.getNode().getId(),
            iriReference.getId(), DecoratedEdge.RANGE );
        return propertyGraph.and( iriReference ).and( domainEdge );
    }

    @Override
    public Graph visit( final @Nonnull SWRLRule node ) {
        return TODO();
    }

    @Override
    public <T> Graph doDefault( final T object ) {
        return TODO();
    }
}
