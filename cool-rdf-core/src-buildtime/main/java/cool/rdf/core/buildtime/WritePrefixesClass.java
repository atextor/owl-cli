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

package cool.rdf.core.buildtime;

import cool.rdf.core.util.StringTemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is executed at build time and will write the Java file containing static RDF prefix information.
 */
public class WritePrefixesClass {
    private static final StringTemplate PREFIXES_CLASS_TEMPLATE = new StringTemplate( """
        /*
         * Copyright ${year} ${copyrightHolder}
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

        package ${package};
         
        import cool.rdf.core.model.RdfPrefix;

        /**
         * Provides information about well-known RDF prefixes and namespaces.
         * Generated class, do not edit.
         * Generated on ${buildDate}.
         */
        public enum ${className} implements RdfPrefix {
        ${entries};
            
            private final String prefix;
            private final String uri;
            
            ${className}( final String uri ) {
                this.prefix = null;
                this.uri = uri;
            }
            
            ${className}( final String prefix, final String uri ) {
                this.prefix = prefix;
                this.uri = uri;
            }
            
            @Override
            public String prefix() {
                return this.prefix == null
                    ? this.toString().toLowerCase()
                    : this.prefix;
            }
            
            @Override
            public String uri() {
                return uri;
            }
        }
        """ );

    private record PrefixEntry( String enumName, String prefix, String uri ) {
    }

    /**
     * args[0]: the path to resources directory containing prefix definitions
     * args[1]: fully qualified class name to generate
     * args[2]: the path to the file to write
     * args[3]: name of the copyright holder
     *
     * @param args the arguments
     */
    public static void main( final String[] args ) {
        final Path resourcesDirectory = Path.of( args[0] );
        final String fullyQualifiedClassName = args[1];
        final String copyrightHolder = args[3];
        final int lastDot = fullyQualifiedClassName.lastIndexOf( "." );
        final String packageName = fullyQualifiedClassName.substring( 0, lastDot );
        final String className = fullyQualifiedClassName.substring( lastDot + 1 );

        final List<PrefixEntry> prefixes = Stream.of(
                entriesFromFile( resourcesDirectory.resolve( "prefix.cc.txt" ), entry ->
                    new PrefixEntry( entry[0].toUpperCase(), entry[0], entry[1] ) ),
                entriesFromFile( resourcesDirectory.resolve( "other-prefixes.txt" ), entry ->
                    new PrefixEntry( entry[0], entry[1], entry[2] ) ) )
            .flatMap( Function.identity() )
            .toList();

        final File targetFile = new File( args[2] );
        targetFile.getParentFile().mkdirs();

        final String entries = prefixes.stream().sorted( Comparator.comparing( PrefixEntry::enumName ) ).map( entry ->
                entry.enumName().toLowerCase().equals( entry.prefix() )
                    ? "    %s( \"%s\" )".formatted( entry.enumName(), entry.uri() )
                    : "    %s( \"%s\", \"%s\" )".formatted( entry.enumName(), entry.prefix(), entry.uri() ) )
            .collect( Collectors.joining( ",\n" ) );

        final String content = PREFIXES_CLASS_TEMPLATE.apply( Map.of(
            "year", new SimpleDateFormat( "yyyy" ).format( new Date() ),
            "buildDate", new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format( new Date() ),
            "copyrightHolder", copyrightHolder,
            "package", packageName,
            "className", className,
            "entries", entries
        ) );

        try {
            final BufferedWriter writer = new BufferedWriter( new FileWriter( targetFile ) );
            writer.write( content );
            writer.close();
        } catch ( final IOException exception ) {
            throw new RuntimeException( exception );
        }
    }

    private static Stream<PrefixEntry> entriesFromFile( final Path prefixFile, final Function<String[], PrefixEntry> lineParser ) {
        try {
            return Files.readAllLines( prefixFile ).stream().map( line -> lineParser.apply( line.split( "\t" ) ) );
        } catch ( final IOException | NullPointerException exception ) {
            exception.printStackTrace();
            System.err.println( "Could not open " + prefixFile );
            System.exit( 1 );
            return null;
        }
    }
}
