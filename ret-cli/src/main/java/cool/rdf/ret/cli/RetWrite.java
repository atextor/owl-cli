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

package cool.rdf.ret.cli;

import cool.rdf.ret.core.RdfLoader;
import cool.rdf.ret.core.Version;
import cool.rdf.ret.write.Configuration;
import cool.rdf.ret.write.RdfWriter;
import de.atextor.turtle.formatter.FormattingStyle;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static cool.rdf.ret.cli.RetWrite.COMMAND_NAME;

/**
 * The 'write' subcommand
 */
@CommandLine.Command( name = COMMAND_NAME,
    description = "Read a given RDF document and write it out, possibly in a different format",
    descriptionHeading = "%n@|bold Description|@:%n%n",
    parameterListHeading = "%n@|bold Parameters|@:%n",
    optionListHeading = "%n@|bold Options|@:%n",
    footer = "%nSee the online documentation for details:%n" +
        "https://atextor.de/owl-cli/main/" + Version.VERSION + "/usage.html#write-command"
)
public class RetWrite extends AbstractCommand implements Runnable {
    /**
     * The name of this subcommand
     */
    public static final String COMMAND_NAME = "write";

    private static final Logger LOG = LoggerFactory.getLogger( RetWrite.class );

    private static final Configuration config = RdfWriter.DEFAULT_CONFIGURATION;

    private final String fallbackUri = "urn:owl-cli:empty";

