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

package de.atextor.ret.diagram.owl.printers;

import de.atextor.ret.diagram.owl.mappers.MappingConfiguration;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;

import javax.annotation.Nonnull;

/**
 * Serializes {@link org.semanticweb.owlapi.model.OWLPropertyExpression}s into expressions
 */
public class OWLPropertyExpressionPrinter implements OWLPropertyExpressionVisitorEx<String> {
    private final MappingConfiguration mappingConfig;

    public OWLPropertyExpressionPrinter( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public String visit( final @Nonnull OWLObjectInverseOf property ) {
        return String.format( "inverse(%s)", property.getInverse().accept( this ) );
    }

    @Override
    public String visit( final @Nonnull OWLObjectProperty property ) {
        return mappingConfig.getNameMapper().getName( property );
    }

    @Override
    public String visit( final @Nonnull OWLDataProperty property ) {
        return mappingConfig.getNameMapper().getName( property );
    }

    @Override
    public String visit( final @Nonnull OWLAnnotationProperty property ) {
        return mappingConfig.getNameMapper().getName( property );
    }
}
