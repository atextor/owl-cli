/*
 * This file is part of OWL-CLI.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright (c) 2020, Andreas Textor.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www
 * .gnu.org/licenses/.
 */

package de.atextor.owlcli.diagram.mappers;

import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.Graph;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDataVisitorEx;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLLiteral;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vavr.API.TODO;

/**
 * Maps OWL Data objects to {@link Graph}s
 */
public class OWLDataMapper implements OWLDataVisitorEx<Graph> {
    private final MappingConfiguration mappingConfig;

    public OWLDataMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    private Stream<GraphElement> createEdgeToDataRange( final Node sourceNode,
                                                        final OWLDataRange classExpression ) {
        final Graph diagramPartsForDataRange = classExpression.accept( this );
        final Node targetNode = diagramPartsForDataRange.getNode();
        final Stream<GraphElement> remainingElements = diagramPartsForDataRange.getOtherElements();
        final Node.Id from = sourceNode.getId();
        final Node.Id to = targetNode.getId();
        final Edge operandEdge = new Edge.Plain( Edge.Type.DEFAULT_ARROW, from, to );

        return Stream.concat( Stream.of( sourceNode, targetNode, operandEdge ), remainingElements );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataComplementOf dataRange ) {
        final Node complementNode =
            new Node.Complement( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final Stream<GraphElement> remainingElements = createEdgeToDataRange( complementNode,
            dataRange.getDataRange() );
        return Graph.of( complementNode, remainingElements );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataOneOf dataRange ) {
        final Node restrictionNode =
            new Node.ClosedClass( mappingConfig.getIdentifierMapper().getSyntheticId() );
        return dataRange.values().map( value -> {
            final Graph valueGraph = value.accept( mappingConfig.getOwlDataMapper() );
            final Edge vEdge = new Edge.Plain( Edge.Type.DEFAULT_ARROW, restrictionNode.getId(),
                valueGraph.getNode().getId() );
            return valueGraph.and( vEdge );
        } ).reduce( Graph.of( restrictionNode ), Graph::and );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataIntersectionOf dataRange ) {
        final Node intersectionNode =
            new Node.Intersection( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final Stream<GraphElement> remainingElements = dataRange.operands().flatMap( operand ->
            createEdgeToDataRange( intersectionNode, operand ) );
        return Graph.of( intersectionNode, remainingElements );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataUnionOf dataRange ) {
        final Node unionNode = new Node.Union( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final Stream<GraphElement> remainingElements = dataRange.operands().flatMap( operand ->
            createEdgeToDataRange( unionNode, operand ) );
        return Graph.of( unionNode, remainingElements );
    }

    @Override
    public Graph visit( final @Nonnull OWLDatatypeRestriction restriction ) {
        final String datatypeName = mappingConfig.getNameMapper().getName( restriction.getDatatype() );
        final String restrictionExpression = restriction.facetRestrictions()
            .map( owlFacetRestriction -> owlFacetRestriction.getFacet().getSymbolicForm() + " " + owlFacetRestriction
                .getFacetValue().getLiteral() ).collect( Collectors.joining( ", ", "[", "]" ) );

        final Node typeNode = new Node.Datatype( mappingConfig.getIdentifierMapper().getSyntheticId(),
            datatypeName + " " + restrictionExpression );
        return Graph.of( typeNode );
    }

    @Override
    public Graph visit( final @Nonnull OWLFacetRestriction node ) {
        return TODO();
    }

    @Override
    public Graph visit( final @Nonnull OWLDatatype node ) {
        return node.accept( mappingConfig.getOwlEntityMapper() );
    }

    @Override
    public Graph visit( final @Nonnull OWLLiteral node ) {
        final Node.Id id = mappingConfig.getIdentifierMapper().getSyntheticId();
        return Graph.of( new Node.Literal( id, node.getLiteral() ) );
    }
}