    @SuppressWarnings( "unused" )
    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "-o", "--output" },
        description = "Output file format, one of ${COMPLETION-CANDIDATES} (Default: ${DEFAULT-VALUE})" )
    private Configuration.Format outputFormat = config.outputFormat;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "-i", "--input" },
        description = "Input file format, one of ${COMPLETION-CANDIDATES} (Default: ${DEFAULT-VALUE})" )
    private Configuration.Format inputFormat = config.inputFormat;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "-p", "--prefix" },
        description = "Prefix to add as @prefix when used.",
        mapFallbackValue = fallbackUri )
    private Map<String, URI> prefixMap = new HashMap<>();

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--prefixAlign" },
        description = "Alignment of @prefix statements, one of ${COMPLETION-CANDIDATES} (Default: ${DEFAULT-VALUE})" )
    private FormattingStyle.Alignment alignPrefixes = FormattingStyle.DEFAULT.alignPrefixes;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--encoding" },
        description = "Output encoding, one of ${COMPLETION-CANDIDATES} (Default: ${DEFAULT-VALUE})" )
    private FormattingStyle.Charset encoding = FormattingStyle.DEFAULT.charset;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--doubleFormat" },
        description = "Defines how double numbers are formatted (Default: ${DEFAULT-VALUE})" )
    private String doubleFormatPattern = ( (DecimalFormat) FormattingStyle.DEFAULT.doubleFormat ).toPattern();

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--endOfLine" },
        description = "End of line style, one of ${COMPLETION-CANDIDATES} (Default: ${DEFAULT-VALUE})" )
    private FormattingStyle.EndOfLineStyle endOfLineStyle = FormattingStyle.DEFAULT.endOfLine;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--indent" },
        description = "Indent style, one of ${COMPLETION-CANDIDATES} (Default: ${DEFAULT-VALUE})" )
    private FormattingStyle.IndentStyle indentStyle = FormattingStyle.DEFAULT.indentStyle;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--firstPredicateInNewLine" },
        description = "Write first predicate in new line of block (Default: ${DEFAULT-VALUE})" )
    private boolean firstPredicateInNewLine = FormattingStyle.DEFAULT.firstPredicateInNewLine;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--writeRdfType" },
        description = "Write 'rdf:type' instead of 'a' (Default: ${DEFAULT-VALUE})" )
    private boolean writeRdfType = !FormattingStyle.DEFAULT.useAForRdfType;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--useCommaByDefault" },
        description = "Use commas for multiple objects (Default: ${DEFAULT-VALUE})" )
    private boolean useCommaByDefault = FormattingStyle.DEFAULT.useCommaByDefault;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--commaForPredicate" },
        description = "A set of predicates that, when used multiple times, are separated by commas, even when " +
            "useCommaByDefault is false (Default: ${DEFAULT-VALUE})" )
    private Set<Property> commaForPredicate = FormattingStyle.DEFAULT.commaForPredicate;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--noCommaForPredicate" },
        description = "Use no commas for multiple objects (Default: ${DEFAULT-VALUE})" )
    private Set<Property> noCommaForPredicate = FormattingStyle.DEFAULT.noCommaForPredicate;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--useLongLiterals" },
        description = "Use long form for literals where possible (Default: ${DEFAULT-VALUE})" )
    private boolean useLongLiterals = !FormattingStyle.DEFAULT.useShortLiterals;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--alignObjects" },
        description = "Align objects for same predicates (Default: ${DEFAULT-VALUE})" )
    private boolean alignObjects = FormattingStyle.DEFAULT.alignObjects;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--alignPredicates" },
        description = "Align predicates for same subjects (Default: ${DEFAULT-VALUE})" )
    private boolean alignPredicates = FormattingStyle.DEFAULT.alignPredicates;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--continuationIndentSize" },
        description = "Indentation size after forced line wraps (Default: ${DEFAULT-VALUE})" )
    private int continuationIndentSize = FormattingStyle.DEFAULT.continuationIndentSize;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--doNotInsertFinalNewline" },
        description = "Do not insert newline at end of file (Default: ${DEFAULT-VALUE})" )
    private boolean doNotInsertFinalNewline = !FormattingStyle.DEFAULT.insertFinalNewline;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--indentSize" },
        description = "Indentation size in spaces (Default: ${DEFAULT-VALUE})" )
    private int indentSize = FormattingStyle.DEFAULT.indentSize;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--keepUnusedPrefixes" },
        description = "Keeps prefixes that are not part of any statement (Default: ${DEFAULT-VALUE})" )
    private boolean keepUnusedPrefixes = FormattingStyle.DEFAULT.keepUnusedPrefixes;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--prefixOrder" },
        description = "Sort order for prefixes (Default: ${DEFAULT-VALUE})" )
    private List<String> prefixOrder = FormattingStyle.DEFAULT.prefixOrder;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--subjectOrder" },
        description = "Sort order for subjects by type (Default: ${DEFAULT-VALUE})" )
    private List<Resource> subjectOrder = FormattingStyle.DEFAULT.subjectOrder;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--predicateOrder" },
        description = "Sort order for predicates (Default: ${DEFAULT-VALUE})" )
    private List<Property> predicateOrder = FormattingStyle.DEFAULT.predicateOrder;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--objectOrder" },
        description = "Sort order for objects (Default: ${DEFAULT-VALUE})" )
    private List<RDFNode> objectOrder = FormattingStyle.DEFAULT.objectOrder;

    @SuppressWarnings( { "FieldMayBeFinal", "CanBeFinal" } )
    @CommandLine.Option( names = { "--anonymousNodeIdPattern" },
        description = "Name pattern for blank node IDs (Default: ${DEFAULT-VALUE})" )
    private String anonymousNodeIdPattern =
        FormattingStyle.DEFAULT.anonymousNodeIdGenerator.apply( ResourceFactory.createResource(), 0 );

    @SuppressWarnings( "unused" )
    @CommandLine.Parameters( paramLabel = "INPUT", description = "File name, URL, or - for stdin", arity = "1",
        index = "0" )
    private String input;

    @SuppressWarnings( "unused" )
    @CommandLine.Parameters( paramLabel = "OUTPUT",
        description = "File name or - for stdout. If left out, output is written to stdout.",
        arity = "0..1", index = "1" )
    private String output;

    @Override
    public void run() {
        final FormattingStyle style = FormattingStyle.builder()
            .alignPrefixes( alignPrefixes )
            .charset( encoding )
            .doubleFormat( new DecimalFormat( doubleFormatPattern ) )
            .endOfLine( endOfLineStyle )
            .indentStyle( indentStyle )
            .firstPredicateInNewLine( firstPredicateInNewLine )
            .useAForRdfType( !writeRdfType )
            .useCommaByDefault( useCommaByDefault )
            .commaForPredicate( commaForPredicate )
            .noCommaForPredicate( noCommaForPredicate )
            .useShortLiterals( !useLongLiterals )
            .alignObjects( alignObjects )
            .alignPredicates( alignPredicates )
            .continuationIndentSize( continuationIndentSize )
            .insertFinalNewline( !doNotInsertFinalNewline )
            .indentSize( indentSize )
            .keepUnusedPrefixes( keepUnusedPrefixes )
            .prefixOrder( prefixOrder )
            .subjectOrder( subjectOrder )
            .predicateOrder( predicateOrder )
            .objectOrder( objectOrder )
            .anonymousNodeIdGenerator( buildAnonymousNodeIdGenerator( anonymousNodeIdPattern ) )
            .knownPrefixes( buildKnownPrefixes( prefixMap ) )
            .emptyRdfBase( RdfLoader.DEFAULT_EMPTY_PREFIX )
            .build();

        final Configuration.ConfigurationBuilder configurationBuilder = Configuration.builder()
            .outputFormat( outputFormat )
            .inputFormat( inputFormat )
            .formattingStyle( style );

        final RdfWriter writer = new RdfWriter();

        if ( input.toLowerCase().startsWith( "http:" ) || input.toLowerCase().startsWith( "https:" ) ) {
            final Configuration configuration = configurationBuilder.build();
            try {
                final URL inputUrl = new URL( input );
                openOutput( input, output != null ? Optional.of( output ) : Optional.of( "-" ), "ttl" )
                    .map( outputStream -> writer.write( inputUrl, outputStream, configuration ) )
                    .onFailure( throwable -> exitWithErrorMessage( LOG, loggingMixin, throwable ) );
                return;
            } catch ( final MalformedURLException exception ) {
                exitWithErrorMessage( LOG, loggingMixin, exception );
            }
        }

        openInput( input ).flatMap( inputStream -> {
                final Configuration configuration = configurationBuilder.build();
                return openOutput( input, output != null ? Optional.of( output ) : Optional.of( "-" ), "ttl" )
                    .flatMap( outputStream -> {
                        LOG.debug( "Calling write command with config {}", configuration );
                        return writer.write( inputStream, outputStream, configuration );
                    } );
            }
        ).onFailure( throwable -> exitWithErrorMessage( LOG, loggingMixin, throwable ) );
    }

    @Override
    public void registerTypeConverters( final CommandLine commandLine ) {
        commandLine.registerConverter( NumberFormat.class, new NumberFormatConverter() );
        commandLine.registerConverter( Property.class, new PropertyConverter() );
        commandLine.registerConverter( Resource.class, new ResourceConverter() );
        commandLine.registerConverter( RDFNode.class, new RDFNodeConverter() );
    }

    private URI wellKnownUriByPrefixOrElse( final String prefix, final URI uri ) {
        if ( !uri.toString().equals( fallbackUri ) ) {
            return uri;
        }
        // When we end up here, a prefix was given without a URI, i.e., this is supposed to be a
        // "well known" prefix.
        return FormattingStyle.DEFAULT.knownPrefixes.stream()
            .filter( knownPrefix -> knownPrefix.prefix().equals( prefix ) )
            .findAny()
            .map( FormattingStyle.KnownPrefix::iri )
            .orElseThrow( () -> new RuntimeException( "Used prefix " + prefix + " is not well-known" ) );
    }

    private Set<FormattingStyle.KnownPrefix> buildKnownPrefixes( final Map<String, URI> prefixes ) {
        final Set<FormattingStyle.KnownPrefix> result = prefixes.entrySet().stream().map( entry -> {
            final URI uri = wellKnownUriByPrefixOrElse( entry.getKey(), entry.getValue() );
            return new FormattingStyle.KnownPrefix( entry.getKey(), uri );
        } ).collect( Collectors.toSet() );
        LOG.debug( "Known prefixes: {}", result );
        return result;
    }

    private BiFunction<Resource, Integer, String> buildAnonymousNodeIdGenerator( final String pattern ) {
        return ( resource, integer ) -> pattern.replace( "0", "" + integer );
    }

    private static class NumberFormatConverter implements CommandLine.ITypeConverter<NumberFormat> {
        @Override
        public NumberFormat convert( final String value ) {
            return new DecimalFormat( value );
        }
    }

    private abstract class AbstractResourceConverter {
        protected String buildResourceUri( final String resourceUri ) {
            for ( final Map.Entry<String, URI> entry : prefixMap.entrySet() ) {
                final URI uri = wellKnownUriByPrefixOrElse( entry.getKey(), entry.getValue() );
                if ( resourceUri.startsWith( entry.getKey() ) ) {
                    return uri.toString() + resourceUri.substring( ( entry.getKey() + ":" ).length() );
                }
            }
            LOG.debug( "No prefix declaration matched URI {}, keeping as-is", resourceUri );
            return resourceUri;
        }
    }

    private class PropertyConverter extends AbstractResourceConverter implements CommandLine.ITypeConverter<Property> {
        @Override
        public Property convert( final String value ) {
            final String propertyUri = buildResourceUri( value );
            return ResourceFactory.createProperty( propertyUri );
        }
    }

    private class ResourceConverter extends AbstractResourceConverter implements CommandLine.ITypeConverter<Resource> {
        @Override
        public Resource convert( final String value ) {
            final String propertyUri = buildResourceUri( value );
            return ResourceFactory.createResource( propertyUri );
        }
    }

    private class RDFNodeConverter extends AbstractResourceConverter implements CommandLine.ITypeConverter<RDFNode> {
        @Override
        public RDFNode convert( final String value ) {
            final String propertyUri = buildResourceUri( value );
            return ResourceFactory.createResource( propertyUri );
        }
    }

    @Override
    public String commandName() {
        return COMMAND_NAME;
    }
}