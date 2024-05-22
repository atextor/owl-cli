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

package cool.rdf.cli.buildtime;

import cool.rdf.core.util.StringTemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class WriteStaticCliInfoClass {
    private static final StringTemplate STATIC_CLI_INFO_CLASS_TEMPLATE = new StringTemplate( """
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
          * Provides static information for the command line interface.
          * Generated class, do not edit.
          * Generated on ${buildDate}.
          */
         public class ${className} {
            /**
             * The name of the CLI command
             */
            public static final String COMMAND_NAME = "${commandName}";
         }
         """ );

    /**
     * args[0]: the name of the CLI command
     * args[1]: fully qualified class name to generate
     * args[2]: the path to the file to write
     * args[3]: name of the copyright holder
     *
     * @param args the arguments
     */
    public static void main( final String[] args ) {
        final String commandName = args[0];
        final String fullyQualifiedClassName = args[1];
        final String copyrightHolder = args[3];
        final int lastDot = fullyQualifiedClassName.lastIndexOf( "." );
        final String packageName = fullyQualifiedClassName.substring( 0, lastDot );
        final String className = fullyQualifiedClassName.substring( lastDot + 1 );

        final File targetFile = new File( args[2] );
        targetFile.getParentFile().mkdirs();

        final String content = STATIC_CLI_INFO_CLASS_TEMPLATE.apply( Map.of(
            "year", new SimpleDateFormat( "yyyy" ).format( new Date() ),
            "copyrightHolder", copyrightHolder,
            "package", packageName,
            "className", className,
            "commandName", commandName,
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
