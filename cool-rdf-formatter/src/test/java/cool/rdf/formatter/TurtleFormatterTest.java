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

package cool.rdf.formatter;

import cool.rdf.core.model.RdfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.assertj.core.api.Assertions.assertThat;

public class TurtleFormatterTest {
    @Test
    public void testPrefixAlignmentLeft() {
        final Model model = prefixModel();
        final FormattingStyle style = FormattingStyle.builder()
            .knownPrefixes( Set.of() )
            .beforeDot( FormattingStyle.GapStyle.SPACE )
            .alignPrefixes( FormattingStyle.Alignment.LEFT )
            .insertFinalNewline( false )
            .keepUnusedPrefixes( true )
            .build();
        final TurtleFormatter formatter = new TurtleFormatter( style );
        final String result = formatter.apply( model );
        final String expected = """
            @prefix       : <http://example.com/> .
            @prefix a     : <http://example.com/a> .
            @prefix abc   : <http://example.com/abc> .
            @prefix abcdef: <http://example.com/abc> .

            """;
        assertThat( result ).isEqualTo( expected );
    }

    @Test
    public void testPrefixAlignmentOff() {
        final Model model = prefixModel();
        final FormattingStyle style = FormattingStyle.builder()
            .knownPrefixes( Set.of() )
            .beforeDot( FormattingStyle.GapStyle.SPACE )
            .alignPrefixes( FormattingStyle.Alignment.OFF )
            .insertFinalNewline( false )
            .keepUnusedPrefixes( true )
            .build();
        final TurtleFormatter formatter = new TurtleFormatter( style );
        final String result = formatter.apply( model );
        final String expected = """
            @prefix : <http://example.com/> .
            @prefix a: <http://example.com/a> .
            @prefix abc: <http://example.com/abc> .
            @prefix abcdef: <http://example.com/abc> .

            """;
        assertThat( result ).isEqualTo( expected );
    }

    @Test
    public void testPrefixAlignmentRight() {
        final Model model = prefixModel();
        final FormattingStyle style = FormattingStyle.builder()
            .knownPrefixes( Set.of() )
            .beforeDot( FormattingStyle.GapStyle.SPACE )
            .alignPrefixes( FormattingStyle.Alignment.RIGHT )
            .insertFinalNewline( false )
            .keepUnusedPrefixes( true )
            .build();
        final TurtleFormatter formatter = new TurtleFormatter( style );
        final String result = formatter.apply( model );
        final String expected = """
            @prefix       : <http://example.com/> .
            @prefix      a: <http://example.com/a> .
            @prefix    abc: <http://example.com/abc> .
            @prefix abcdef: <http://example.com/abc> .

            """;
        assertThat( result ).isEqualTo( expected );
    }

    @Test
    public void testLiterals() {
        final String modelString = """
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            @prefix : <http://example.com/> .

            :foo01 :bar 1 .

            :foo02 :bar "2" .

            :foo03 :bar true .

            :foo04 :bar -5.0 .

            :foo05 :bar 4.2E9 .

            :foo06 :bar "2021-01-01"^^xsd:date .

            :foo07 :bar "something"^^:custom .

            :foo08 :bar "something"@en .

            :foo09 :bar "This contains a \\" quote" .

            :foo10 :bar \"""This contains a
            linebreak\""" .
            """;
        final Model model = RdfModel.fromTurtle( modelString );
        final FormattingStyle style = FormattingStyle.builder()
            .knownPrefixes( Set.of() )
            .quoteStyle( FormattingStyle.QuoteStyle.TRIPLE_QUOTES_FOR_MULTILINE )
            .build();
        final TurtleFormatter formatter = new TurtleFormatter( style );
        final String result = formatter.apply( model );
        assertThat( result.trim() ).isEqualTo( modelString.trim() );
    }

    @Test
    public void testQuotedStringsWithSingleQuotes() {
        final String modelString = """
            @prefix : <http://example.com/> .

            :foo :bar "This contains and ends with a \\"quote\\"" ;
              :bar2 "This contains a\\nlinebreak" ;
              :bar3 "This contains \\t \\\\ \\b" .
            """;
        final Model model = RdfModel.fromTurtle( modelString );
        final FormattingStyle style = FormattingStyle.builder()
            .quoteStyle( FormattingStyle.QuoteStyle.ALWAYS_SINGE_QUOTES )
            .knownPrefixes( Set.of() )
            .build();
        final TurtleFormatter formatter = new TurtleFormatter( style );
        final String result = formatter.apply( model );
        assertThat( result.trim() ).isEqualTo( modelString.trim() );
    }

