package de.atextor.owlcli.diagram.mappers;

import com.google.common.collect.Sets;
import de.atextor.owlcli.diagram.graph.Edge;
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
        final OWLClassExpressionVisitorEx<Result> mapper = mappingConfig.getOwlClassExpressionMapper();

        final Result superClassResult = axiom.getSuperClass().accept( mapper );
        final Result subClassResult = axiom.getSubClass().accept( mapper );

        final Edge edge = new PlainEdge( Edge.Type.HOLLOW_ARROW, subClassResult.getNode().getId(),
            superClassResult.getNode().getId() );

        return superClassResult.and( subClassResult ).and( edge ).toUniqueStream();
    }

    private <P extends OWLPropertyExpression, O extends OWLPropertyAssertionObject> Stream<GraphElement>
    visit( final OWLPropertyAssertionAxiom<P, O> axiom, final Node thirdNode, final Edge.Type toThirdNodeEdgeType ) {

        final Result subjectResult = axiom.getSubject().accept( mappingConfig.getOwlIndividualMapper() );
        final Result propertyResult = axiom.getProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Result objectResult = axiom.getObject().accept( mappingConfig.getOwlObjectMapper() );

        final Edge subectToThirdNode = new PlainEdge( toThirdNodeEdgeType, subjectResult.getNode().getId(),
            thirdNode.getId() );
        final Edge thirdNodeToObject = new PlainEdge( Edge.Type.DEFAULT_ARROW, thirdNode.getId(),
            objectResult.getNode().getId() );
        final Edge thirdNodeToProperty = new PlainEdge( Edge.Type.DASHED_ARROW, thirdNode.getId(),
            propertyResult.getNode().getId() );

        return subjectResult
            .and( propertyResult )
            .and( objectResult )
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
        final Result domainResult = axiom.getDomain().accept( mappingConfig.getOwlClassExpressionMapper() );
        final Result propertyResult = axiom.getProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Node domainNode = new NodeType.Domain( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final Edge fromDomainNodeToDomain = new PlainEdge( Edge.Type.HOLLOW_ARROW, domainNode.getId(), domainResult
            .getNode().getId() );
        final Edge fromDomainNodeToProperty = new PlainEdge( Edge.Type.DEFAULT_ARROW, domainNode.getId(), propertyResult
            .getNode().getId() );
        return domainResult.and( propertyResult ).and( domainNode ).and( fromDomainNodeToDomain )
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
        final OWLPropertyExpressionVisitorEx<Result> mapper = mappingConfig.getOwlPropertyExpressionMapper();

        final Result superPropertyResult = axiom.getSuperProperty().accept( mapper );
        final Result subPropertyResult = axiom.getSubProperty().accept( mapper );

        final Edge edge = new PlainEdge( Edge.Type.HOLLOW_ARROW, subPropertyResult.getNode().getId(),
            superPropertyResult.getNode().getId() );

        return superPropertyResult.and( subPropertyResult ).and( edge ).toUniqueStream();
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
    private <O extends OWLObject, A extends OWLNaryAxiom<O>, V extends OWLObjectVisitorEx<Result>>
    Stream<GraphElement> visit( final A axiom, final V visitor ) {

        final Map<O, Result> operands = axiom.operands().collect( Collectors.toMap( Function.identity(),
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
            final Result result1 = operands.get( iterator.next() );
            final Result result2 = operands.get( iterator.next() );
            return new PlainEdge( Edge.Type.DOUBLE_ENDED_HOLLOW_ARROW, result1.getNode().getId(),
                result2.getNode().getId() );
        } );

        return Stream.concat( operands.values().stream().flatMap( Result::toStream ), edges );
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLEquivalentDataPropertiesAxiom axiom ) {
        return visit( axiom, mappingConfig.getOwlObjectMapper() );
    }

    @Override
    public Stream<GraphElement> visit( final @Nonnull OWLClassAssertionAxiom axiom ) {
        final OWLIndividual individual = axiom.getIndividual();
        final OWLClassExpression classExpression = axiom.getClassExpression();
        final Result individualResult = individual.accept( mappingConfig.getOwlIndividualMapper() );
        final Result classExpressionResult =
            classExpression.accept( mappingConfig.getOwlClassExpressionMapper() );

        final Edge edge = new PlainEdge( Edge.Type.DEFAULT_ARROW, individualResult.getNode().getId(),
            classExpressionResult.getNode().getId() );
        return individualResult.and( classExpressionResult ).and( edge ).toUniqueStream();
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
        final OWLEntityVisitorEx<Result> mapper = mappingConfig.getOwlEntityMapper();
        final Result result = axiom.getEntity().accept( mapper );
        return result.toUniqueStream();
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
        final Result superPropertyResult =
            axiom.getSuperProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Result subPropertyResult =
            axiom.getSubProperty().accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Edge edge = new PlainEdge( Edge.Type.HOLLOW_ARROW, subPropertyResult.getNode().getId(),
            superPropertyResult.getNode().getId() );
        return subPropertyResult.and( superPropertyResult ).and( edge ).toUniqueStream();
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
