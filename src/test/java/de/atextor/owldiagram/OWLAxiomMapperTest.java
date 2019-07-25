package de.atextor.owldiagram;

import de.atextor.owldiagram.graph.Edge;
import de.atextor.owldiagram.graph.GraphElement;
import de.atextor.owldiagram.graph.Node;
import de.atextor.owldiagram.mappers.DefaultMappingConfiguration;
import de.atextor.owldiagram.mappers.IdentifierMapper;
import de.atextor.owldiagram.mappers.MappingConfiguration;
import de.atextor.owldiagram.mappers.OWLAxiomMapper;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataPropertyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLEquivalentClassesAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLEquivalentDataPropertiesAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLEquivalentObjectPropertiesAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class OWLAxiomMapperTest extends MapperTestBase {
    private final MappingConfiguration mappingConfiguration = DefaultMappingConfiguration.builder().build();
    private final OWLAxiomMapper mapper = new OWLAxiomMapper( mappingConfiguration );

    @Test
    public void testOWLSubClassOfAxiom() {
        final OWLSubClassOfAxiom axiom = getAxiom( ":Foo rdfs:subClassOf :Bar ." );
        final List<GraphElement> result = mapper.visit( axiom ).collect( Collectors.toList() );
        assertThat( result ).hasSize( 3 );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "Foo" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "Bar" ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 1 );

        final Edge theEdge = edges.get( 0 );
        assertThat( theEdge ).matches( isEdgeWithFromAndTo( "Foo", "Bar" ) );
        assertThat( theEdge.getType() ).isEqualTo( Edge.Type.HOLLOW_ARROW );
    }

    @Test
    public void testOWLDeclarationAxiom() {
        final OWLDeclarationAxiom axiom = getAxiom( ":Foo a owl:Class ." );
        final List<GraphElement> result = mapper.visit( axiom ).collect( Collectors.toList() );
        assertThat( result ).hasSize( 1 );

        final Node theNode = nodes( result ).get( 0 );
        assertThat( theNode ).matches( isNodeWithId( "Foo" ) );
    }

    @Test
    public void testOWLClassAssertionAxiom() {
        final OWLClassAssertionAxiom axiom = getAxiom( ":Foo a owl:Thing ." );
        final List<GraphElement> result = mapper.visit( axiom ).collect( Collectors.toList() );
        assertThat( result ).hasSize( 3 );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "Foo" ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "Thing" ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 1 );

        final Edge theEdge = edges.get( 0 );
        assertThat( theEdge ).matches( isEdgeWithFromAndTo( "Foo", "Thing" ) );
        assertThat( theEdge.getType() ).isEqualTo( Edge.Type.DEFAULT_ARROW );
    }

    private void assertEquivalentResult( final List<GraphElement> result, final IRI fooIri, final IRI barIri,
                                         final IRI bazIri ) {
        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 3 );

        final IdentifierMapper identifierMapper = mappingConfiguration.getIdentifierMapper();
        final Node.Id foo = identifierMapper.getIdForIri( fooIri );
        final Node.Id bar = identifierMapper.getIdForIri( barIri );
        final Node.Id baz = identifierMapper.getIdForIri( bazIri );

        assertThat( nodes ).anyMatch( isNodeWithId( foo ) );
        assertThat( nodes ).anyMatch( isNodeWithId( bar ) );
        assertThat( nodes ).anyMatch( isNodeWithId( baz ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 3 );

        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( bar, foo ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( baz, foo ) );
        assertThat( edges ).anyMatch( isEdgeWithFromAndTo( bar, baz ) );

        assertThat( edges ).noneMatch( isEdgeWithFromAndTo( foo, bar ) );
        assertThat( edges ).noneMatch( isEdgeWithFromAndTo( foo, baz ) );
        assertThat( edges ).noneMatch( isEdgeWithFromAndTo( baz, bar ) );
        assertThat( edges ).noneMatch( isEdgeWithFromAndTo( foo, foo ) );
        assertThat( edges ).noneMatch( isEdgeWithFromAndTo( bar, bar ) );
        assertThat( edges ).noneMatch( isEdgeWithFromAndTo( baz, baz ) );
    }

    @Test
    public void testOWLEquivalentClassesAxiom() {
        final IRI fooIri = IRI.create( "http://test.de#Foo" );
        final IRI barIri = IRI.create( "http://test.de#Bar" );
        final IRI bazIri = IRI.create( "http://test.de#Baz" );
        final OWLClassExpression classExpression1 = new OWLClassImpl( fooIri );
        final OWLClassExpression classExpression2 = new OWLClassImpl( barIri );
        final OWLClassExpression classExpression3 = new OWLClassImpl( bazIri );
        final OWLEquivalentClassesAxiom axiom = new OWLEquivalentClassesAxiomImpl( Arrays.asList( classExpression1,
                classExpression2, classExpression3 ), Collections.emptyList() );
        final List<GraphElement> result = mapper.visit( axiom ).collect( Collectors.toList() );

        assertEquivalentResult( result, fooIri, barIri, bazIri );
    }

    @Test
    public void testOWLEquivalentDataPropertiesAxiom() {
        final IRI fooIri = IRI.create( "http://test.de#foo" );
        final IRI barIri = IRI.create( "http://test.de#bar" );
        final IRI bazIri = IRI.create( "http://test.de#baz" );
        final OWLDataPropertyExpression dataPropertyExpression1 = new OWLDataPropertyImpl( fooIri );
        final OWLDataPropertyExpression dataPropertyExpression2 = new OWLDataPropertyImpl( barIri );
        final OWLDataPropertyExpression dataPropertyExpression3 = new OWLDataPropertyImpl( bazIri );
        final OWLEquivalentDataPropertiesAxiom axiom =
                new OWLEquivalentDataPropertiesAxiomImpl( Arrays.asList( dataPropertyExpression1,
                        dataPropertyExpression2, dataPropertyExpression3 ), Collections.emptyList() );

        final List<GraphElement> result = mapper.visit( axiom ).collect( Collectors.toList() );

        assertEquivalentResult( result, fooIri, barIri, bazIri );
    }

    @Test
    public void testOWLEquivalentObjectPropertiesAxiom() {
        final IRI fooIri = IRI.create( "http://test.de#foo" );
        final IRI barIri = IRI.create( "http://test.de#bar" );
        final IRI bazIri = IRI.create( "http://test.de#baz" );
        final OWLObjectPropertyExpression dataPropertyExpression1 = new OWLObjectPropertyImpl( fooIri );
        final OWLObjectPropertyExpression dataPropertyExpression2 = new OWLObjectPropertyImpl( barIri );
        final OWLObjectPropertyExpression dataPropertyExpression3 = new OWLObjectPropertyImpl( bazIri );
        final OWLEquivalentObjectPropertiesAxiom axiom =
                new OWLEquivalentObjectPropertiesAxiomImpl( Arrays.asList( dataPropertyExpression1,
                        dataPropertyExpression2, dataPropertyExpression3 ), Collections.emptyList() );

        final List<GraphElement> result = mapper.visit( axiom ).collect( Collectors.toList() );

        assertEquivalentResult( result, fooIri, barIri, bazIri );
    }
}
