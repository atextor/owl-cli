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
import de.atextor.owlcli.diagram.graph.Node;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;

import javax.annotation.Nonnull;

/**
 * Maps {@link OWLPropertyExpression}s to {@link Graph}s
 */
public class OWLPropertyExpressionMapper implements OWLPropertyExpressionVisitorEx<Graph> {
    private final MappingConfiguration mappingConfig;

    public OWLPropertyExpressionMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectInverseOf property ) {
        final Node inverseNode =
            new Node.Inverse( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final OWLPropertyExpression invertedProperty = property.getInverseProperty();
        final Graph propertyVisitorGraph =
            invertedProperty.accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Edge propertyEdge = new Edge.Plain( Edge.Type.DEFAULT_ARROW, inverseNode.getId(),
            propertyVisitorGraph.getNode().getId() );
        return Graph.of( inverseNode ).and( propertyVisitorGraph ).and( propertyEdge );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectProperty property ) {
        final Node.Id id = mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() );
        final String label = id.getId();
        final Node node = new Node.ObjectProperty( id, label );
        return Graph.of( node );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataProperty property ) {
        final Node.Id id = mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() );
        final String label = id.getId();
        final Node node = new Node.DataProperty( id, label );
        return Graph.of( node );
    }

    @Override
    public Graph visit( final @Nonnull OWLAnnotationProperty property ) {
        final Node.Id id = mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() );
        final String label = id.getId();
        final Node node = new Node.AnnotationProperty( id, label );
        return Graph.of( node );
    }
}
