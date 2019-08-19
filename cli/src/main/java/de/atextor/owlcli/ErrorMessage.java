package de.atextor.owlcli;

public class ErrorMessage extends Exception {
    private static final long serialVersionUID = 5086560337386407192L;

    public ErrorMessage( final String message ) {
        super( message );
    }
}
