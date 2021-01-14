/*
 * This file is part of OWL-CLI.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright (c) 2020, Andreas Textor.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www
 * .gnu.org/licenses/.
 */

package de.atextor.owlcli.write;

import io.vavr.control.Try;
import lombok.Builder;
import lombok.Value;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.vocabulary.XSD;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

@Builder
public class FormattingStyle {
    @Builder.Default
    Set<KnownPrefix> knownPrefixes = Set.of(
        PREFIX_RDF,
        PREFIX_RDFS,
        PREFIX_XSD,
        PREFIX_OWL,
        PREFIX_DCTERMS,
        PREFIX_FMT
    );

    @Builder.Default
    GapStyle afterClosingParenthesis = GapStyle.NEWLINE;

    @Builder.Default
    GapStyle afterClosingSquareBracket = GapStyle.SPACE;

    @Builder.Default
    GapStyle afterComma = GapStyle.SPACE;

    @Builder.Default
    GapStyle afterDot = GapStyle.NEWLINE;

    @Builder.Default
    GapStyle afterOpeningParenthesis = GapStyle.SPACE;

    @Builder.Default
    GapStyle afterOpeningSquareBracket = GapStyle.NEWLINE;

    @Builder.Default
    GapStyle afterSemicolon = GapStyle.NEWLINE;

    @Builder.Default
    Alignment alignPrefixes = Alignment.OFF;

    @Builder.Default
    GapStyle beforeClosingParenthesis = GapStyle.SPACE;

    @Builder.Default
    GapStyle beforeClosingSquareBracket = GapStyle.NEWLINE;

    @Builder.Default
    GapStyle beforeComma = GapStyle.NOTHING;

    @Builder.Default
    GapStyle beforeDot = GapStyle.SPACE;

    @Builder.Default
    GapStyle beforeOpeningParenthesis = GapStyle.SPACE;

    @Builder.Default
    GapStyle beforeOpeningSquareBracket = GapStyle.SPACE;

    @Builder.Default
    GapStyle beforeSemicolon = GapStyle.SPACE;

    @Builder.Default
    Charset charset = Charset.UTF_8;

    @Builder.Default
    EndOfLineStyle endOfLine = EndOfLineStyle.LF;

    @Builder.Default
    IndentStyle indentStyle = IndentStyle.SPACE;

    @Builder.Default
    WrappingStyle wrapListItems = WrappingStyle.FOR_LONG_LINES;

    @Builder.Default
    boolean rdfTypeInNewLine = false;

    @Builder.Default
    boolean useAForRdfType = true;

    @Builder.Default
    boolean useCommaByDefault = false;

    @Builder.Default
    Set<Property> commaForPredicate = Set.of( RDF.type );

    @Builder.Default
    Set<Property> noCommaForPredicate = Set.of();

    @Builder.Default
    boolean useShortLiterals = true;

    @Builder.Default
    boolean alignBaseIRI = false;

    @Builder.Default
    boolean alignObjects = false;

    @Builder.Default
    boolean alignPredicates = false;

    @Builder.Default
    int continuationIndentSize = 4;

    @Builder.Default
    boolean indentPrediates = true;

    @Builder.Default
    boolean insertFinalNewline = true;

    @Builder.Default
    int indentSize = 2;

    @Builder.Default
    int maxLineLength = 100;

    @Builder.Default
    boolean trimTrailingWhitespace = true;

    @Builder.Default
    List<String> prefixOrder = List.of(
        "rdf",
        "rdfs",
        "xsd",
        "owl"
    );

    @Builder.Default
    List<Resource> subjectOrder = List.of(
        OWL2.Ontology,
        OWL2.Class,
        OWL2.ObjectProperty,
        OWL2.DatatypeProperty,
        OWL2.AnnotationProperty,
        OWL2.NamedIndividual,
        OWL2.AllDifferent,
        OWL2.Axiom
    );

    @Builder.Default
    List<Property> predicateOrder = List.of(
        RDF.type
    );

    @Builder.Default
    List<RDFNode> objectOrder = List.of(
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
    BiFunction<Resource, Integer, String> anonymousNodeIdGenerator = ( resource, integer ) -> "_:genId" + integer;

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

    @Value
    public static class KnownPrefix {
        String prefix;

        URI iri;
    }

    public static final KnownPrefix PREFIX_FMT = new KnownPrefix( "fmt", URI.create( FMT.NS ) );

    public static final KnownPrefix PREFIX_RDF = new KnownPrefix( "rdf", URI.create( RDF.uri ) );

    public static final KnownPrefix PREFIX_RDFS = new KnownPrefix( "rdfs", URI.create( RDFS.uri ) );

    public static final KnownPrefix PREFIX_XSD = new KnownPrefix( "xsd", URI.create( XSD.NS ) );

    public static final KnownPrefix PREFIX_OWL = new KnownPrefix( "owl", URI.create( OWL2.NS ) );

    public static final KnownPrefix PREFIX_DCTERMS = new KnownPrefix( "dcterms", URI.create( DCTerms.NS ) );

    public static final KnownPrefix PREFIX_VANN = new KnownPrefix( "vann",
        URI.create( "http://purl.org/vocab/vann/" ) );

    public static final KnownPrefix PREFIX_SKOS = new KnownPrefix( "skos", URI.create( SKOS.getURI() ) );

    public static final KnownPrefix PREFIX_EX = new KnownPrefix( "ex", URI.create( "http://example.org/" ) );

    public static Try<FormattingStyle> fromModel( final Model model ) {
        return null;
    }
}
