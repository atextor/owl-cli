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

import org.junit.jupiter.api.Test;

import static de.atextor.owlcli.MainClassRunner.run;
import static org.assertj.core.api.Assertions.assertThat;

public class CommandLineTest {
    @Test
    public void testNoArguments() {
        final Runnable command = () -> OWLCLI.main( new String[]{} );
        final MainClassRunner.ExecutionResult result = run( command );

        assertThat( result.getExitStatus() ).isEqualTo( 0 );
        assertThat( result.getStdOut() ).contains( "Usage: " );
        assertThat( result.getStdErr() ).isEmpty();
    }

    @Test
    public void testHelp() {
        final Runnable command = () -> OWLCLI.main( new String[]{ "--help" } );
        final MainClassRunner.ExecutionResult result = run( command );

        assertThat( result.getExitStatus() ).isEqualTo( 0 );
        assertThat( result.getStdOut() ).contains( "Usage: " );
        assertThat( result.getStdErr() ).isEmpty();
    }

    @Test
    public void testInvalidArguments() {
        final Runnable command = () -> OWLCLI.main( new String[]{ "definitelynotavalidargument" } );
        final MainClassRunner.ExecutionResult result = run( command );

        assertThat( result.getExitStatus() ).isEqualTo( 1 );
        assertThat( result.getStdOut() ).isEmpty();
        assertThat( result.getStdErr() ).contains( "Error: " );
    }
}
