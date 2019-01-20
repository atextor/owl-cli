package de.atextor.owldiagram.mappers;

import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDataVisitorEx;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLLiteral;

import static io.vavr.API.TODO;

public class OWLDataMapper implements OWLDataVisitorEx<MappingResult> {
    @Override
    public MappingResult visit( final OWLDataComplementOf node ) {
        return TODO();
    }

    @Override
    public MappingResult visit( final OWLDataOneOf node ) {
        return TODO();
    }

    @Override
    public MappingResult visit( final OWLDataIntersectionOf node ) {
        return TODO();
    }

    @Override
    public MappingResult visit( final OWLDataUnionOf node ) {
        return TODO();
    }

    @Override
    public MappingResult visit( final OWLDatatypeRestriction node ) {
        return TODO();
    }

    @Override
    public MappingResult visit( final OWLFacetRestriction node ) {
        return TODO();
    }

    @Override
    public MappingResult visit( final OWLDatatype node ) {
        return TODO();
    }

    @Override
    public MappingResult visit( final OWLLiteral node ) {
        return TODO();
    }
}
