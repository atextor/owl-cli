package de.atextor.owldiagram.mappers;

import com.google.common.collect.Sets;
import de.atextor.owldiagram.graph.Edge;
import de.atextor.owldiagram.graph.GraphElement;
import de.atextor.owldiagram.graph.Node;
import de.atextor.owldiagram.graph.NodeType;
import de.atextor.owldiagram.graph.PlainEdge;
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
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OWLAxiomMapper implements OWLAxiomVisitorEx<Stream<GraphElement>> {
    private MappingConfiguration mappingConfig;

    public OWLAxiomMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public Stream<GraphElement> visit( final OWLSubClassOfAxiom axiom ) {
        final OWLClassExpressionVisitorEx<Result> mapper = mappingConfig.getOwlClassExpressionMapper();

        final Result superClassResult = axiom.getSuperClass().accept( mapper );
        final Result subClassResult = axiom.getSubClass().accept( mapper );

        final Edge edge = new PlainEdge( Edge.Type.HOLLOW_ARROW, subClassResult.getNode().getId(),
            superClassResult.getNode().getId() );

        return superClassResult.and( subClassResult ).and( edge ).toStream();
    }

    private <P extends OWLPropertyExpression, O extends OWLPropertyAssertionObject> Stream<GraphElement>
    visit( final OWLPropertyAssertionAxiom<P, O> axiom, final Supplier<Node> thirdNodeSupplier ) {

        final OWLIndividualVisitorEx<Result> individualMapper = mappingConfig.getOwlIndividualMapper();
        final OWLPropertyExpressionVisitorEx<Result> propertyMapper = mappingConfig.getOwlPropertyExpressionMapper();
        final OWLObjectVisitorEx<Result> objectMapper = mappingConfig.getOwlObjectMapper();

        final Result subjectResult = axiom.getSubject().accept( individualMapper );
        final Result propertyResult = axiom.getProperty().accept( propertyMapper );
        final Result objectResult = axiom.getObject().accept( objectMapper );

        final Node thirdNode = thirdNodeSupplier.get();
        final Edge subectToThirdNode = new PlainEdge( Edge.Type.NO_ARROW, subjectResult.getNode().getId(),
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
    public Stream<GraphElement> visit( final OWLNegativeObjectPropertyAssertionAxiom axiom ) {
        final IdentifierMapper identifierMapper = mappingConfig.getIdentifierMapper();

        final Supplier<Node> thirdNodeSupplier = () -> new NodeType.Complement( identifierMapper.getSyntheticId() );
        return visit( axiom, thirdNodeSupplier );
    }

    @Override
    public Stream<GraphElement> visit( final OWLAsymmetricObjectPropertyAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final OWLReflexiveObjectPropertyAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final OWLDisjointClassesAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final OWLDataPropertyDomainAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final OWLObjectPropertyDomainAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final OWLEquivalentObjectPropertiesAxiom axiom ) {
        return visit( axiom, mappingConfig.getOwlObjectMapper() );
    }

    @Override
    public Stream<GraphElement> visit( final OWLNegativeDataPropertyAssertionAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final OWLDifferentIndividualsAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final OWLDisjointDataPropertiesAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final OWLDisjointObjectPropertiesAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final OWLObjectPropertyRangeAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final OWLObjectPropertyAssertionAxiom axiom ) {
        final IdentifierMapper identifierMapper = mappingConfig.getIdentifierMapper();

        final Supplier<Node> thirdNodeSupplier = () -> new NodeType.Invisible( identifierMapper.getSyntheticId() );
        return visit( axiom, thirdNodeSupplier );
    }

    @Override
    public Stream<GraphElement> visit( final OWLFunctionalObjectPropertyAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final OWLSubObjectPropertyOfAxiom axiom ) {
        final OWLPropertyExpressionVisitorEx<Result> mapper = mappingConfig.getOwlPropertyExpressionMapper();

        final Result superPropertyResult = axiom.getSuperProperty().accept( mapper );
        final Result subPropertyResult = axiom.getSubProperty().accept( mapper );

        final Edge edge = new PlainEdge( Edge.Type.HOLLOW_ARROW, subPropertyResult.getNode().getId(),
            superPropertyResult.getNode().getId() );

        return superPropertyResult.and( subPropertyResult ).and( edge ).toStream();
    }

    @Override
    public Stream<GraphElement> visit( final OWLDisjointUnionAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final OWLSymmetricObjectPropertyAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final OWLDataPropertyRangeAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final OWLFunctionalDataPropertyAxiom axiom ) {
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
    public Stream<GraphElement> visit( final OWLEquivalentDataPropertiesAxiom axiom ) {
        return visit( axiom, mappingConfig.getOwlObjectMapper() );
    }

    @Override
    public Stream<GraphElement> visit( final OWLClassAssertionAxiom axiom ) {
        final OWLIndividual individual = axiom.getIndividual();
        final OWLClassExpression classExpression = axiom.getClassExpression();
        final Result individualResult = individual.accept( mappingConfig.getOwlIndividualMapper() );
        final Result classExpressionResult =
            classExpression.accept( mappingConfig.getOwlClassExpressionMapper() );

        final Edge edge = new PlainEdge( Edge.Type.DEFAULT_ARROW, individualResult.getNode().getId(),
            classExpressionResult.getNode().getId() );
        return individualResult.and( classExpressionResult ).and( edge ).toStream();
    }

    @Override
    public Stream<GraphElement> visit( final OWLEquivalentClassesAxiom axiom ) {
        return visit( axiom, mappingConfig.getOwlObjectMapper() );
    }

    @Override
    public Stream<GraphElement> visit( final OWLDataPropertyAssertionAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final OWLTransitiveObjectPropertyAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final OWLIrreflexiveObjectPropertyAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final OWLSubDataPropertyOfAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final OWLInverseFunctionalObjectPropertyAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final OWLSameIndividualAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final OWLSubPropertyChainOfAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final OWLInverseObjectPropertiesAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final OWLHasKeyAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final OWLDeclarationAxiom axiom ) {
        final OWLEntityVisitorEx<Result> mapper = mappingConfig.getOwlEntityMapper();
        final Result result = axiom.getEntity().accept( mapper );
        return result.toStream();
    }

    @Override
    public Stream<GraphElement> visit( final OWLDatatypeDefinitionAxiom axiom ) {
        return Stream.empty();
    }


    @Override
    public Stream<GraphElement> visit( final OWLAnnotationAssertionAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final OWLSubAnnotationPropertyOfAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final OWLAnnotationPropertyDomainAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final OWLAnnotationPropertyRangeAxiom axiom ) {
        return Stream.empty();
    }

    @Override
    public Stream<GraphElement> visit( final SWRLRule node ) {
        return Stream.empty();
    }

    @Override
    public <T> Stream<GraphElement> doDefault( final T object ) {
        return Stream.empty();
    }
}
