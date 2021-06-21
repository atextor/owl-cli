/*
 * Copyright 2021 Andreas Textor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.atextor.owlcli.diagram.mappers;

import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.Graph;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.node.AnnotationProperty;
import de.atextor.owlcli.diagram.graph.node.DataProperty;
import de.atextor.owlcli.diagram.graph.node.Inverse;
import de.atextor.owlcli.diagram.graph.node.ObjectProperty;
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
            new Inverse( mappingConfig.getIdentifierMapper().getSyntheticId() );
        final OWLPropertyExpression invertedProperty = property.getInverseProperty();
        final Graph propertyVisitorGraph =
            invertedProperty.accept( mappingConfig.getOwlPropertyExpressionMapper() );
        final Edge propertyEdge = new Edge.Plain( Edge.Type.DEFAULT_ARROW, inverseNode, propertyVisitorGraph
            .getNode() );
        return Graph.of( inverseNode ).and( propertyVisitorGraph ).and( propertyEdge );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectProperty property ) {
        final Node.Id id = mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() );
        final String label = mappingConfig.getNameMapper().getName( property );
        final Node node = new ObjectProperty( id, label );
        return Graph.of( node );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataProperty property ) {
        final Node.Id id = mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() );
        final String label = mappingConfig.getNameMapper().getName( property );
        final Node node = new DataProperty( id, label );
        return Graph.of( node );
    }

    @Override
    public Graph visit( final @Nonnull OWLAnnotationProperty property ) {
        final Node.Id id = mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() );
        final String label = mappingConfig.getNameMapper().getName( property );
        final Node node = new AnnotationProperty( id, label );
        return Graph.of( node );
    }
}
