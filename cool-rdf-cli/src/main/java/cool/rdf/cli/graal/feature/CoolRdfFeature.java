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

package cool.rdf.cli.graal.feature;

import cool.rdf.cli.AbstractCommand;
import cool.rdf.cli.Cool;
import cool.rdf.cli.CoolDiagram;
import cool.rdf.cli.CoolInfer;
import cool.rdf.cli.CoolWrite;
import cool.rdf.cli.LoggingMixin;
import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.nativeimage.hosted.Feature;
import picocli.CommandLine;

import java.util.List;

@Platforms( Platform.HOSTED_ONLY.class )
public class CoolRdfFeature implements Feature {
    @Override
    public void beforeAnalysis( final BeforeAnalysisAccess access ) {
        Native.forClass( AbstractCommand.class ).registerClass().registerAllFields().registerAllMethods();
        Native.forClass( LoggingMixin.class ).registerClass().registerConstructor().registerAllFields().registerAllMethods();
        Native.forClass( Cool.class ).registerClass().registerAllFields().registerAllMethods();
        Native.forClass( CoolDiagram.class ).registerClass().registerAllFields().registerAllMethods();
        Native.forClass( CoolWrite.class ).registerClass().registerAllFields().registerAllMethods();
        Native.forClass( CoolInfer.class ).registerClass().registerAllFields().registerAllMethods();

        Native.forClass( java.io.FilePermission.class ).registerClass();
        Native.forClass( java.lang.Object.class ).registerClass().registerAllFields().registerAllMethods();
        Native.forClass( java.lang.RuntimePermission.class ).registerClass();
        Native.forClass( java.lang.Thread.class ).registerClass().registerFields( "threadLocalRandomProbe" );
        Native.forClass( java.net.NetPermission.class ).registerClass();
        Native.forClass( java.net.SocketPermission.class ).registerClass();
        Native.forClass( java.util.PropertyPermission.class ).registerClass();
        Native.forClass( java.net.URLPermission.class ).registerClass().registerConstructor( String.class, String.class );
        Native.forClass( java.nio.file.Path.class ).registerClass();
        Native.forClass( java.nio.file.Paths.class ).registerClass().registerMethod( "get", String.class, String.class.arrayType() );
        Native.forClass( java.security.AllPermission.class ).registerClass();
        Native.forClass( java.security.SecurityPermission.class ).registerClass();
        Native.forClass( CommandLine.HelpCommand.class ).registerClass().registerAllFields().registerAllMethods();

        Native.forClass( "sun.misc.Unsafe" ).registerClass().registerFields( "theUnsafe" );

        Native.forClass( ClassNotFoundException.class ).registerClassForJni();
        Native.forClass( NoSuchMethodError.class ).registerClassForJni();
        Native.forClass( ClassLoader.class ).registerMethodForJni( "getPlatformClassLoader" );
        Native.forClass( ClassLoader.class ).registerMethodForJni( "loadClass", String.class );
    }

    @Override
    public List<Class<? extends Feature>> getRequiredFeatures() {
        return List.of( JenaFeature.class, LogbackFeature.class, OwlApiFeature.class );
    }
}