    @SuppressWarnings( "TextBlockMigration" )
    @Test
    public void testQuotedStringsWithTripleQuotes() {
        // We'll put this in a regular string instead of a text block to make the escaping and the
        // RDF triple quotes inside the string clearer
        final String modelString = "@prefix : <http://example.com/> .\n" +
            "\n:foo :bar \"\"\"This contains and ends with a \"quote\\\"\"\"\" ;\n" +
            "  :bar2 \"\"\"This contains \\t \\\\ \\b\"\"\" .\n";
        final Model model = RdfModel.fromTurtle( modelString );
        final FormattingStyle style = FormattingStyle.builder()
            .quoteStyle( FormattingStyle.QuoteStyle.ALWAYS_TRIPLE_QUOTES )
            .knownPrefixes( Set.of() )
            .build();
        final TurtleFormatter formatter = new TurtleFormatter( style );
        final String result = formatter.apply( model );
        assertThat( result.trim() ).isEqualTo( modelString.trim() );
    }

    @Test
    public void testPredicateAlignmentWithFirstPredicateInSameLine() {
        final String modelString = """
            @prefix : <http://example.com/> .

            :foo1 :bar1 1 ;
                  :bar2 2 ;
                  :bar3 3 .

            :something :bar1 1 ;
                       :bar2 2 ;
                       :bar3 3 .
            """;
        final Model model = RdfModel.fromTurtle( modelString );

        final FormattingStyle style = FormattingStyle.builder()
            .knownPrefixes( Set.of() )
            .alignPredicates( true )
            .firstPredicateInNewLine( false )
            .build();
        final TurtleFormatter formatter = new TurtleFormatter( style );
        final String result = formatter.apply( model );
        assertThat( result.trim() ).isEqualTo( modelString.trim() );
    }

    @Test
    public void testIndentationWithTabs() {
        final String modelString = """
            @prefix : <http://example.com/> .

            :foo1 :bar1 1 ;
            \t:bar2 2 ;
            \t:bar3 3 .
            """;
        final Model model = RdfModel.fromTurtle( modelString );

        final FormattingStyle style = FormattingStyle.builder()
            .knownPrefixes( Set.of() )
            .firstPredicateInNewLine( false )
            .indentStyle( FormattingStyle.IndentStyle.TAB )
            .build();
        final TurtleFormatter formatter = new TurtleFormatter( style );
        final String result = formatter.apply( model );
        assertThat( result.trim() ).isEqualTo( modelString.trim() );
    }

    @Test
    public void testPredicateAlignmentWithFirstPredicateInNewLine() {
        final String modelString = """
            @prefix : <http://example.com/> .

            :foo1
              :bar1 1 ;
              :bar2 2 ;
              :bar3 3 .

            :something
              :bar1 1 ;
              :bar2 2 ;
              :bar3 3 .
            """;
        final Model model = RdfModel.fromTurtle( modelString );

        final FormattingStyle style = FormattingStyle.builder()
            .knownPrefixes( Set.of() )
            .firstPredicateInNewLine( true )
            .build();
        final TurtleFormatter formatter = new TurtleFormatter( style );
        final String result = formatter.apply( model );
        assertThat( result.trim() ).isEqualTo( modelString.trim() );
    }

    @Test
    public void testPredicateAndObjectAlignment() {
        final String modelString = """
            @prefix : <http://example.com/> .

            :foo1 :bar       1 ;
                  :bar234    2 ;
                  :bar567890 3 .
            """;
        final Model model = RdfModel.fromTurtle( modelString );

        final FormattingStyle style = FormattingStyle.builder()
            .knownPrefixes( Set.of() )
            .firstPredicateInNewLine( false )
            .alignPredicates( true )
            .alignObjects( true )
            .build();
        final TurtleFormatter formatter = new TurtleFormatter( style );
        final String result = formatter.apply( model );
        assertThat( result.trim() ).isEqualTo( modelString.trim() );
    }

