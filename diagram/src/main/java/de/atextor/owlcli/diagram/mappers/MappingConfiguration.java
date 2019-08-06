package de.atextor.owlcli.diagram.mappers;

import de.atextor.owlcli.diagram.graph.GraphElement;
import org.semanticweb.owlapi.model.OWLAnnotationObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataVisitorEx;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;

import java.util.stream.Stream;

public interface MappingConfiguration {
    OWLAxiomVisitorEx<Stream<GraphElement>> getOwlAxiomMapper();

    OWLClassExpressionVisitorEx<Result> getOwlClassExpressionMapper();

    OWLIndividualVisitorEx<Result> getOwlIndividualMapper();

    OWLPropertyExpressionVisitorEx<Result> getOwlPropertyExpressionMapper();

    OWLObjectVisitorEx<Result> getOwlObjectMapper();

    OWLDataVisitorEx<Result> getOwlDataMapper();

    OWLEntityVisitorEx<Result> getOwlEntityMapper();

    OWLAnnotationObjectVisitorEx<Result> getOwlAnnotationObjectMapper();

    IdentifierMapper getIdentifierMapper();

    NameMapper getNameMapper();
}
