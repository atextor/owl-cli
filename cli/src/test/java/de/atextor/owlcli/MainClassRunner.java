/*
 * This file is part of OWL-CLI.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright (c) 2020, Andreas Textor.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www
 * .gnu.org/licenses/.
 */

package de.atextor.owlcli;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class MainClassRunner {
    record ExecutionResult(int exitStatus, String stdOut, String stdErr) {
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
