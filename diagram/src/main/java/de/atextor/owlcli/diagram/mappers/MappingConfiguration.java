package de.atextor.owlcli.diagram.mappers;

import de.atextor.owlcli.diagram.graph.Graph;
import org.semanticweb.owlapi.model.OWLAnnotationObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAnnotationSubjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataVisitorEx;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;

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

    IdentifierMapper getIdentifierMapper();

    NameMapper getNameMapper();
}