    @Test
    public void testPredicateAndObjectAlignmentWithFirstPredicateInNewLine() {
        final String modelString = """
            @prefix : <http://example.com/> .

            :foo1
              :bar       1 ;
              :bar234    2 ;
              :bar567890 3 .
            """;
        final Model model = RdfModel.fromTurtle( modelString );

        final FormattingStyle style = FormattingStyle.builder()
            .knownPrefixes( Set.of() )
            .firstPredicateInNewLine( true )
            .alignPredicates( true )
            .alignObjects( true )
            .build();
        final TurtleFormatter formatter = new TurtleFormatter( style );
        final String result = formatter.apply( model );
        assertThat( result.trim() ).isEqualTo( modelString.trim() );
    }

    @Test
    public void testMultipleReferencedAnonNodes() {
        // Note how the anonymous node can not be serialized using [ ] because it is referenced multiple times.
        final String modelString = """
            @prefix : <http://example.com/> .

            :a :foo _:gen0 ;
              :bar 1 .

            :b :foo _:gen0 .

            _:gen0 :bar 2 .
            """;
        final Model model = RdfModel.fromTurtle( modelString );

        final String ex = "http://example.com/";
        final Property foo = createProperty( ex + "foo" );
        final Property bar = createProperty( ex + "bar" );
        final FormattingStyle style = FormattingStyle.builder()
            .knownPrefixes( Set.of() )
            .predicateOrder( List.of( foo, bar ) )
            .build();
        final TurtleFormatter formatter = new TurtleFormatter( style );
        final String result = formatter.apply( model );
        assertThat( result.trim() ).isEqualTo( modelString.trim() );
    }

    @Test
    public void testTopLevelAnonymousNode() {
        final String modelString = """
            @prefix : <http://example.com/> .

            [
              :foo 1 ;
              :bar 2 ;
              :baz 3 ;
            ] .
            """;
        final Model model = RdfModel.fromTurtle( modelString );

        final String ex = "http://example.com/";
        final Property foo = createProperty( ex + "foo" );
        final Property bar = createProperty( ex + "bar" );
        final Property baz = createProperty( ex + "baz" );
        final FormattingStyle style = FormattingStyle.builder()
            .knownPrefixes( Set.of() )
            .predicateOrder( List.of( foo, bar, baz ) )
            .build();
        final TurtleFormatter formatter = new TurtleFormatter( style );
        final String result = formatter.apply( model );
        assertThat( result.trim() ).isEqualTo( modelString.trim() );
    }

    @Test
    public void testTopLevelIdentifiedAnonymousNodeWithMultiplePredicates() {
        final String modelString = """
            @prefix : <http://example.com/> .

            :a a :type ;
              :foo _:gen0 ;
              :bar 1 .

            :b :foo _:gen0 .

            _:gen0 a :type ;
              :foo "1" ;
              :bar "2" .
            """;
        final Model model = RdfModel.fromTurtle( modelString );

        final String ex = "http://example.com/";
        final Property foo = createProperty( ex + "foo" );
        final Property bar = createProperty( ex + "bar" );
        final FormattingStyle style = FormattingStyle.builder()
            .knownPrefixes( Set.of() )
            .predicateOrder( List.of( RDF.type, foo, bar ) )
            .build();
        final TurtleFormatter formatter = new TurtleFormatter( style );
        final String result = formatter.apply( model );
        assertThat( result.trim() ).isEqualTo( modelString.trim() );
    }

    @Test
    public void testListWrappingNever() {
        final String modelString = """
            @prefix : <http://example.com/> .

            :foo :bar ( "foo1" "foo2" "foo3" "foo4" ) .
            """;
        // Line length ruler
        //  ###############################################
        //  1        10        20        30        40
        final Model model = RdfModel.fromTurtle( modelString );

        final FormattingStyle style = FormattingStyle.builder()
            .knownPrefixes( Set.of() )
            .maxLineLength( 30 )
            .wrapListItems( FormattingStyle.WrappingStyle.NEVER )
            .build();
        final TurtleFormatter formatter = new TurtleFormatter( style );
        final String result = formatter.apply( model );
        assertThat( result.trim() ).isEqualTo( modelString.trim() );
    }

    @Test
    public void testListWrappingAlways() {
        final String modelString = """
            @prefix : <http://example.com/> .

            :foo :bar ( "foo1" "foo2" "foo3" "foo4" ) .
            """;
        // Line length ruler
        //  ###############################################
        //  1        10        20        30        40
        final Model model = RdfModel.fromTurtle( modelString );

        final FormattingStyle style = FormattingStyle.builder()
            .knownPrefixes( Set.of() )
            .maxLineLength( 30 )
            .wrapListItems( FormattingStyle.WrappingStyle.ALWAYS )
            .build();
        final TurtleFormatter formatter = new TurtleFormatter( style );
        final String result = formatter.apply( model );
        final String expected = """
            @prefix : <http://example.com/> .

            :foo :bar (
                "foo1"
                "foo2"
                "foo3"
                "foo4"
              ) .
            """;
        assertThat( result.trim() ).isEqualTo( expected.trim() );
    }

