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

package cool.rdf.ret.core.buildtime;

import cool.rdf.ret.core.util.StringTemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * This class is executed at build time and will write the Java file containing static build version information.
 */
public class WriteVersionClass {
    private static final StringTemplate VERSION_CLASS_TEMPLATE = new StringTemplate( """
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

         /**
          * Provides static build version information.
          * Generated class, do not edit.
          */
         public class ${className} {
            /**
             * The version of the package
             */
            public static final String VERSION = "${version}";

            /**
             * The commit id of the package
             */
            public static final String COMMIT_ID = "${commitId}";

            /**
             * The build date
             */
            public static final String BUILD_DATE = "${buildDate}";
         }
         """ );

    /**
     * args[0]: the path to git.properties
     * args[1]: fully qualified class name to generate
     * args[2]: the path to the file to write
     *
     * @param args the arguments
     */
    public static void main( final String[] args ) {
        final String gitPropertiesFile = args[0];
        final String fullyQualifiedClassName = args[1];
        final int lastDot = fullyQualifiedClassName.lastIndexOf( "." );
        final String packageName = fullyQualifiedClassName.substring( 0, lastDot );
        final String className = fullyQualifiedClassName.substring( lastDot + 1 );

        final Properties gitProperties;
        try ( final InputStream gitPropertiesInputStream = new FileInputStream( gitPropertiesFile ) ) {
            gitProperties = new Properties();
            gitProperties.load( gitPropertiesInputStream );
        } catch ( final IOException exception ) {
            System.err.println( "Could not open git.properties" );
            System.exit( 1 );
            return;
        }

        final File targetFile = new File( args[2] );
        if ( !targetFile.getParentFile().mkdirs() ) {
            System.err.println( "Could not create directory: " + targetFile.getParentFile() );
            System.exit( 1 );
        }

        final String content = VERSION_CLASS_TEMPLATE.apply( Map.of(
            "year", new SimpleDateFormat( "yyyy" ).format( new Date() ),
            // For now. May read this from contributors file in the future.
            "copyrightHolder", "Andreas Textor",
            "package", packageName,
            "className", className,
            "version", gitProperties.getProperty( "git.build.version" ),
            "commitId", gitProperties.getProperty( "git.commit.id" ),
            "buildDate", new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format( new Date() )
        ) );

        try {
            final BufferedWriter writer = new BufferedWriter( new FileWriter( targetFile ) );
            writer.write( content );
            writer.close();
        } catch ( final IOException exception ) {
            throw new RuntimeException( exception );
        }
    }
}
