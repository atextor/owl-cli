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

package cool.rdf.turtle.formatter;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class TurtleFormatterPropertyTest {
    @Provide
    Arbitrary<String> anyString() {
        return Arbitraries.strings().all().ofMaxLength( 5 );
    }

    @Provide
    Arbitrary<Literal> anyStringLiteral() {
        return anyString().map( ResourceFactory::createStringLiteral );
    }

    @Provide
    Arbitrary<String> anyLanguageCode() {
        return Arbitraries.of( "en", "de" );
    }

    @Provide
    Arbitrary<Literal> anyLangStringLiteral() {
        return Combinators.combine( anyString(), anyLanguageCode() ).as( ResourceFactory::createLangLiteral );
    }

    @Provide
    Arbitrary<Literal> anyFloatLiteral() {
        return Arbitraries.floats()
            .map( value -> ResourceFactory.createTypedLiteral( value.toString(), XSDDatatype.XSDfloat ) );
    }


    @Provide
    Arbitrary<Literal> anyDoubleLiteral() {
        return Arbitraries.doubles()
            .map( value -> ResourceFactory.createTypedLiteral( value.toString(), XSDDatatype.XSDdouble ) );
    }

    // Providing only one for each of the integer number types is sufficient for testing the formatter
    // as there is no variation in their serialization, but greatly reduces the test space
    @Provide
    Arbitrary<Literal> anyIntegerNumberLiteral() {
        return Arbitraries.of(
            ResourceFactory.createTypedLiteral( "1", XSDDatatype.XSDint ),
            ResourceFactory.createTypedLiteral( "2", XSDDatatype.XSDlong ),
            ResourceFactory.createTypedLiteral( "3", XSDDatatype.XSDshort ),
            ResourceFactory.createTypedLiteral( "4", XSDDatatype.XSDbyte ),
            ResourceFactory.createTypedLiteral( "5", XSDDatatype.XSDunsignedByte ),
            ResourceFactory.createTypedLiteral( "6", XSDDatatype.XSDunsignedInt ),
            ResourceFactory.createTypedLiteral( "7", XSDDatatype.XSDunsignedLong ),
            ResourceFactory.createTypedLiteral( "8", XSDDatatype.XSDunsignedShort )
        );
    }

    @Provide
    Arbitrary<Literal> anyLiteral() {
        return Arbitraries.oneOf( List.of( anyStringLiteral(), anyLangStringLiteral(), anyFloatLiteral(),
            anyDoubleLiteral(), anyIntegerNumberLiteral() ) );
    }

    @Provide
    Arbitrary<Resource> anyAnonymousResource() {
        return Arbitraries.of( ResourceFactory.createResource() );
    }

    @Provide
    Arbitrary<String> anyUrl() {
        return Arbitraries.integers().between( 0, 10 ).map( number -> "http://example.com/" + number );
    }

    @Provide
    Arbitrary<String> anyUrn() {
        return Arbitraries.integers().between( 0, 10 ).map( number -> "urn:ex:" + number );
    }

    @Provide
    Arbitrary<String> anyUri() {
        return Arbitraries.oneOf( List.of( anyUrl(), anyUrn() ) );
    }

    @Provide
    Arbitrary<Resource> anyNamedResource() {
        return anyUri().map( ResourceFactory::createResource );
    }

    @Provide
    Arbitrary<Resource> anyResource() {
        return Arbitraries.oneOf( List.of( anyAnonymousResource(), anyNamedResource() ) );
    }

    @Provide
    Arbitrary<org.apache.jena.rdf.model.Property> anyProperty() {
        return anyUri().map( ResourceFactory::createProperty );
    }

    @Provide
    Arbitrary<Statement> anyStatement( final Model model ) {
        return Combinators.combine( anyResource(), anyProperty(), anyRdfNode( model ) )
            .as( ResourceFactory::createStatement );
    }

    @Provide
    Arbitrary<RDFNode> anyList( final Model model ) {
        final Supplier<Arbitrary<? extends RDFNode>> elements = () -> anyRdfNode( model );
        return Arbitraries.lazyOf( elements, elements ).list().ofMaxSize( 3 )
            .map( list -> model.createList( list.iterator() ) );
    }

    @Provide
    Arbitrary<RDFNode> anyRdfNode( final Model model ) {
        return Arbitraries.oneOf( List.of( anyLiteral(), anyResource(), anyList( model ) ) );
    }

    @Provide
    Arbitrary<List<Statement>> anyListOfStatements( final Model model ) {
        return anyStatement( model ).list().ofMaxSize( 5 );
    }

    // Creates styles for some of the options that control the actual formatting and are difficult to test
    // in isolation.
    @Provide
    Arbitrary<FormattingStyle> anyStyle() {
        final Arbitrary<Boolean> onOff = Arbitraries.of( true, false );

        return Combinators.combine( onOff, onOff, onOff, onOff, onOff )
            .as( ( firstPredicateInNewLine, useAForRdfType, useComma, alignPredicates, alignObjects ) ->
                FormattingStyle.builder()
                    .firstPredicateInNewLine( firstPredicateInNewLine )
                    .useAForRdfType( useAForRdfType )
                    .useCommaByDefault( useComma )
                    .alignPredicates( alignPredicates )
                    .alignObjects( alignObjects )
                    .build() );
    }

    @Provide
    Arbitrary<Model> anyModel() {
        final Model model = ModelFactory.createDefaultModel();
        return anyListOfStatements( model ).map( statements -> {
            model.add( statements );
            return model;
        } );
    }

    @Property( tries = 300 )
    public void anyPrettyPrintedModelIsSyntacticallyValid( @ForAll( "anyModel" ) final Model model,
        @ForAll( "anyStyle" ) final FormattingStyle style ) {
        final TurtleFormatter formatter = new TurtleFormatter( style );
        final String result = formatter.apply( model );
        final Model newModel = ModelFactory.createDefaultModel();
        try {
            newModel.read( new StringReader( result ), "", "TURTLE" );
        } catch ( final RuntimeException e ) {
            fail();
        }
    }

    private final TurtleFormatter defaultFormatter = new TurtleFormatter( FormattingStyle.DEFAULT );

    private final Resource subject = ResourceFactory.createResource( "urn:foo" );

    private final org.apache.jena.rdf.model.Property predicate = ResourceFactory.createProperty( "urn:bar" );

    @Provide
    Arbitrary<Literal> anyStringLiteralWithSpecialCharacters() {
        return Arbitraries.strings().all().map( ResourceFactory::createStringLiteral );
    }

    @Property( tries = 500, seed = "1" )
    public void anyModelContainingSpecialCharactersIsSyntacticallyValid( @ForAll(
        "anyStringLiteralWithSpecialCharacters" ) final Literal literal ) {
        final Model model = ModelFactory.createDefaultModel();
        model.add( subject, predicate, literal );

        final ByteArrayOutputStream out = new ByteArrayOutputStream( 128 );
        try {
            model.write( out, "TURTLE" );
        } catch ( final Exception e ) {
            // If Jena itself can't write it, we don't need to try it
            return;
        }

        final String result = defaultFormatter.apply( model );
        final Model originalModel = ModelFactory.createDefaultModel();
        final Model newModel = ModelFactory.createDefaultModel();
        try {
            originalModel.read( new StringReader( out.toString() ), "", "TURTLE" );
            newModel.read( new StringReader( result ), "", "TURTLE" );
            final Literal originalLiteral =
                originalModel.listStatements( subject, predicate, (RDFNode) null ).nextStatement().getLiteral();
            final Literal newLiteral =
                newModel.listStatements( subject, predicate, (RDFNode) null ).nextStatement().getLiteral();
            assertThat( newLiteral.asNode().getLiteralLexicalForm() )
                .isEqualTo( originalLiteral.asNode().getLiteralLexicalForm() );
        } catch ( final RuntimeException e ) {
            fail( e );
        }
    }
}
