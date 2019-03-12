package de.atextor.owldiagram.diagram;

enum Resource {
    OWL_CLASS,
    OWL_OBJECT_PROPERTY,
    OWL_DATA_PROPERTY,
    OWL_ANNOTATION_PROPERTY,
    OWL_INDIVIDUAL,
    OWL_DATATYPE,
    OWL_SOMEVALUES,
    OWL_HASVALUE,
    OWL_ALLVALUES,
    OWL_INTERSECTION,
    OWL_UNION,
    OWL_CLOSEDCLASS,
    OWL_DOMAIN,
    OWL_COMPLEMENT,
    OWL_SELF,
    EDGE_C,
    EDGE_R,
    EDGE_U,
    EDGE_O,
    LITERAL,
    GET,
    LET,
    EQ,
    R,
    R_C,
    U;

    public String getResourceName() {
        return toString().toLowerCase().replace( '_', '-' );
    }
}
