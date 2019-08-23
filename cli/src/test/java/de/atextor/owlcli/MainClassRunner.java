package de.atextor.owlcli;

import lombok.Value;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class MainClassRunner {
    @Value
    static class ExecutionResult {
        int exitStatus;
        String stdOut;
        String stdErr;
    }

    public static ExecutionResult run( final Runnable runnable ) {
        final SecurityManager originalSecurityManager = System.getSecurityManager();

        final CaptureSystemExitSecurityManager securityManager =
            new CaptureSystemExitSecurityManager( originalSecurityManager );
        System.setSecurityManager( securityManager );

        final PrintStream originalStdOut = System.out;
        final PrintStream originalStdErr = System.err;
        final ByteArrayOutputStream stdOutBuffer = new ByteArrayOutputStream();
        final ByteArrayOutputStream stdErrBuffer = new ByteArrayOutputStream();
        final PrintStream newStdOut = new PrintStream( stdOutBuffer );
        final PrintStream newStdErr = new PrintStream( stdErrBuffer );
        System.setOut( newStdOut );
        System.setErr( newStdErr );

        try {
            runnable.run();
        } catch ( final SystemExitCapturedException e ) {
            // Ignore
        } finally {
            System.setSecurityManager( originalSecurityManager );
            System.setOut( originalStdOut );
            System.setErr( originalStdErr );
        }

        return new ExecutionResult( securityManager.getExitCode(), stdOutBuffer.toString(), stdErrBuffer.toString() );
    }
}
