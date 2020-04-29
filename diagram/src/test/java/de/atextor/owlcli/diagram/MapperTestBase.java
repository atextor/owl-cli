package de.atextor.owlcli.diagram;

import de.atextor.owlcli.diagram.graph.DecoratedEdge;
import de.atextor.owlcli.diagram.graph.Decoration;
import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.mappers.DefaultMappingConfiguration;
import de.atextor.owlcli.diagram.mappers.MappingConfiguration;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Fail.fail;

public class MapperTestBase {
    protected TestIdentifierMapper testIdentifierMapper = new TestIdentifierMapper();
    protected TestNameMapper testNameMapper = new TestNameMapper();

    protected final Predicate<Edge> hasDefaultArrow = edge -> edge.getType().equals( Edge.Type.DEFAULT_ARROW );
    protected final Predicate<Edge> hasHollowArrow = edge -> edge.getType().equals( Edge.Type.HOLLOW_ARROW );
    protected final Predicate<Edge> hasDashedArrow = edge -> edge.getType().equals( Edge.Type.DASHED_ARROW );
    protected final Predicate<Edge> hasNoArrow = edge -> edge.getType().equals( Edge.Type.NO_ARROW );
    final Predicate<Edge> hasDomainDecoration = edge -> edge.view( DecoratedEdge.class )
        .map( decoratedEdge -> decoratedEdge.getDecoration().equals( DecoratedEdge.DOMAIN ) )
        .findFirst()
        .orElse( false );
    final Predicate<Edge> hasRangeDecoration = edge -> edge.view( DecoratedEdge.class )
        .map( decoratedEdge -> decoratedEdge.getDecoration().equals( DecoratedEdge.RANGE ) )
        .findFirst()
        .orElse( false );
    final Predicate<Edge> hasFromBar = edge -> edge.getFrom().getId().equals( "bar" );

    protected MappingConfiguration createTestMappingConfiguration() {
        return DefaultMappingConfiguration.builder()
            .identifierMapper( () -> testIdentifierMapper )
            .nameMapper( () -> testNameMapper )
            .build();
    }

    protected IRI iri( final String element ) {
        return IRI.create( "http://test.de#" + element );
    }

    protected OWLOntology createOntology( final String content ) {
        final OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();

        // Advanced heuristic to find out if Turtle or Functional Syntax
        final String ontologyContent = content.contains( "." ) ? """
            @prefix : <http://test.de#> .
            @prefix owl: <http://www.w3.org/2002/07/owl#> .
            @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
            @prefix xml: <http://www.w3.org/XML/1998/namespace> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
            """ + content : """
            Prefix(:=<http://test.de#>)
            Prefix(owl:=<http://www.w3.org/2002/07/owl#>)
            Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)
            Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)
            Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)
            Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)
            Ontology(<http://test.de>
            """ + content + """
            )""";
        final OWLOntology ontology;

        try {
            final InputStream stream = new ByteArrayInputStream( ontologyContent.getBytes( StandardCharsets.UTF_8 ) );
            ontology = ontologyManager.loadOntologyFromOntologyDocument( stream );
            return ontology;
        } catch ( final OWLOntologyCreationException e ) {
            fail( "Could not create ontology", e );
        }
        return null;
    }

    @SuppressWarnings( "unchecked" )
    protected <T extends OWLAxiom> T getAxiom( final String content ) {
        return (T) createOntology( content ).axioms().findFirst().get();
    }

    @SuppressWarnings( "unchecked" )
    protected <T extends OWLAxiom> T getAxiom( final String content, final AxiomType<T> axiomType ) {
        return (T) createOntology( content ).axioms().filter( axiom -> axiom.isOfType( axiomType ) ).findAny().get();
    }

    protected List<Edge> edges( final List<GraphElement> elements ) {
        return elements.stream()
            .filter( GraphElement::isEdge )
            .map( GraphElement::asEdge )
            .collect( Collectors.toList() );
    }

    protected List<Edge> edges( final Set<GraphElement> elements ) {
        return edges( new ArrayList<>( elements ) );
    }

    protected List<Node> nodes( final List<GraphElement> elements ) {
        return elements.stream()
            .filter( GraphElement::isNode )
            .map( GraphElement::asNode )
            .collect( Collectors.toList() );
    }

    protected List<Node> nodes( final Set<GraphElement> elements ) {
        return nodes( new ArrayList<>( elements ) );
    }

    protected Predicate<Node> isNodeWithId( final String targetId ) {
        return node -> node.getId().getId().equals( targetId );
    }

    protected final Predicate<Node> isInvisible = node -> node.is( Node.Invisible.class );

    protected final Predicate<Node> isComplement = node -> node.is( Node.Complement.class );

    protected Predicate<Node> isNodeWithId( final Node.Id targetId ) {
        return isNodeWithId( targetId.getId() );
    }

    protected Predicate<Edge> isEdgeWithFromAndTo( final String fromId, final String toId ) {
        return edge -> edge.getFrom().getId().equals( fromId )
            && edge.getTo().getId().equals( toId );
    }

    protected Predicate<Edge> isEdgeWithFromAndToAndDecoration( final String fromId, final String toId,
                                                                final Decoration decoration ) {
        return edge -> edge.getFrom().getId().equals( fromId )
            && edge.getTo().getId().equals( toId )
            && ( (DecoratedEdge) edge ).getDecoration().equals( decoration );
    }

    protected Predicate<Edge> isEdgeWithFromAndTo( final Node.Id fromId, final Node.Id toId ) {
        return isEdgeWithFromAndTo( fromId.getId(), toId.getId() );
    }

    protected Predicate<Edge> isEdgeWithFrom( final String iri ) {
        return edge -> edge.getFrom().getIri().map( theIri -> theIri.equals( iri( iri ) ) ).orElse( false );
    }

    protected Predicate<Edge> isEdgeWithTo( final String iri ) {
        return edge -> edge.getTo().getIri().map( theIri -> theIri.equals( iri( iri ) ) ).orElse( false );
    }
}
