/*
 * Copyright 2021 Andreas Textor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.atextor.ret.cli;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class MainClassRunner {
    record ExecutionResult( int exitStatus, String stdOut, String stdErr ) {
    }

    /**
     * This method uses {@link System#getSecurityManager()} and {@link System#setSecurityManager(SecurityManager)} that
     * are deprecated as of Java 17. However, until
     * <a href="https://bugs.openjdk.java.net/browse/JDK-8199704">JDK-8199704</a> is addressed,
     * we have still to rely on it.
     *
     * @param runnable the runnable to execute
     * @return the {@link ExecutionResult} of the runnable
     */
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