    @Test
    public void testListWrappingForLongLines() {
        final String modelString = """
            @prefix : <http://example.com/> .

            :foo :bar ( "foo1" "foo2" "foo3" "foo4" ) .
            """;
        // Line length ruler
        //  ###############################################
        //  1        10        20        30        40
        final Model model = RdfModel.fromTurtle( modelString );

        final FormattingStyle style = FormattingStyle.builder()
            .knownPrefixes( Set.of() )
            .maxLineLength( 30 )
            .wrapListItems( FormattingStyle.WrappingStyle.FOR_LONG_LINES )
            .build();
        final TurtleFormatter formatter = new TurtleFormatter( style );
        final String result = formatter.apply( model );
        final String expected = """
            @prefix : <http://example.com/> .

            :foo :bar ( "foo1" "foo2"
                "foo3" "foo4" ) .
            """;
        assertThat( result.trim() ).isEqualTo( expected.trim() );
    }

    @Test
    public void testListOfAnonymousNodes() {
        final String modelString = """
            @prefix : <http://example.com/> .

            :foo :bar ( [
                :x 1 ;
              ] [
                :x 2 ;
              ] ) .
            """;
        final Model model = RdfModel.fromTurtle( modelString );

        final FormattingStyle style = FormattingStyle.builder()
            .knownPrefixes( Set.of() )
            .build();
        final TurtleFormatter formatter = new TurtleFormatter( style );
        final String result = formatter.apply( model );
        assertThat( result.trim() ).isEqualTo( modelString.trim() );
    }

    @Test
    public void testEmptyUrlWithEmptyBase() {
        final String modelString = """
            @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
            @prefix : <http://example.com/> .

            :Person a rdfs:Class ;
              :foo <> .
            """;
        final Model model = RdfModel.fromTurtle( modelString );

        final FormattingStyle style = FormattingStyle.builder()
            .knownPrefixes( Set.of() )
            .build();
        final TurtleFormatter formatter = new TurtleFormatter( style );
        final String result = formatter.apply( model );
        assertThat( result.trim() ).isEqualTo( modelString.trim() );
    }

    @Test
    public void testUtf8BomCharset() {
        final Model model = prefixModel();
        final FormattingStyle style = FormattingStyle.builder()
            .charset( FormattingStyle.Charset.UTF_8_BOM )
            .build();
        final TurtleFormatter formatter = new TurtleFormatter( style );
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        formatter.accept( model, outputStream );
        assertThat( outputStream.toByteArray() ).startsWith( (byte) 0xEF, (byte) 0xBB, (byte) 0xBF );
    }

    @Test
    public void testFormatting() {
        final Model model = ModelFactory.createDefaultModel();
        model
            .read( "https://raw.githubusercontent.com/atextor/turtle-formatting/main/turtle-formatting.ttl", "TURTLE" );
        final FormattingStyle style = FormattingStyle.builder().build();

        final TurtleFormatter formatter = new TurtleFormatter( style );
        final String result = formatter.apply( model );
        final Model resultModel = RdfModel.fromTurtle( result );
        assertThat( model.isIsomorphicWith( resultModel ) ).isTrue();

    }

    @Test
    public void testSubjectsNotInSubjectOrder() {
        final String modelString = """
            @prefix : <http://example.com/> .
            @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
            @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .

            :Person a rdfs:Class .
            :name a rdf:Property .
            :address a rdf:Property .
            :city a rdf:Property .
            :Max a :Person ;
              :name "Max" ;
              :address [
                :city "City Z"
              ] .
            """;

        final Model model = RdfModel.fromTurtle( modelString );
        final FormattingStyle style = FormattingStyle.builder().build();

        final TurtleFormatter formatter = new TurtleFormatter( style );
        final String result = formatter.apply( model );
        final Model resultModel = RdfModel.fromTurtle( result );
        assertThat( model.isIsomorphicWith( resultModel ) ).isTrue();
    }

