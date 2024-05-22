/*
 * Copyright 2024 Andreas Textor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cool.rdf.diagram.owl;

import cool.rdf.diagram.owl.graph.Edge;
import cool.rdf.diagram.owl.graph.GraphElement;
import cool.rdf.diagram.owl.graph.Node;
import cool.rdf.diagram.owl.graph.node.Complement;
import cool.rdf.diagram.owl.graph.node.Invisible;
import cool.rdf.diagram.owl.mappers.DefaultMappingConfiguration;
import cool.rdf.diagram.owl.mappers.MappingConfiguration;
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
    protected final TestIdentifierMapper testIdentifierMapper = new TestIdentifierMapper();

    protected final TestNameMapper testNameMapper = new TestNameMapper();

    protected final Predicate<Edge> hasDefaultArrow = edge -> edge.getType().equals( Edge.Type.DEFAULT_ARROW );

    protected final Predicate<Edge> hasHollowArrow = edge -> edge.getType().equals( Edge.Type.HOLLOW_ARROW );

    protected final Predicate<Edge> hasDashedArrow = edge -> edge.getType().equals( Edge.Type.DASHED_ARROW );

    protected final Predicate<Edge> hasNoArrow = edge -> edge.getType().equals( Edge.Type.NO_ARROW );

    final Predicate<Edge> hasDomainLabel = edge -> edge.view( Edge.Decorated.class )
        .map( decoratedEdge -> decoratedEdge.getLabel().equals( Edge.Decorated.Label.DOMAIN ) )
        .findFirst()
        .orElse( false );

    final Predicate<Edge> hasRangeLabel = edge -> edge.view( Edge.Decorated.class )
        .map( decoratedEdge -> decoratedEdge.getLabel().equals( Edge.Decorated.Label.RANGE ) )
        .findFirst()
        .orElse( false );

    final Predicate<Edge> hasFromBar = edge -> edge.getFrom().getId().getId().equals( "bar" );

    protected MappingConfiguration createTestMappingConfiguration() {
        return DefaultMappingConfiguration.builder()
            .identifierMapper( testIdentifierMapper )
            .nameMapper( testNameMapper )
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
            @prefix swrl: <http://www.w3.org/2003/11/swrl#> .
            @prefix swrlb: <http://www.w3.org/2003/11/swrlb#> .
            @prefix var: <urn:swrl:var#> .
            """ + content : """
            Prefix(:=<http://test.de#>)
            Prefix(owl:=<http://www.w3.org/2002/07/owl#>)
            Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)
            Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)
            Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)
            Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)
            Prefix(swrl:=<http://www.w3.org/2003/11/swrl#>)
            Prefix(swrlb:=<http://www.w3.org/2003/11/swrlb#>)
            Prefix(var:=<urn:swrl:var#>)
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

    protected final Predicate<Node> isInvisible = node -> node.is( Invisible.class );

    protected final Predicate<Node> isComplement = node -> node.is( Complement.class );

    protected Predicate<Node> isNodeWithId( final Node.Id targetId ) {
        return isNodeWithId( targetId.getId() );
    }

    protected Predicate<Edge> isEdgeWithFromAndTo( final String fromId, final String toId ) {
        return edge -> edge.getFrom().getId().getId().equals( fromId )
            && edge.getTo().getId().getId().equals( toId );
    }

    protected Predicate<Edge> isEdgeWithFromAndToAndLabel( final String fromId, final String toId,
        final Edge.Decorated.Label label ) {
        return edge -> edge.getFrom().getId().getId().equals( fromId )
            && edge.getTo().getId().getId().equals( toId )
            && ( (Edge.Decorated) edge ).getLabel().equals( label );
    }

    protected Predicate<Edge> isEdgeWithFromAndTo( final Node.Id fromId, final Node.Id toId ) {
        return isEdgeWithFromAndTo( fromId.getId(), toId.getId() );
    }

    protected Predicate<Edge> isEdgeWithFrom( final String iri ) {
        return edge -> edge.getFrom().getId().getIri().map( theIri -> theIri.equals( iri( iri ) ) ).orElse( false );
    }

    protected Predicate<Edge> isEdgeWithTo( final String iri ) {
        return edge -> edge.getTo().getId().getIri().map( theIri -> theIri.equals( iri( iri ) ) ).orElse( false );
    }
}
