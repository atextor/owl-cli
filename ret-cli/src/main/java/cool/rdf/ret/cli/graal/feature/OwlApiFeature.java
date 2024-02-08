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

package cool.rdf.ret.cli.graal.feature;

import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.nativeimage.hosted.Feature;

import java.util.List;

@Platforms( Platform.HOSTED_ONLY.class )
public class OwlApiFeature implements Feature {
    @Override
    public void beforeAnalysis( final BeforeAnalysisAccess access ) {
        Native.forClass( uk.ac.manchester.cs.owl.owlapi.OWLOntologyManagerImpl.class ).initializeAtBuildTime();

        Native.forClass( "uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl" ).registerClass();
        Native.forClass( "uk.ac.manchester.cs.owl.owlapi.OWLOntologyFactoryImpl" ).registerClass();
        Native.forClass( "uk.ac.manchester.cs.owl.owlapi.concurrent.ConcurrentOWLOntologyBuilder" ).registerClass();
        Native.forClass( "uk.ac.manchester.cs.owl.owlapi.concurrent.NonConcurrentOWLOntologyBuilder" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.dlsyntax.parser.DLSyntaxOWLParserFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxHTMLStorerFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxStorerFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.functional.parser.OWLFunctionalSyntaxOWLParserFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.functional.renderer.FunctionalSyntaxStorerFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.krss2.parser.KRSS2OWLParserFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.krss2.renderer.KRSS2OWLSyntaxStorerFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.latex.renderer.LatexStorerFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntaxOntologyParserFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterSyntaxStorerFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.oboformat.OBOFormatOWLAPIParserFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.oboformat.OBOFormatStorerFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.owlxml.parser.OWLXMLParserFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.owlxml.renderer.OWLXMLStorerFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.rdf.rdfxml.parser.RDFXMLParserFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.rdf.rdfxml.renderer.RDFXMLStorerFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.rdf.turtle.parser.TurtleOntologyParserFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.rdf.turtle.renderer.TurtleStorerFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.rio.RioBinaryRdfParserFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.rio.RioBinaryRdfStorerFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.rio.RioJsonLDParserFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.rio.RioJsonLDStorerFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.rio.RioJsonParserFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.rio.RioJsonStorerFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.rio.RioN3ParserFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.rio.RioN3StorerFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.rio.RioNQuadsParserFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.rio.RioNQuadsStorerFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.rio.RioNTriplesParserFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.rio.RioNTriplesStorerFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.rio.RioRDFXMLParserFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.rio.RioRDFXMLStorerFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.rio.RioRDFaParserFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.rio.RioTrigParserFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.rio.RioTrigStorerFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.rio.RioTrixParserFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.rio.RioTrixStorerFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.rio.RioTurtleParserFactory" ).registerClass();
        Native.forClass( "org.semanticweb.owlapi.rio.RioTurtleStorerFactory" ).registerClass();

        Native.forClass( "org.semanticweb.owlapi.apibinding.OWLManager$InjectorConstants" )
            .registerClass()
            .registerFields( "COMPRESSION_ENABLED", "CONCURRENTBUILDER", "CONFIG", "NONCONCURRENTBUILDER", "NOOP", "REENTRANT" );
    }

    @Override
    public List<Class<? extends Feature>> getRequiredFeatures() {
        return List.of( CaffeineFeature.class );
    }
}
