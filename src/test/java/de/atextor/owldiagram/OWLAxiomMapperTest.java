package de.atextor.owldiagram;

import de.atextor.owldiagram.graph.Edge;
import de.atextor.owldiagram.graph.GraphElement;
import de.atextor.owldiagram.graph.Node;
import de.atextor.owldiagram.mappers.OWLAxiomMapper;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLEquivalentClassesAxiomImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class OWLAxiomMapperTest extends MapperTestBase {
    OWLAxiomMapper mapper = new OWLAxiomMapper();

    @Test
    public void testOWLSubClassOfAxiom() {
        final OWLSubClassOfAxiom axiom = getAxiom( ":Foo rdfs:subClassOf :Bar ." );
        final List<GraphElement> result = mapper.visit( axiom ).collect( Collectors.toList() );
        assertThat( result ).hasSize( 3 );
    }

    @Test
    public void testOWLDeclarationAxiom() {
        final OWLDeclarationAxiom axiom = getAxiom( ":Foo a owl:Class ." );
        final List<GraphElement> result = mapper.visit( axiom ).collect( Collectors.toList() );
        assertThat( result ).hasSize( 1 );
    }

    @Test
    public void testOWLClassAssertionAxiom() {
        final OWLClassAssertionAxiom axiom = getAxiom( ":Foo a owl:Thing ." );
        final List<GraphElement> result = mapper.visit( axiom ).collect( Collectors.toList() );
        assertThat( result ).hasSize( 3 );
    }

    private Predicate<Node> isNodeWithId( final String targetId ) {
        return node -> node.getId().getId().equals( targetId );
    }

    private Predicate<Edge> isEdgeWithFromAndTo( final String fromId, final String toId ) {
        return edge -> edge.getFrom().getId().equals( fromId ) && edge.getTo().getId().equals( toId );
    }

    @Test
    public void testOWLEquivalentClassesAxiom() {
        final IRI fooIri = IRI.create( "http://test.de/Foo" );
        final IRI barIri = IRI.create( "http://test.de/Bar" );
        final IRI bazIri = IRI.create( "http://test.de/Baz" );
        final OWLClassExpression classExpression1 = new OWLClassImpl( fooIri );
        final OWLClassExpression classExpression2 = new OWLClassImpl( barIri );
        final OWLClassExpression classExpression3 = new OWLClassImpl( bazIri );
        final OWLEquivalentClassesAxiom axiom = new OWLEquivalentClassesAxiomImpl( Arrays.asList( classExpression1,
                classExpression2, classExpression3 ), Collections.emptyList() );
        final List<GraphElement> result = mapper.visit( axiom ).collect( Collectors.toList() );

        final List<Node> nodes =
                result.stream().filter( GraphElement::isNode ).map( GraphElement::asNode ).collect( Collectors.toList() );
        assertThat( nodes ).hasSize( 3 );

        assertThat( nodes ).anyMatch( isNodeWithId( "Foo" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "Bar" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "Baz" ) );

        final List<Edge> edges =
                result.stream().filter( GraphElement::isEdge ).map( GraphElement::asEdge ).collect( Collectors.toList() );

        assertThat( edges ).hasSize( 3 );

        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( "Bar", "Foo" ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( "Baz", "Foo" ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( "Bar", "Baz" ) );

        assertThat( edges ).noneMatch( isEdgeWithFromAndTo( "Foo", "Bar" ) );
        assertThat( edges ).noneMatch( isEdgeWithFromAndTo( "Foo", "Baz" ) );
        assertThat( edges ).noneMatch( isEdgeWithFromAndTo( "Baz", "Bar" ) );
        assertThat( edges ).noneMatch( isEdgeWithFromAndTo( "Foo", "Foo" ) );
        assertThat( edges ).noneMatch( isEdgeWithFromAndTo( "Bar", "Bar" ) );
        assertThat( edges ).noneMatch( isEdgeWithFromAndTo( "Baz", "Baz" ) );
    }
}
