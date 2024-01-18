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

package cool.rdf.ret.diagram.owl.printers;

import cool.rdf.ret.diagram.owl.mappers.MappingConfiguration;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;

/**
 * Serializes {@link org.semanticweb.owlapi.model.OWLIndividual}s into expressions
 */
public class OWLIndividualPrinter implements OWLIndividualVisitorEx<String> {
    final MappingConfiguration mappingConfiguration;

    /**
     * Creates a new individual printer from a given mapping config
     *
     * @param mappingConfiguration the config
     */
    public OWLIndividualPrinter( final MappingConfiguration mappingConfiguration ) {
        this.mappingConfiguration = mappingConfiguration;
    }

    @Override
    public String visit( final @Nonnull OWLAnonymousIndividual individual ) {
        return "[]";
    }

    @Override
    public String visit( final @Nonnull OWLNamedIndividual individual ) {
        return mappingConfiguration.getNameMapper().getName( individual );
    }
}
