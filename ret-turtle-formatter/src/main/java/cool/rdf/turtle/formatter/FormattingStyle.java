package cool.rdf.turtle.formatter;

import lombok.Builder;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

@Builder
public class FormattingStyle {
    public static final KnownPrefix PREFIX_FMT = new KnownPrefix( "fmt", URI.create( FMT.NS ) );

    public static final KnownPrefix PREFIX_RDF = new KnownPrefix( "rdf", URI.create( RDF.uri ) );

    public static final KnownPrefix PREFIX_RDFS = new KnownPrefix( "rdfs", URI.create( RDFS.uri ) );

    public static final KnownPrefix PREFIX_XSD = new KnownPrefix( "xsd", URI.create( XSD.NS ) );

    public static final KnownPrefix PREFIX_OWL = new KnownPrefix( "owl", URI.create( OWL2.NS ) );

    public static final KnownPrefix PREFIX_DCTERMS = new KnownPrefix( "dcterms", URI.create( DCTerms.NS ) );

    public static final FormattingStyle DEFAULT = builder().build();

    public static final KnownPrefix PREFIX_VANN = new KnownPrefix( "vann",
        URI.create( "http://purl.org/vocab/vann/" ) );

    public static final KnownPrefix PREFIX_SKOS = new KnownPrefix( "skos", URI.create( SKOS.getURI() ) );

    public static final KnownPrefix PREFIX_EX = new KnownPrefix( "ex", URI.create( "http://example.org/" ) );

    @Builder.Default
    public Set<KnownPrefix> knownPrefixes = Set.of(
        PREFIX_RDF,
        PREFIX_RDFS,
        PREFIX_XSD,
        PREFIX_OWL,
        PREFIX_DCTERMS
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

    public record KnownPrefix(String prefix, URI iri) {
    }
}
