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

package cool.rdf.diagram.owl.mappers;

import cool.rdf.diagram.owl.graph.Graph;
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
 * Captures the different parts of the ontology-to-{@link Graph} mapping operation
 */
public interface MappingConfiguration {
    /**
     * The OWL axiom mapper translates arbitrary OWL axioms to graphs
     *
     * @return the mapper
     */
    OWLAxiomVisitorEx<Graph> getOwlAxiomMapper();

    /**
     * The OWL class expression mapper translates OWL class expressions (declarations, but also unions, intersections etc.) to graphs
     *
     * @return the mapper
     */
    OWLClassExpressionVisitorEx<Graph> getOwlClassExpressionMapper();

    /**
     * The OWL individual mapper translates OWL individuals to graphs
     *
     * @return the mapper
     */
    OWLIndividualVisitorEx<Graph> getOwlIndividualMapper();

    /**
     * The OWL property expression mapper translates OWL object properties and data properties to graphs
     *
     * @return the mapper
     */
    OWLPropertyExpressionVisitorEx<Graph> getOwlPropertyExpressionMapper();

    /**
     * The OWL object mapper unifies the interfaces for various other mappers, and translates the corresponding OWL objects to graphs
     *
     * @return the mapper
     */
    OWLObjectVisitorEx<Graph> getOwlObjectMapper();

    /**
     * The OWL data mapper translates data axioms, such as data unions and complements, to graphs
     *
     * @return the mapper
     */
    OWLDataVisitorEx<Graph> getOwlDataMapper();

    /**
     * The OWL entity mapper translates structural OWL elements such as classes, properties and individuals, to graphs
     *
     * @return the mapper
     */
    OWLEntityVisitorEx<Graph> getOwlEntityMapper();

    /**
     * The OWL annotation objects mapper translates annotation objects (i.e., annotations) to graphs
     *
     * @return the mapper
     */
    OWLAnnotationObjectVisitorEx<Graph> getOwlAnnotationObjectMapper();

    /**
     * The OWL annotation subject mapper translates annotation subjects (i.e., annotated elements) to graphs
     *
     * @return the mapper
     */
    OWLAnnotationSubjectVisitorEx<Graph> getOwlAnnotationSubjectMapper();

    /**
     * The SWRL object visitor translates SWRL axioms to graphs
     *
     * @return the mapper
     */
    SWRLObjectVisitorEx<Graph> getSwrlObjectMapper();

    /**
     * The identifier mapper creates graph (node and edge) identifiers from input ontology identifiers
     *
     * @return the mapper
     */
    IdentifierMapper getIdentifierMapper();

    /**
     * The name mapper translates ontology element IRIs to graph (node and edge) string labels
     *
     * @return the mapper
     */
    NameMapper getNameMapper();

    /**
     * The OWL data printer translates data axioms, such as data unions and complements, to expression String representations
     *
     * @return the printer
     */
    OWLDataVisitorEx<String> getOwlDataPrinter();

    /**
     * The OWL property expression printer translates OWL object properties and data properties to expression String representations
     *
     * @return the printer
     */
    OWLPropertyExpressionVisitorEx<String> getOwlPropertyExpressionPrinter();

    /**
     * The OWL individual printer translates OWL individuals to expression String representations
     *
     * @return the printer
     */
    OWLIndividualVisitorEx<String> getOwlIndividualPrinter();

    /**
     * The OWL class expression printer translates OWL class expressions to String representations
     *
     * @return the printer
     */
    OWLClassExpressionVisitorEx<String> getOwlClassExpressionPrinter();
}
