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

import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.nativeimage.hosted.Feature;

@Platforms( Platform.HOSTED_ONLY.class )
public class JenaFeature implements Feature {
    @Override
    public void beforeAnalysis( final BeforeAnalysisAccess access ) {
        Native.forClass( org.apache.jena.base.module.SubsystemRegistryServiceLoader.class ).initializeAtBuildTime();
        Native.forClass( org.apache.jena.util.LocationMapper.class ).initializeAtBuildTime();
        Native.forClass( org.apache.jena.riot.system.stream.JenaIOEnvironment.class ).initializeAtBuildTime();

        Native.forClass( "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl" ).registerClass().registerConstructor();
        Native.forClass( "com.sun.org.apache.xerces.internal.jaxp.datatype.DatatypeFactoryImpl" ).registerClass().registerConstructor();
        Native.forClass( org.apache.jena.rdf.model.impl.NTripleReader.class ).registerClass().registerAllMethods();
        Native.forClass( org.apache.jena.rdf.model.impl.NTripleWriter.class ).registerClass().registerAllMethods();

        Native.forClass( "com.github.jsonldjava.shaded.com.google.common.cache.Striped64" )
            .registerClass().registerFields( "base", "busy" );

        Native.forClass( java.sql.Connection.class ).registerClass();
        Native.forClass( java.sql.Driver.class ).registerClass();
        Native.forClass( java.sql.DriverManager.class )
            .registerClass().registerMethod( "getConnection", String.class ).registerMethod( "getDriver", String.class );
        Native.forClass( java.sql.Time.class ).registerClass().registerConstructor( long.class );
        Native.forClass( java.sql.Timestamp.class ).registerClass().registerMethod( "valueOf", String.class );
        Native.forClass( java.time.Duration.class ).registerClass().registerMethod( "parse", CharSequence.class );
        Native.forClass( java.time.Instant.class ).registerClass().registerMethod( "parse", CharSequence.class );
        Native.forClass( java.time.LocalDate.class ).registerClass().registerMethod( "parse", CharSequence.class );
        Native.forClass( java.time.LocalDateTime.class ).registerClass().registerMethod( "parse", CharSequence.class );
        Native.forClass( java.time.LocalTime.class ).registerClass().registerMethod( "parse", CharSequence.class );
        Native.forClass( java.time.MonthDay.class ).registerClass().registerMethod( "parse", CharSequence.class );
        Native.forClass( java.time.OffsetDateTime.class ).registerClass().registerMethod( "parse", CharSequence.class );
        Native.forClass( java.time.OffsetTime.class ).registerClass().registerMethod( "parse", CharSequence.class );
        Native.forClass( java.time.Period.class ).registerClass().registerMethod( "parse", CharSequence.class );
        Native.forClass( java.time.Year.class ).registerClass().registerMethod( "parse", CharSequence.class );
        Native.forClass( java.time.YearMonth.class ).registerClass().registerMethod( "parse", CharSequence.class );
        Native.forClass( java.time.ZoneId.class ).registerClass().registerMethod( "of", String.class );
        Native.forClass( java.time.ZoneOffset.class ).registerClass().registerMethod( "of", String.class );
        Native.forClass( java.time.ZonedDateTime.class ).registerClass().registerMethod( "parse", CharSequence.class );

        Native.withModule( org.apache.jena.Jena.class.getModule() )
            .addResource( "META-INF/services/org.apache.jena.sys.JenaSubSystemLifeCycle" )
            .addResource( "org/apache/jena/jena-properties.xml" )
            .addResourceBundle( "org.apache.jena.ext.xerces.impl.xpath.regex.message" );

        Native.withModule( "java.xml" )
            .addResourceBundle( "com.sun.org.apache.xerces.internal.impl.msg.XMLMessages" );
    }
}
