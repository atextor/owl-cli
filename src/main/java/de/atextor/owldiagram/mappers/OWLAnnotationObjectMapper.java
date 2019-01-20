package de.atextor.owldiagram.mappers;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;

import static io.vavr.API.TODO;

public class OWLAnnotationObjectMapper implements OWLAnnotationObjectVisitorEx<MappingResult> {
    @Override
    public MappingResult visit( final OWLAnnotation node ) {
        return TODO();
    }

    @Override
    public MappingResult visit( final OWLAnnotationAssertionAxiom axiom ) {
        return TODO();
    }

    @Override
    public MappingResult visit( final OWLSubAnnotationPropertyOfAxiom axiom ) {
        return TODO();
    }

    @Override
    public MappingResult visit( final OWLAnnotationPropertyDomainAxiom axiom ) {
        return TODO();
    }

    @Override
    public MappingResult visit( final OWLAnnotationPropertyRangeAxiom axiom ) {
        return TODO();
    }

    @Override
    public MappingResult visit( final IRI iri ) {
        return TODO();
    }

    @Override
    public MappingResult visit( final OWLAnonymousIndividual individual ) {
        return TODO();
    }

    @Override
    public MappingResult visit( final OWLLiteral node ) {
        return TODO();
    }
}
