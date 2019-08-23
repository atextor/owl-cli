package de.atextor.owlcli;

import java.io.FileDescriptor;
import java.net.InetAddress;
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
    public void checkPermission( final Permission permission, final Object o ) {
        if ( delegateSecurityManager != null ) {
            delegateSecurityManager.checkPermission( permission, o );
        }
    }

    @Override
    public void checkCreateClassLoader() {
        if ( delegateSecurityManager != null ) {
            delegateSecurityManager.checkCreateClassLoader();
        }
    }

    @Override
    public void checkAccess( final Thread thread ) {
        if ( delegateSecurityManager != null ) {
            delegateSecurityManager.checkAccess( thread );
        }
    }

    @Override
    public void checkAccess( final ThreadGroup threadGroup ) {
        if ( delegateSecurityManager != null ) {
            delegateSecurityManager.checkAccess( threadGroup );
        }
    }

    @Override
    public void checkExit( final int i ) {
        exitCode = i;
        throw new SystemExitCapturedException();
    }

    @Override
    public void checkExec( final String s ) {
        if ( delegateSecurityManager != null ) {
            delegateSecurityManager.checkExec( s );
        }
    }

    @Override
    public void checkLink( final String s ) {
        if ( delegateSecurityManager != null ) {
            delegateSecurityManager.checkLink( s );
        }
    }

    @Override
    public void checkRead( final FileDescriptor fileDescriptor ) {
        if ( delegateSecurityManager != null ) {
            delegateSecurityManager.checkRead( fileDescriptor );
        }
    }

    @Override
    public void checkRead( final String s ) {
        if ( delegateSecurityManager != null ) {
            delegateSecurityManager.checkRead( s );
        }
    }

    @Override
    public void checkRead( final String s, final Object o ) {
        if ( delegateSecurityManager != null ) {
            delegateSecurityManager.checkRead( s, o );
        }
    }

    @Override
    public void checkWrite( final FileDescriptor fileDescriptor ) {
        if ( delegateSecurityManager != null ) {
            delegateSecurityManager.checkWrite( fileDescriptor );
        }
    }

    @Override
    public void checkWrite( final String s ) {
        if ( delegateSecurityManager != null ) {
            delegateSecurityManager.checkWrite( s );
        }
    }

    @Override
    public void checkDelete( final String s ) {
        if ( delegateSecurityManager != null ) {
            delegateSecurityManager.checkDelete( s );
        }
    }

    @Override
    public void checkConnect( final String s, final int i ) {
        if ( delegateSecurityManager != null ) {
            delegateSecurityManager.checkConnect( s, i );
        }
    }

    @Override
    public void checkConnect( final String s, final int i, final Object o ) {
        if ( delegateSecurityManager != null ) {
            delegateSecurityManager.checkConnect( s, i, o );
        }
    }

    @Override
    public void checkListen( final int i ) {
        if ( delegateSecurityManager != null ) {
            delegateSecurityManager.checkListen( i );
        }
    }

    @Override
    public void checkAccept( final String s, final int i ) {
        if ( delegateSecurityManager != null ) {
            delegateSecurityManager.checkAccept( s, i );
        }
    }

    @Override
    public void checkMulticast( final InetAddress inetAddress ) {
        if ( delegateSecurityManager != null ) {
            delegateSecurityManager.checkMulticast( inetAddress );
        }
    }

    @Override
    public void checkPropertiesAccess() {
        if ( delegateSecurityManager != null ) {
            delegateSecurityManager.checkPropertiesAccess();
        }
    }

    @Override
    public void checkPropertyAccess( final String s ) {
        if ( delegateSecurityManager != null ) {
            delegateSecurityManager.checkPropertyAccess( s );
        }
    }

    @Override
    public void checkPrintJobAccess() {
        if ( delegateSecurityManager != null ) {
            delegateSecurityManager.checkPrintJobAccess();
        }
    }

    @Override
    public void checkPackageAccess( final String s ) {
        if ( delegateSecurityManager != null ) {
            delegateSecurityManager.checkPackageAccess( s );
        }
    }

    @Override
    public void checkPackageDefinition( final String s ) {
        if ( delegateSecurityManager != null ) {
            delegateSecurityManager.checkPackageDefinition( s );
        }
    }

    @Override
    public void checkSetFactory() {
        if ( delegateSecurityManager != null ) {
            delegateSecurityManager.checkSetFactory();
        }
    }
}
