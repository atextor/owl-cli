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

import de.atextor.ret.diagram.owl.graph.Graph;
import org.semanticweb.owlapi.model.OWLAnnotationObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAnnotationSubjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataVisitorEx;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;
import org.semanticweb.owlapi.model.SWRLObjectVisitorEx;

/**
 * Captures the different parts of the ontology-to-graph mapping operation
 */
public interface MappingConfiguration {
    OWLAxiomVisitorEx<Graph> getOwlAxiomMapper();

    OWLClassExpressionVisitorEx<Graph> getOwlClassExpressionMapper();

    OWLIndividualVisitorEx<Graph> getOwlIndividualMapper();

    OWLPropertyExpressionVisitorEx<Graph> getOwlPropertyExpressionMapper();

    OWLObjectVisitorEx<Graph> getOwlObjectMapper();

    OWLDataVisitorEx<Graph> getOwlDataMapper();

    OWLEntityVisitorEx<Graph> getOwlEntityMapper();

    OWLAnnotationObjectVisitorEx<Graph> getOwlAnnotationObjectMapper();

    OWLAnnotationSubjectVisitorEx<Graph> getOwlAnnotationSubjectMapper();

    SWRLObjectVisitorEx<Graph> getSwrlObjectMapper();

    IdentifierMapper getIdentifierMapper();

    NameMapper getNameMapper();

    OWLDataVisitorEx<String> getOwlDataPrinter();

    OWLPropertyExpressionVisitorEx<String> getOwlPropertyExpressionPrinter();

    OWLIndividualVisitorEx<String> getOwlIndividualPrinter();

    OWLClassExpressionVisitorEx<String> getOwlClassExpressionPrinter();
}
