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
import cool.rdf.diagram.owl.graph.node.Class;
import cool.rdf.diagram.owl.graph.node.Individual;
import cool.rdf.diagram.owl.graph.node.PropertyMarker;
import cool.rdf.diagram.owl.mappers.DefaultMappingConfiguration;
import cool.rdf.diagram.owl.mappers.MappingConfiguration;
import cool.rdf.diagram.owl.mappers.OWLOntologyMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class OWLOntologyMapperTest extends MapperTestBase {
    private final MappingConfiguration mappingConfiguration = DefaultMappingConfiguration.builder().build();

    private final OWLOntologyMapper mapper = new OWLOntologyMapper( mappingConfiguration );

    @Test
    public void testOWLAnnotationPropertyDomainAxiom() {
        final String ontology = """
            :foo a owl:Class .
            :bar a owl:AnnotationProperty ;
               rdfs:domain :foo .
            """;

        final Set<GraphElement> result = mapper.apply( createOntology( ontology ) );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( node -> node.is( Class.class ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 1 );

        final Edge propertyToDomain = edges.iterator().next();
        assertThat( propertyToDomain.getType() ).isEqualTo( Edge.Type.DEFAULT_ARROW );
        assertThat( propertyToDomain.getClass() ).isEqualTo( Edge.Decorated.class );
        assertThat( ( (Edge.Decorated) propertyToDomain ).getLabel() ).isEqualTo( Edge.Decorated.Label.DOMAIN );
        assertThat( propertyToDomain.getTo().getId() ).isEqualTo( nodes.stream()
            .filter( node -> node.is( Class.class ) ).map( Node::getId ).findFirst().get() );
    }

    @Test
    public void testOWLAnnotationPropertyDomainAxiomWithPunning() {
        final String ontology = """
            :foo a owl:Class .
            :foo a owl:NamedIndividual .
            :bar a owl:AnnotationProperty ;
               rdfs:domain :foo .
            """;

        final Set<GraphElement> result = mapper.apply( createOntology( ontology ) );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 3 );
        assertThat( nodes ).anyMatch( node -> node.is( Class.class ) );
        assertThat( nodes ).anyMatch( node -> node.is( Individual.class ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 2 );

        assertThat( edges ).anyMatch( hasDefaultArrow.and( hasDomainLabel ).and( hasFromBar ).and( edge ->
            nodes.stream().anyMatch( node ->
                node.is( Class.class ) && node.equals( edge.getTo() ) ) ) );

        assertThat( edges ).anyMatch( hasDefaultArrow.and( hasDomainLabel ).and( hasFromBar ).and( edge ->
            nodes.stream().anyMatch( node ->
                node.is( Individual.class ) && node.equals( edge.getTo() ) ) ) );
    }

    @Test
    public void testOWLAnnotationPropertyRangeAxiom() {
        final String ontology = """
            :foo a owl:Class .
            :bar a owl:AnnotationProperty ;
               rdfs:range :foo .
            """;

        final Set<GraphElement> result = mapper.apply( createOntology( ontology ) );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( node -> node.is( Class.class ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 1 );

        final Edge propertyToRange = edges.iterator().next();
        assertThat( propertyToRange.getType() ).isEqualTo( Edge.Type.DEFAULT_ARROW );
        assertThat( propertyToRange.getClass() ).isEqualTo( Edge.Decorated.class );
        assertThat( ( (Edge.Decorated) propertyToRange ).getLabel() ).isEqualTo( Edge.Decorated.Label.RANGE );
        assertThat( propertyToRange.getTo().getId() ).isEqualTo( nodes.stream()
            .filter( node -> node.is( Class.class ) ).map( Node::getId ).findFirst().get() );
    }

    @Test
    public void testOWLAnnotationPropertyRangeAxiomWithPunning() {
        final String ontology = """
            :foo a owl:Class .
            :foo a owl:NamedIndividual .
            :bar a owl:AnnotationProperty ;
               rdfs:range :foo .
            """;

        final Set<GraphElement> result = mapper.apply( createOntology( ontology ) );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 3 );
        assertThat( nodes ).anyMatch( node -> node.is( Class.class ) );
        assertThat( nodes ).anyMatch( node -> node.is( Individual.class ) );
        assertThat( nodes ).anyMatch( isNodeWithId( "bar" ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 2 );

        assertThat( edges ).anyMatch( hasDefaultArrow.and( hasRangeLabel ).and( hasFromBar ).and( edge ->
            nodes.stream().anyMatch( node ->
                node.is( Class.class ) && node.equals( edge.getTo() ) ) ) );

        assertThat( edges ).anyMatch( hasDefaultArrow.and( hasRangeLabel ).and( hasFromBar ).and( edge ->
            nodes.stream().anyMatch( node ->
                node.is( Individual.class ) && node.equals( edge.getTo() ) ) ) );
    }

    @Test
    public void testOWLAnnotationAssertionAxiom() {
        final String ontology = """
            :foo a owl:AnnotationProperty .
            :Baz a owl:Class .
            :bar a owl:NamedIndividual ;
               :foo :Baz .
            """;

        final Set<GraphElement> result = mapper.apply( createOntology( ontology ) );
        assertThat( result ).isNotEmpty();

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 4 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( node -> node.is( Class.class ) );
        assertThat( nodes ).anyMatch( node -> node.is( Individual.class ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 3 );
        assertThat( edges ).anyMatch( hasNoArrow.and( isEdgeWithFrom( "bar" ) ) );
        assertThat( edges ).anyMatch( hasDashedArrow.and( isEdgeWithTo( "foo" ) ) );
        assertThat( edges ).anyMatch( hasDefaultArrow.and( isEdgeWithTo( "Baz" ) ) );
    }

    @Test
    public void testMultipleObjectPropertyCharacteristics() {
        final String ontology = """
            :foo a owl:ObjectProperty,
                   owl:FunctionalProperty,
                   owl:TransitiveProperty,
                   owl:IrreflexiveProperty .
            """;

        final Set<GraphElement> result = mapper.apply( createOntology( ontology ) );
        assertThat( result ).hasSize( 3 );

        final List<Node> nodes = nodes( result );
        assertThat( nodes ).hasSize( 2 );
        assertThat( nodes ).anyMatch( isNodeWithId( "foo" ) );
        assertThat( nodes ).anyMatch( node -> node.view( PropertyMarker.class ).map( propertyMarker ->
            propertyMarker.getKind().contains( PropertyMarker.Kind.FUNCTIONAL ) &&
                propertyMarker.getKind().contains( PropertyMarker.Kind.TRANSITIVE ) &&
                propertyMarker.getKind().contains( PropertyMarker.Kind.IRREFLEXIVE )
        ).findFirst().orElse( false ) );

        final List<Edge> edges = edges( result );
        assertThat( edges ).hasSize( 1 );

        assertThat( edges ).anyMatch( isEdgeWithFrom( "foo" ).and( hasDashedArrow ) );
    }

}
