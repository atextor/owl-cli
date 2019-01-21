package de.atextor.owldiagram.mappers;

public class Mappers {
    private static final OWLAxiomMapper owlAxiomMapper = new OWLAxiomMapper();
    private static final OWLClassExpressionMapper owlClassExpressionMapper = new OWLClassExpressionMapper();
    private static final OWLIndividualMapper owlIndividualMapper = new OWLIndividualMapper();
    private static final OWLPropertyExpressionMapper owlPropertyExpressionMapper = new OWLPropertyExpressionMapper();
    private static final OWLObjectMapper owlObjectMapper = new OWLObjectMapper();
    private static final OWLDataMapper owlDataMapper = new OWLDataMapper();
    private static final OWLEntityMapper owlEntityMapper = new OWLEntityMapper();
    private static final OWLAnnotationObjectMapper owlAnnotationObjectMapper = new OWLAnnotationObjectMapper();
    private static final IdentifierMapper identifierMapper = new IdentifierMapper();
    private static final NameMapper nameMapper = new NameMapper();

    public static OWLAxiomMapper getOwlAxiomMapper() {
        return owlAxiomMapper;
    }

    public static OWLClassExpressionMapper getOwlClassExpressionMapper() {
        return owlClassExpressionMapper;
    }

    public static OWLIndividualMapper getOwlIndividualMapper() {
        return owlIndividualMapper;
    }

    public static IdentifierMapper getIdentifierMapper() {
        return identifierMapper;
    }

    public static OWLPropertyExpressionMapper getOwlPropertyExpressionMapper() {
        return owlPropertyExpressionMapper;
    }

    public static OWLObjectMapper getOwlObjectMapper() {
        return owlObjectMapper;
    }

    public static OWLDataMapper getOwlDataMapper() {
        return owlDataMapper;
    }

    public static OWLEntityMapper getOwlEntityMapper() {
        return owlEntityMapper;
    }

    public static OWLAnnotationObjectMapper getOwlAnnotationObjectMapper() {
        return owlAnnotationObjectMapper;
    }

    public static NameMapper getNameMapper() {
        return nameMapper;
    }
}
