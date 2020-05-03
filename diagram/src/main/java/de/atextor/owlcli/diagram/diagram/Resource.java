package de.atextor.owlcli.diagram.diagram;

enum Resource {
    OWL_SELF;

    public String getResourceName( final Configuration.Format format ) {
        return toString().toLowerCase().replace( '_', '-' ) + "." + format.getExtension();
    }
}
