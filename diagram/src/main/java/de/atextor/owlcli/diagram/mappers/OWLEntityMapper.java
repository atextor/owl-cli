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

import de.atextor.owlcli.diagram.graph.Graph;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.node.AnnotationProperty;
import de.atextor.owlcli.diagram.graph.node.Class;
import de.atextor.owlcli.diagram.graph.node.DataProperty;
import de.atextor.owlcli.diagram.graph.node.Datatype;
import de.atextor.owlcli.diagram.graph.node.ObjectProperty;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.annotation.Nonnull;

/**
 * Maps {@link org.semanticweb.owlapi.model.OWLEntity}s to {@link Graph}s
 */
public class OWLEntityMapper implements OWLEntityVisitorEx<Graph> {
    private final MappingConfiguration mappingConfig;

    public OWLEntityMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public Graph visit( final @Nonnull OWLClass classExpression ) {
        final Node node =
            new Class( mappingConfig.getIdentifierMapper().getIdForIri( classExpression.getIRI() ),
                mappingConfig.getNameMapper().getName( classExpression ) );
        return Graph.of( node );
    }

    @Override
    public Graph visit( final @Nonnull OWLDatatype dataType ) {
        final Node node =
            new Datatype( mappingConfig.getIdentifierMapper().getIdForIri( dataType.getIRI() ),
                mappingConfig.getNameMapper().getName( dataType ) );
        return Graph.of( node );
    }

    @Override
    public Graph visit( final @Nonnull OWLNamedIndividual individual ) {
        return individual.accept( mappingConfig.getOwlIndividualMapper() );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectProperty property ) {
        final Node node =
            new ObjectProperty( mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() ),
                mappingConfig.getNameMapper().getName( property ) );
        return Graph.of( node );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataProperty property ) {
        final Node node =
            new DataProperty( mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() ),
                mappingConfig.getNameMapper().getName( property ) );
        return Graph.of( node );
    }

    @Override
    public Graph visit( final @Nonnull OWLAnnotationProperty property ) {
        final Node node =
            new AnnotationProperty( mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() ),
                mappingConfig.getNameMapper().getName( property ) );
        return Graph.of( node );
    }
}
