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

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class ResourceArgumentsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments( final ExtensionContext context ) {
        return Arrays.stream( Objects
            .requireNonNull( Path.of( System.getProperty( "user.dir" ), "src", "test", "resources" ).toFile()
                .listFiles( ( dir, name ) -> name.endsWith( ".ttl" ) ) ) )
            .map( File::getName )
            .map( filename -> filename.replace( ".ttl", "" ) )
            .sorted()
            .map( FilenameArguments::new );
    }

    public static class FilenameArguments implements Arguments {
        String filename;

        public FilenameArguments( final String filename ) {
            this.filename = filename;
        }

        @Override
        public Object[] get() {
            return new Object[]{ filename };
        }
    }
}
