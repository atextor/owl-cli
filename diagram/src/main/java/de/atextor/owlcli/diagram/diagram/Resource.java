package de.atextor.owlcli.diagram.diagram;

enum Resource {
    OWL_CLASS,
    OWL_OBJECT_PROPERTY,
    OWL_DATA_PROPERTY,
    OWL_ANNOTATION_PROPERTY,
    OWL_INDIVIDUAL,
    OWL_DATATYPE,
    OWL_ALLVALUES,
    OWL_INTERSECTION,
    OWL_INVERSE,
    OWL_DISJOINTNESS,
    OWL_DISJOINT_UNION,
    OWL_UNION,
    OWL_CLOSEDCLASS,
    OWL_DOMAIN,
    OWL_SELF,
    GET,
    LET,
    EQ,
    NEQ,
    R,
    R_C,
    U;

    public String getResourceName( final Configuration.Format format ) {
        return toString().toLowerCase().replace( '_', '-' ) + "." + format.getExtension();
    }
}
