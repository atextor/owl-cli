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

package de.atextor.ret.diagram.owl.mappers;

import de.atextor.ret.diagram.owl.graph.Edge;
import de.atextor.ret.diagram.owl.graph.Graph;
import de.atextor.ret.diagram.owl.graph.GraphElement;
import de.atextor.ret.diagram.owl.graph.Node;
import de.atextor.ret.diagram.owl.graph.node.ClosedClass;
import de.atextor.ret.diagram.owl.graph.node.Complement;
import de.atextor.ret.diagram.owl.graph.node.Datatype;
import de.atextor.ret.diagram.owl.graph.node.Intersection;
import de.atextor.ret.diagram.owl.graph.node.Literal;
import de.atextor.ret.diagram.owl.graph.node.Union;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDataVisitorEx;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLLiteral;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

/**
 * Maps OWL Data objects to {@link Graph}s
 */
public class OWLDataMapper implements OWLDataVisitorEx<Graph> {
    private final MappingConfiguration mappingConfig;

    /**
     * Creates a new data mapper from a given mapping config
     *
     * @param mappingConfig the config
     */
    public OWLDataMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    private Stream<GraphElement> createEdgeToDataRange( final Node sourceNode,
        final OWLDataRange classExpression ) {
        final Graph diagramPartsForDataRange = classExpression.accept( this );
        final Node targetNode = diagramPartsForDataRange.getNode();
        final Stream<GraphElement> remainingElements = diagramPartsForDataRange.getOtherElements();
        final Edge operandEdge = new Edge.Plain( Edge.Type.DEFAULT_ARROW, sourceNode, targetNode );

        return Stream.concat( Stream.of( sourceNode, targetNode, operandEdge ), remainingElements );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataComplementOf dataRange ) {
        final Node complementNode =
            new Complement( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final Stream<GraphElement> remainingElements = createEdgeToDataRange( complementNode,
            dataRange.getDataRange() );
        return Graph.of( complementNode, remainingElements );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataOneOf dataRange ) {
        final Node restrictionNode =
            new ClosedClass( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return dataRange.values().map( value -> {
            final Graph valueGraph = value.accept( mappingConfig.getOwlDataMapper() );
            final Edge vEdge = new Edge.Plain( Edge.Type.DEFAULT_ARROW, restrictionNode, valueGraph.getNode() );
            return valueGraph.and( vEdge );
        } ).reduce( Graph.of( restrictionNode ), Graph::and );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataIntersectionOf dataRange ) {
        final Node intersectionNode =
            new Intersection( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final Stream<GraphElement> remainingElements = dataRange.operands().flatMap( operand ->
            createEdgeToDataRange( intersectionNode, operand ) );
        return Graph.of( intersectionNode, remainingElements );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataUnionOf dataRange ) {
        final Node unionNode = new Union( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final Stream<GraphElement> remainingElements = dataRange.operands().flatMap( operand ->
            createEdgeToDataRange( unionNode, operand ) );
        return Graph.of( unionNode, remainingElements );
    }

    @Override
    public Graph visit( final @Nonnull OWLDatatypeRestriction restriction ) {
        final Node typeNode = new Datatype( mappingConfig.getIdentifierMapper().getSyntheticId(),
            restriction.accept( mappingConfig.getOwlDataPrinter() ) );
        return Graph.of( typeNode );
    }

    @Override
    public Graph visit( final @Nonnull OWLDatatype node ) {
        return node.accept( mappingConfig.getOwlEntityMapper() );
    }

    @Override
    public Graph visit( final @Nonnull OWLLiteral node ) {
        final Node.Id id = mappingConfig.getIdentifierMapper().getSyntheticId();
        return Graph.of( new Literal( id, node.getLiteral() ) );
    }
}
