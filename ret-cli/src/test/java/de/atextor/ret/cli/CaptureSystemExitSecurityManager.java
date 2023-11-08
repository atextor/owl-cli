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
