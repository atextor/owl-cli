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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static de.atextor.ret.cli.MainClassRunner.run;
import static org.assertj.core.api.Assertions.assertThat;

@Disabled
public class CommandLineTest {
    @Test
    public void testNoArguments() {
        final Runnable command = () -> OWLCLI.main( new String[]{} );
        final MainClassRunner.ExecutionResult result = run( command );

        assertThat( result.exitStatus() ).isEqualTo( 0 );
        assertThat( result.stdOut() ).contains( "Usage: " );
        assertThat( result.stdErr() ).isEmpty();
    }

    @Test
    public void testHelp() {
        final Runnable command = () -> OWLCLI.main( new String[]{ "--help" } );
        final MainClassRunner.ExecutionResult result = run( command );

        assertThat( result.exitStatus() ).isEqualTo( 0 );
        assertThat( result.stdOut() ).contains( "Usage: " );
        assertThat( result.stdErr() ).isEmpty();
    }

    @Test
    public void testInvalidArguments() {
        @SuppressWarnings( "SpellCheckingInspection" ) final Runnable command = () -> OWLCLI.main( new String[]{
            "definitelynotavalidargument" } );
        final MainClassRunner.ExecutionResult result = run( command );

        assertThat( result.exitStatus() ).isEqualTo( 1 );
        assertThat( result.stdOut() ).isEmpty();
        assertThat( result.stdErr() ).contains( "Error: " );
    }
}
