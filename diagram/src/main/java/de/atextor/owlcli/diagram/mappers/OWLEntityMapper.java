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
        final Node node = new Class( mappingConfig.getIdentifierMapper().getIdForIri( classExpression.getIRI() ),
            mappingConfig.getNameMapper().getName( classExpression ) );
        return Graph.of( node );
    }

    @Override
    public Graph visit( final @Nonnull OWLDatatype dataType ) {
        final Node node = new Datatype( mappingConfig.getIdentifierMapper().getIdForIri( dataType.getIRI() ),
            mappingConfig.getNameMapper().getName( dataType ) );
        return Graph.of( node );
    }

    @Override
    public Graph visit( final @Nonnull OWLNamedIndividual individual ) {
        return individual.accept( mappingConfig.getOwlIndividualMapper() );
    }

    @Override
    public Graph visit( final @Nonnull OWLObjectProperty property ) {
        final Node node = new ObjectProperty( mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() ),
            mappingConfig.getNameMapper().getName( property ) );
        return Graph.of( node );
    }

    @Override
    public Graph visit( final @Nonnull OWLDataProperty property ) {
        final Node node = new DataProperty( mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() ),
            mappingConfig.getNameMapper().getName( property ) );
        return Graph.of( node );
    }

    @Override
    public Graph visit( final @Nonnull OWLAnnotationProperty property ) {
        final Node node = new AnnotationProperty( mappingConfig.getIdentifierMapper().getIdForIri( property.getIRI() ),
            mappingConfig.getNameMapper().getName( property ) );
        return Graph.of( node );
    }
}