    @Test
    public void testRepeatedPredicates() {
        final String modelString = """
            @prefix : <http://example.com/> .

            :foo :bar [
                :something "x" ;
                :something "y" ;
                :something "z" ;
              ] .
            """;
        final Model model = RdfModel.fromTurtle( modelString );

        final FormattingStyle style = FormattingStyle.builder()
            .knownPrefixes( Set.of() )
            .build();
        final TurtleFormatter formatter = new TurtleFormatter( style );
        final String result = formatter.apply( model );
        assertThat( result.trim() ).isEqualTo( modelString.trim() );
    }

    @Test
    public void testEscapedLocalNameAndEscapedString() {
        final String modelString = """
            @prefix : <http://example.com#> .

            :foo :something :ab\\/cd .
            :bar :something "ab\\\\cd" .
            :baz :something "ab\\"cd" .
            :baz2 :something \"""ab"cd\""" .
            """;
        final Model model = RdfModel.fromTurtle( modelString );
        final FormattingStyle style = FormattingStyle.builder().build();

        final TurtleFormatter formatter = new TurtleFormatter( style );
        final String result = formatter.apply( model );
        final Model resultModel = RdfModel.fromTurtle( result );

        assertThat( model.isIsomorphicWith( resultModel ) ).isTrue();

        final Resource foo = createResource( "http://example.com#foo" );
        final Statement fooStatement = resultModel.listStatements( foo, null, (RDFNode) null ).nextStatement();
        assertThat( fooStatement.getObject().asResource().getURI() ).isEqualTo( "http://example.com#ab/cd" );

        final Resource bar = createResource( "http://example.com#bar" );
        final Statement barStatement = resultModel.listStatements( bar, null, (RDFNode) null ).nextStatement();
        assertThat( barStatement.getObject().asLiteral().getString() ).isEqualTo( "ab\\cd" );

        final Resource baz = createResource( "http://example.com#baz" );
        final Statement bazStatement = resultModel.listStatements( baz, null, (RDFNode) null ).nextStatement();
        assertThat( bazStatement.getObject().asLiteral().getString() ).isEqualTo( "ab\"cd" );

        final Resource baz2 = createResource( "http://example.com#baz2" );
        final Statement baz2Statement = resultModel.listStatements( baz2, null, (RDFNode) null ).nextStatement();
        assertThat( baz2Statement.getObject().asLiteral().getString() ).isEqualTo( "ab\"cd" );
    }

    @Test
    public void testPrefixEscapeCharacters() {
        final String modelString = """
            @prefix foo-bar: <http://example.com#> .
            @prefix foo_bar: <http://example2.com#> .
            @prefix ä_1: <http://example3.com#> .
            @prefix foo: <http://example4.com#> .

            foo-bar:foo foo-bar:foo "value1" .
            foo_bar:bar foo_bar:bar "value2" .
            ä_1:baz ä_1:baz "value3" .
            foo:some-thing foo:some-thing "x" .
            foo:some.thing foo:some.thing "x" .
            foo:some_thing foo:some_thing "x" .
            foo:some\\*thing foo:some\\*thing "x" .
            foo:some\\?thing foo:some\\?thing "x" .
            foo:some\\@thing foo:some\\@thing "x" .
            <http://example4.com#-10> foo:something "x" .
            foo::another foo:test "x" .
            foo:\\$10 foo:test "x" .
            """;
        final Model model = RdfModel.fromTurtle( modelString );
        final FormattingStyle style = FormattingStyle.builder().build();

        final TurtleFormatter formatter = new TurtleFormatter( style );
        final String result = formatter.apply( model );
        final Model resultModel = RdfModel.fromTurtle( result );

        // Should not be escaped and should be local names: dashes and underscores in prefix part of local names
        assertThat( result ).contains( "foo-bar:foo" );
        assertThat( result ).contains( "foo_bar:bar" );
        assertThat( result ).contains( "foo::another" );
        // Should not be escaped and should be local names: dashes and underscores in name part of local names
        assertThat( result ).contains( "foo:some-thing" );
        assertThat( result ).contains( "foo:some_thing" );
        // Should be full URIs, since local names would be invalid
        assertThat( result ).contains( "http://example4.com#some*thing" );
        assertThat( result ).contains( "http://example4.com#some?thing" );
        assertThat( result ).contains( "http://example4.com#some@thing" );
        assertThat( result ).contains( "http://example4.com#-10" );
        assertThat( result ).contains( "http://example4.com#$10" );

        assertThat( model.isIsomorphicWith( resultModel ) ).isTrue();

        final Resource subject1 = createResource( "http://example.com#foo" );
        final Statement subject1Statement =
            resultModel.listStatements( subject1, null, (RDFNode) null ).nextStatement();
        assertThat( subject1Statement.getObject().asLiteral().getString() ).isEqualTo( "value1" );

        final Resource subject2 = createResource( "http://example2.com#bar" );
        final Statement subject2Statement = resultModel.listStatements( subject2, null, (RDFNode) null )
            .nextStatement();
        assertThat( subject2Statement.getObject().asLiteral().getString() ).isEqualTo( "value2" );

        final Resource subject3 = createResource( "http://example3.com#baz" );
        final Statement subject3Statement = resultModel.listStatements( subject3, null, (RDFNode) null )
            .nextStatement();
        assertThat( subject3Statement.getObject().asLiteral().getString() ).isEqualTo( "value3" );

        for ( final String namePart :
            List.of( "some-thing", "some_thing", "some*thing", "some?thing", "some@thing" ) ) {
            final Resource resource = createResource( "http://example4.com#" + namePart );
            final Statement statement = resultModel.listStatements( resource, null, (RDFNode) null )
                .nextStatement();
            assertThat( statement.getObject().asLiteral().getString() ).isEqualTo( "x" );
        }
    }

