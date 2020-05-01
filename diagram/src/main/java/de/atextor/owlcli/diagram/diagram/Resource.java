package de.atextor.owlcli.diagram.diagram;

enum Resource {
    OWL_INTERSECTION,
    OWL_INVERSE,
    OWL_DISJOINTNESS,
    OWL_DISJOINT_UNION,
    OWL_UNION,
    OWL_SELF;

    public String getResourceName( final Configuration.Format format ) {
        return toString().toLowerCase().replace( '_', '-' ) + "." + format.getExtension();
    }
}
