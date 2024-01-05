/*
 * Copyright 2024 Andreas Textor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.atextor.ret.cli;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;

import static org.assertj.core.api.Assumptions.assumeThat;

@ExtendWith( TestExecutionLogger.class )
public class BinaryTest extends OwlCliTest {
    private static File binary;

    @BeforeAll
    static void beforeMethod() {
        final String binaryPath = System.getProperty( "binary" );
        assumeThat( binaryPath ).isNotNull();
        binary = new File( binaryPath );
        assumeThat( binary ).isFile();
        assumeThat( binary ).exists();
    }

    @Override
    protected CliRunner.Result runCli( final CliRunner.ExecArguments arguments ) {
        return CliRunner.runBinary( binary, arguments );
    }
}
