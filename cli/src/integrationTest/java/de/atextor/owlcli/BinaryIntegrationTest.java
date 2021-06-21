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
