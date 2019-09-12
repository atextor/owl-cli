package de.atextor.owlcli;

import java.security.Permission;

public class CaptureSystemExitSecurityManager extends SecurityManager {
    private final SecurityManager delegateSecurityManager;
    private int exitCode = 0;

    CaptureSystemExitSecurityManager( final SecurityManager delegateSecurityManager ) {
        this.delegateSecurityManager = delegateSecurityManager;
    }

    int getExitCode() {
        return exitCode;
    }

    @Override
    public void checkPermission( final Permission permission ) {
        if ( delegateSecurityManager != null ) {
            delegateSecurityManager.checkPermission( permission );
        }
    }

    @Override
    public void checkExit( final int i ) {
        exitCode = i;
        throw new SystemExitCapturedException();
    }
}
