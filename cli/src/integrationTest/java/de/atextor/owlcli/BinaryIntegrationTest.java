/*
 * This file is part of OWL-CLI.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright (c) 2021, Andreas Textor.
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

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class BinaryIntegrationTest {
    static String owl;

    @BeforeAll
    public static void setup() {
        owl = System.getProperty( "owlBinary" );
    }

    @Test
    public void testBinary() throws IOException {
        final Runtime runtime = Runtime.getRuntime();
        final Process p = runtime.exec( owl );

        final byte[] stdout = IOUtils.toByteArray( p.getInputStream() );
        final String output = new String( stdout, StandardCharsets.UTF_8 );

        assertThat( output ).contains( "Usage: owl [-v] [--help] [--version] [COMMAND]" );
    }
}