    @Test
    void testRespectEndOfLineConfig() {
        final Map<FormattingStyle.EndOfLineStyle, String> expected = Map.of(
            FormattingStyle.EndOfLineStyle.LF, "\n",
            FormattingStyle.EndOfLineStyle.CR, "\r",
            FormattingStyle.EndOfLineStyle.CRLF, "\r\n" );
        final Map<FormattingStyle.EndOfLineStyle, List<String>> unexpected = Map.of(
            FormattingStyle.EndOfLineStyle.LF, List.of( "\r", "\r\n" ),
            FormattingStyle.EndOfLineStyle.CR, List.of( "\n", "\r\n" ),
            FormattingStyle.EndOfLineStyle.CRLF, List.of() );

        for ( final FormattingStyle.EndOfLineStyle endOfLine : FormattingStyle.EndOfLineStyle.values() ) {
            final FormattingStyle style = FormattingStyle.builder()
                .endOfLine( endOfLine )
                .build();
            final TurtleFormatter formatter = new TurtleFormatter( style );

            final String expectedLineEnding = expected.get( endOfLine );
            final List<String> unexpectedLineEndings = unexpected.get( endOfLine );

            final Model onlyPrefixesModel = prefixModel();
            final String onlyPrefixesResult = formatter.apply( onlyPrefixesModel );
            assertThat( onlyPrefixesResult ).contains( expectedLineEnding );
            unexpectedLineEndings.forEach( unexpectedLineEnding -> assertThat( onlyPrefixesResult ).doesNotContain( unexpectedLineEnding ) );

            final Model modelWithAssertions = RdfModel.fromTurtle( """
                @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
                @prefix : <http://example.com/> .

                :Person a rdfs:Class ;
                  :foo <> .
                """ );
            final String modelWithAssertionsResult = formatter.apply( modelWithAssertions );
            assertThat( modelWithAssertionsResult ).contains( expectedLineEnding );
            unexpectedLineEndings.forEach( unexpectedLineEnding -> assertThat( modelWithAssertionsResult ).doesNotContain( unexpectedLineEnding ) );
        }
    }

    @Test
    void testRdfTypeAsObjectIsValid() {
        final String modelString = """
            @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
            @prefix : <http://example.com/> .

            :foo a :bar ;
              :something rdf:type .
            """;
        final Model model = RdfModel.fromTurtle( modelString );

        final FormattingStyle style = FormattingStyle.builder()
            .useAForRdfType( true )
            .knownPrefixes( Set.of() )
            .build();
        final TurtleFormatter formatter = new TurtleFormatter( style );
        final String result = formatter.apply( model );
        assertThat( result.trim() ).isEqualTo( modelString.trim() );
    }

    private Model prefixModel() {
        final Model model = ModelFactory.createDefaultModel();
        model.setNsPrefix( "", "http://example.com/" );
        model.setNsPrefix( "a", "http://example.com/a" );
        model.setNsPrefix( "abc", "http://example.com/abc" );
        model.setNsPrefix( "abcdef", "http://example.com/abc" );
        return model;
    }
}
