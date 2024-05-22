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

import cool.rdf.core.Prefixes;
import cool.rdf.core.model.RdfPrefix;
import lombok.Builder;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

@Builder
public class FormattingStyle {
    public static final FormattingStyle DEFAULT = builder().build();

    @Builder.Default
    public Set<RdfPrefix> knownPrefixes = Set.of(
        Prefixes.RDF,
        Prefixes.RDFS,
        Prefixes.XSD,
        Prefixes.OWL,
        Prefixes.DCTERMS
    );

    @Builder.Default
    public String emptyRdfBase = TurtleFormatter.DEFAULT_EMPTY_BASE;

    @Builder.Default
    public GapStyle afterClosingParenthesis = GapStyle.NOTHING;

    @Builder.Default
    public GapStyle afterClosingSquareBracket = GapStyle.SPACE;

    @Builder.Default
    public GapStyle afterComma = GapStyle.SPACE;

    @Builder.Default
    public GapStyle afterDot = GapStyle.NEWLINE;

    @Builder.Default
    public GapStyle afterOpeningParenthesis = GapStyle.SPACE;

    @Builder.Default
    public GapStyle afterOpeningSquareBracket = GapStyle.NEWLINE;

    @Builder.Default
    public GapStyle afterSemicolon = GapStyle.NEWLINE;

    @Builder.Default
    public Alignment alignPrefixes = Alignment.OFF;

    @Builder.Default
    public GapStyle beforeClosingParenthesis = GapStyle.SPACE;

    @Builder.Default
    public GapStyle beforeClosingSquareBracket = GapStyle.NEWLINE;

    @Builder.Default
    public GapStyle beforeComma = GapStyle.NOTHING;

    @Builder.Default
    public GapStyle beforeDot = GapStyle.SPACE;

    @Builder.Default
    public GapStyle beforeOpeningParenthesis = GapStyle.SPACE;

    @Builder.Default
    public GapStyle beforeOpeningSquareBracket = GapStyle.SPACE;

    @Builder.Default
    public GapStyle beforeSemicolon = GapStyle.SPACE;

    @Builder.Default
    public Charset charset = Charset.UTF_8;

    @Builder.Default
    public NumberFormat doubleFormat = new DecimalFormat( "0.####E0" );

    @Builder.Default
    public EndOfLineStyle endOfLine = EndOfLineStyle.LF;

    @Builder.Default
    public IndentStyle indentStyle = IndentStyle.SPACE;

    @Builder.Default
    public QuoteStyle quoteStyle = QuoteStyle.TRIPLE_QUOTES_FOR_MULTILINE;

    @Builder.Default
    public WrappingStyle wrapListItems = WrappingStyle.FOR_LONG_LINES;

    @Builder.Default
    public boolean firstPredicateInNewLine = false;

    @Builder.Default
    public boolean useAForRdfType = true;

    @Builder.Default
    public boolean useCommaByDefault = false;

    @Builder.Default
    public Set<Property> commaForPredicate = Set.of( RDF.type );

    @Builder.Default
    public Set<Property> noCommaForPredicate = Set.of();

    @Builder.Default
    public boolean useShortLiterals = true;

    @Builder.Default
    public boolean alignBaseIRI = false;

    @Builder.Default
    public boolean alignObjects = false;

    @Builder.Default
    public boolean alignPredicates = false;

    @Builder.Default
    public int continuationIndentSize = 4;

    @Builder.Default
    public boolean indentPredicates = true;

    @Builder.Default
    public boolean insertFinalNewline = true;

    @Builder.Default
    public int indentSize = 2;

    @Builder.Default
    public int maxLineLength = 100;

    @Builder.Default
    public boolean trimTrailingWhitespace = true;

    @Builder.Default
    public boolean keepUnusedPrefixes = false;

    @Builder.Default
    public List<String> prefixOrder = List.of(
        "rdf",
        "rdfs",
        "xsd",
        "owl"
    );

    @Builder.Default
    public List<Resource> subjectOrder = List.of(
        RDFS.Class,
        OWL2.Ontology,
        OWL2.Class,
        RDF.Property,
        OWL2.ObjectProperty,
        OWL2.DatatypeProperty,
        OWL2.AnnotationProperty,
        OWL2.NamedIndividual,
        OWL2.AllDifferent,
        OWL2.Axiom
    );

    @Builder.Default
    public List<Property> predicateOrder = List.of(
        RDF.type,
        RDFS.label,
        RDFS.comment,
        DCTerms.description
    );

    @Builder.Default
    public List<RDFNode> objectOrder = List.of(
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
        OWL2.IrreflexiveProperty
    );

    @Builder.Default
    public BiFunction<Resource, Integer, String> anonymousNodeIdGenerator = ( resource, integer ) -> "_:gen" + integer;

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
