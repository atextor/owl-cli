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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assumptions.assumeThat;

@ExtendWith( TestExecutionLogger.class )
public class ExecutableJarTest extends OwlCliTest {
    private static File executableJar;

    @BeforeAll
    static void beforeMethod() {
        final Logger logger = (Logger) LoggerFactory.getLogger( Logger.ROOT_LOGGER_NAME );
        logger.setLevel( Level.ALL );

        final String executableJarPath = System.getProperty( "executableJar" );
        assumeThat( executableJarPath ).isNotNull();
        executableJar = new File( executableJarPath );
        assumeThat( executableJar ).isFile();
        assumeThat( executableJar ).exists();

        // If both the executable jar and the binary exist, don't execute the executable jar tests
        final String binaryPath = System.getProperty( "binary" );
        if ( binaryPath != null ) {
            final File binary = new File( binaryPath );
            assumeThat( binary ).doesNotExist();
        }
    }

    @Override
    protected CliRunner.Result runCli( final CliRunner.ExecArguments arguments ) {
        return CliRunner.runJar( executableJar, arguments, List.of() );
    }
}
