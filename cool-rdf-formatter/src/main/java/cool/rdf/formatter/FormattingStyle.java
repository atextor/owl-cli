/*
 * Copyright Cool RDF project
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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiFunction;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import cool.rdf.core.Prefixes;
import cool.rdf.core.model.RdfPrefix;
import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record FormattingStyle(
      @RecordBuilder.Initializer( "DEFAULT_KNOWN_PREFIXES" ) Set<RdfPrefix> knownPrefixes,
      @RecordBuilder.Initializer( "DEFAULT_EMPTY_RDF_BASE" ) String emptyRdfBase,
      @RecordBuilder.Initializer( "DEFAULT_AFTER_CLOSING_PARENTHESIS" ) GapStyle afterClosingParenthesis,
      @RecordBuilder.Initializer( "DEFAULT_AFTER_CLOSING_SQUARE_BRACKET" ) GapStyle afterClosingSquareBracket,
      @RecordBuilder.Initializer( "DEFAULT_AFTER_COMMA" ) GapStyle afterComma,
      @RecordBuilder.Initializer( "DEFAULT_AFTER_DOT" ) GapStyle afterDot,
      @RecordBuilder.Initializer( "DEFAULT_AFTER_OPENING_PARENTHESIS" ) GapStyle afterOpeningParenthesis,
      @RecordBuilder.Initializer( "DEFAULT_AFTER_OPENING_SQUARE_BRACKET" ) GapStyle afterOpeningSquareBracket,
      @RecordBuilder.Initializer( "DEFAULT_AFTER_SEMICOLON" ) GapStyle afterSemicolon,
      @RecordBuilder.Initializer( "DEFAULT_ALIGN_PREFIXES" ) Alignment alignPrefixes,
      @RecordBuilder.Initializer( "DEFAULT_BEFORE_CLOSING_PARENTHESIS" ) GapStyle beforeClosingParenthesis,
      @RecordBuilder.Initializer( "DEFAULT_BEFORE_CLOSING_SQUARE_BRACKET" ) GapStyle beforeClosingSquareBracket,
      @RecordBuilder.Initializer( "DEFAULT_BEFORE_COMMA" ) GapStyle beforeComma,
      @RecordBuilder.Initializer( "DEFAULT_BEFORE_DOT" ) GapStyle beforeDot,
      @RecordBuilder.Initializer( "DEFAULT_BEFORE_OPENING_PARENTHESIS" ) GapStyle beforeOpeningParenthesis,
      @RecordBuilder.Initializer( "DEFAULT_BEFORE_OPENING_SQUARE_BRACKET" ) GapStyle beforeOpeningSquareBracket,
      @RecordBuilder.Initializer( "DEFAULT_BEFORE_SEMICOLON" ) GapStyle beforeSemicolon,
      @RecordBuilder.Initializer( "DEFAULT_CHARSET" ) Charset charset,
      @RecordBuilder.Initializer( "DEFAULT_DOUBLE_FORMAT" ) NumberFormat doubleFormat,
      @RecordBuilder.Initializer( "DEFAULT_ENABLE_DOUBLE_FORMATTING" ) boolean enableDoubleFormatting,
      @RecordBuilder.Initializer( "DEFAULT_END_OF_LINE" ) EndOfLineStyle endOfLine,
      @RecordBuilder.Initializer( "DEFAULT_INDENT_STYLE" ) IndentStyle indentStyle,
      @RecordBuilder.Initializer( "DEFAULT_QUOTE_STYLE" ) QuoteStyle quoteStyle,
      @RecordBuilder.Initializer( "DEFAULT_WRAP_LIST_ITEMS" ) WrappingStyle wrapListItems,
      @RecordBuilder.Initializer( "DEFAULT_FIRST_PREDICATE_IN_NEW_LINE" ) boolean firstPredicateInNewLine,
      @RecordBuilder.Initializer( "DEFAULT_USE_A_FOR_RDF_TYPE" ) boolean useAForRdfType,
      @RecordBuilder.Initializer( "DEFAULT_USE_COMMA_BY_DEFAULT" ) boolean useCommaByDefault,
      @RecordBuilder.Initializer( "DEFAULT_COMMA_FOR_PREDICATE" ) Set<Property> commaForPredicate,
      @RecordBuilder.Initializer( "DEFAULT_NO_COMMA_FOR_PREDICATE" ) Set<Property> noCommaForPredicate,
      @RecordBuilder.Initializer( "DEFAULT_USE_SHORT_LITERALS" ) boolean useShortLiterals,
      @RecordBuilder.Initializer( "DEFAULT_ALIGN_BASE_IRI" ) boolean alignBaseIRI,
      @RecordBuilder.Initializer( "DEFAULT_ALIGN_OBJECTS" ) boolean alignObjects,
      @RecordBuilder.Initializer( "DEFAULT_ALIGN_PREDICATES" ) boolean alignPredicates,
      @RecordBuilder.Initializer( "DEFAULT_CONTINUATION_INDENT_SIZE" ) int continuationIndentSize,
      @RecordBuilder.Initializer( "DEFAULT_INDENT_PREDICATES" ) boolean indentPredicates,
      @RecordBuilder.Initializer( "DEFAULT_INSERT_FINAL_NEWLINE" ) boolean insertFinalNewline,
      @RecordBuilder.Initializer( "DEFAULT_INDENT_SIZE" ) int indentSize,
      @RecordBuilder.Initializer( "DEFAULT_MAX_LINE_LENGTH" ) int maxLineLength,
      @RecordBuilder.Initializer( "DEFAULT_TRIM_TRAILING_WHITESPACE" ) boolean trimTrailingWhitespace,
      @RecordBuilder.Initializer( "DEFAULT_KEEP_UNUSED_PREFIXES" ) boolean keepUnusedPrefixes,
      @RecordBuilder.Initializer( "DEFAULT_PRESERVE_BLANK_NODE_LABELS_AND_ORDERING" ) boolean preserveBlankNodeLabelsAndOrdering,
      @RecordBuilder.Initializer( "DEFAULT_PREFIX_ORDER" ) List<String> prefixOrder,
      @RecordBuilder.Initializer( "DEFAULT_SUBJECT_ORDER" ) List<Resource> subjectOrder,
      @RecordBuilder.Initializer( "DEFAULT_PREDICATE_ORDER" ) List<Property> predicateOrder,
      @RecordBuilder.Initializer( "DEFAULT_OBJECT_ORDER" ) List<RDFNode> objectOrder,
      @RecordBuilder.Initializer( "DEFAULT_ANONYMOUS_NODE_ID_GENERATOR" ) BiFunction<Resource, Integer, String> anonymousNodeIdGenerator
) implements FormattingStyleBuilder.With {

   public static final Set<RdfPrefix> DEFAULT_KNOWN_PREFIXES = Set.of(
         Prefixes.RDF,
         Prefixes.RDFS,
         Prefixes.XSD,
         Prefixes.OWL,
         Prefixes.DCTERMS );
   public static final String DEFAULT_EMPTY_RDF_BASE = TurtleFormatter.DEFAULT_EMPTY_BASE;
   public static final GapStyle DEFAULT_AFTER_CLOSING_PARENTHESIS = GapStyle.NOTHING;
   public static final GapStyle DEFAULT_AFTER_CLOSING_SQUARE_BRACKET = GapStyle.SPACE;
   public static final GapStyle DEFAULT_AFTER_COMMA = GapStyle.SPACE;
   public static final GapStyle DEFAULT_AFTER_DOT = GapStyle.NEWLINE;
   public static final GapStyle DEFAULT_AFTER_OPENING_PARENTHESIS = GapStyle.SPACE;
   public static final GapStyle DEFAULT_AFTER_OPENING_SQUARE_BRACKET = GapStyle.NEWLINE;
   public static final GapStyle DEFAULT_AFTER_SEMICOLON = GapStyle.NEWLINE;
   public static final Alignment DEFAULT_ALIGN_PREFIXES = Alignment.OFF;
   public static final GapStyle DEFAULT_BEFORE_CLOSING_PARENTHESIS = GapStyle.SPACE;
   public static final GapStyle DEFAULT_BEFORE_CLOSING_SQUARE_BRACKET = GapStyle.NEWLINE;
   public static final GapStyle DEFAULT_BEFORE_COMMA = GapStyle.NOTHING;
   public static final GapStyle DEFAULT_BEFORE_DOT = GapStyle.SPACE;
   public static final GapStyle DEFAULT_BEFORE_OPENING_PARENTHESIS = GapStyle.SPACE;
   public static final GapStyle DEFAULT_BEFORE_OPENING_SQUARE_BRACKET = GapStyle.SPACE;
   public static final GapStyle DEFAULT_BEFORE_SEMICOLON = GapStyle.SPACE;
   public static final Charset DEFAULT_CHARSET = Charset.UTF_8;
   public static final NumberFormat DEFAULT_DOUBLE_FORMAT = new DecimalFormat( "0.####E0", DecimalFormatSymbols.getInstance( Locale.US ) );
   public static final boolean DEFAULT_ENABLE_DOUBLE_FORMATTING = false;
   public static final EndOfLineStyle DEFAULT_END_OF_LINE = EndOfLineStyle.LF;
   public static final IndentStyle DEFAULT_INDENT_STYLE = IndentStyle.SPACE;
   public static final QuoteStyle DEFAULT_QUOTE_STYLE = QuoteStyle.TRIPLE_QUOTES_FOR_MULTILINE;
   public static final WrappingStyle DEFAULT_WRAP_LIST_ITEMS = WrappingStyle.FOR_LONG_LINES;
   public static final boolean DEFAULT_FIRST_PREDICATE_IN_NEW_LINE = false;
   public static final boolean DEFAULT_USE_A_FOR_RDF_TYPE = true;
   public static final boolean DEFAULT_USE_COMMA_BY_DEFAULT = false;
   public static final Set<Property> DEFAULT_COMMA_FOR_PREDICATE = Set.of( RDF.type );
   public static final Set<Property> DEFAULT_NO_COMMA_FOR_PREDICATE = Set.of();
   public static final boolean DEFAULT_USE_SHORT_LITERALS = true;
   public static final boolean DEFAULT_ALIGN_BASE_IRI = false;
   public static final boolean DEFAULT_ALIGN_OBJECTS = false;
   public static final boolean DEFAULT_ALIGN_PREDICATES = false;
   public static final int DEFAULT_CONTINUATION_INDENT_SIZE = 4;
   public static final boolean DEFAULT_INDENT_PREDICATES = true;
   public static final boolean DEFAULT_INSERT_FINAL_NEWLINE = true;
   public static final int DEFAULT_INDENT_SIZE = 2;
   public static final int DEFAULT_MAX_LINE_LENGTH = 100;
   public static final boolean DEFAULT_TRIM_TRAILING_WHITESPACE = true;
   public static final boolean DEFAULT_KEEP_UNUSED_PREFIXES = false;
   public static final boolean DEFAULT_PRESERVE_BLANK_NODE_LABELS_AND_ORDERING = true;
   public static final List<String> DEFAULT_PREFIX_ORDER = List.of(
         "rdf",
         "rdfs",
         "xsd",
         "owl" );
   public static final List<Resource> DEFAULT_SUBJECT_ORDER = List.of(
         OWL2.Ontology,
         RDFS.Class,
         OWL2.Class,
         RDF.Property,
         OWL2.ObjectProperty,
         OWL2.DatatypeProperty,
         OWL2.AnnotationProperty,
         OWL2.NamedIndividual,
         OWL2.AllDifferent,
         OWL2.Axiom );
   public static final List<Property> DEFAULT_PREDICATE_ORDER = List.of(
         RDF.type,
         RDFS.label,
         RDFS.comment,
         DCTerms.description );
   public static final List<RDFNode> DEFAULT_OBJECT_ORDER = List.of(
         OWL2.NamedIndividual,
         OWL2.ObjectProperty,
         OWL2.DatatypeProperty,
         OWL2.AnnotationProperty,
         OWL2.FunctionalProperty,
         OWL2.InverseFunctionalProperty,
         OWL2.TransitiveProperty,
         OWL2.SymmetricProperty,
         OWL2.AsymmetricProperty,
         OWL2.ReflexiveProperty,
         OWL2.IrreflexiveProperty );
   public static final BiFunction<Resource, Integer, String> DEFAULT_ANONYMOUS_NODE_ID_GENERATOR = ( resource, integer ) -> "gen" + integer;
   public static final FormattingStyle DEFAULT = FormattingStyleBuilder.builder().build();

   /**
    * For backwards compatibility
    *
    * @return the builder
    */
   public static FormattingStyleBuilder builder() {
      return FormattingStyleBuilder.builder();
   }

   public enum Alignment {
      LEFT,
      OFF,
      RIGHT
   }

   public enum Charset {
      LATIN1,
      UTF_16_BE,
      UTF_16_LE,
      UTF_8,
      UTF_8_BOM
   }

   public enum EndOfLineStyle {
      CR,
      CRLF,
      LF
   }

   public enum GapStyle {
      NEWLINE,
      NOTHING,
      SPACE
   }

   public enum IndentStyle {
      SPACE,
      TAB
   }

   public enum WrappingStyle {
      ALWAYS,
      FOR_LONG_LINES,
      NEVER
   }

   public enum QuoteStyle {
      ALWAYS_SINGE_QUOTES,
      TRIPLE_QUOTES_FOR_MULTILINE,
      ALWAYS_TRIPLE_QUOTES
   }
}
