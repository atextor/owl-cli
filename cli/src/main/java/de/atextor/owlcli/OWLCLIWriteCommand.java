/*
 * Copyright 2021 Andreas Textor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.atextor.owlcli;

import de.atextor.owlcli.write.Configuration;
import de.atextor.owlcli.write.RdfWriter;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@CommandLine.Command( name = "write",
    description = "Read a given RDF document and write it out, possibly in a different format",
    descriptionHeading = "%n@|bold Description|@:%n%n",
    parameterListHeading = "%n@|bold Parameters|@:%n",
    optionListHeading = "%n@|bold Options|@:%n",
    footer = "%nSee the online documentation for details:%n" +
        "https://atextor.de/owl-cli/main/" + OWLCLIConfig.VERSION + "/usage.html#write-command"
)
public class OWLCLIWriteCommand extends AbstractCommand implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger( OWLCLIWriteCommand.class );

    private static final Configuration config = RdfWriter.DEFAULT_CONFIGURATION;

    private final String fallbackUri = "urn:owl-cli:empty";

    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    @CommandLine.Option( names = { "-o", "--output" },
        description = "Output file format, one of ${COMPLETION-CANDIDATES} (Default: ${DEFAULT-VALUE})" )
    private Configuration.Format outputFormat = config.outputFormat;

    @CommandLine.Option( names = { "-i", "--input" },
        description = "Input file format, one of ${COMPLETION-CANDIDATES} (Default: ${DEFAULT-VALUE})" )
    private Configuration.Format inputFormat = config.inputFormat;

    @CommandLine.Option( names = { "-p", "--prefix" },
        description = "Known prefixes to add as @prefix when used. (Default: ${DEFAULT-VALUE})",
        mapFallbackValue = fallbackUri )
    private Map<String, URI> prefixMap =
        FormattingStyle.DEFAULT.knownPrefixes.stream().collect( Collectors.toMap(
            FormattingStyle.KnownPrefix::getPrefix, FormattingStyle.KnownPrefix::getIri ) );

    @CommandLine.Option( names = { "--prefixAlign" },
        description = "Alignment of @prefix statements, one of ${COMPLETION-CANDIDATES} (Default: ${DEFAULT-VALUE})" )
    private FormattingStyle.Alignment alignPrefixes = FormattingStyle.DEFAULT.alignPrefixes;

    @CommandLine.Option( names = { "--encoding" },
        description = "Output encoding, one of ${COMPLETION-CANDIDATES} (Default: ${DEFAULT-VALUE})" )
    private FormattingStyle.Charset encoding = FormattingStyle.DEFAULT.charset;

    @CommandLine.Option( names = { "--doubleFormat" },
        description = "Defines how double numbers are formatted (Default: ${DEFAULT-VALUE})" )
    private String doubleFormatPattern = ( (DecimalFormat) FormattingStyle.DEFAULT.doubleFormat ).toPattern();

    @CommandLine.Option( names = { "--endOfLine" },
        description = "End of line style, one of ${COMPLETION-CANDIDATES} (Default: ${DEFAULT-VALUE})" )
    private FormattingStyle.EndOfLineStyle endOfLineStyle = FormattingStyle.DEFAULT.endOfLine;

    @CommandLine.Option( names = { "--indent" },
        description = "Indent style, one of ${COMPLETION-CANDIDATES} (Default: ${DEFAULT-VALUE})" )
    private FormattingStyle.IndentStyle indentStyle = FormattingStyle.DEFAULT.indentStyle;

    @CommandLine.Option( names = { "--firstPredicateInNewLine" },
        description = "Write first predicate in new line of block (Default: ${DEFAULT-VALUE})" )
    private boolean firstPredicateInNewLine = FormattingStyle.DEFAULT.firstPredicateInNewLine;

    @CommandLine.Option( names = { "--writeRdfType" },
        description = "Write 'rdf:type' instead of 'a' (Default: ${DEFAULT-VALUE})" )
    private boolean writeRdfType = !FormattingStyle.DEFAULT.useAForRdfType;

    @CommandLine.Option( names = { "--useCommaByDefault" },
        description = "Use commas for multiple objects (Default: ${DEFAULT-VALUE})" )
    private boolean useCommaByDefault = FormattingStyle.DEFAULT.useCommaByDefault;

    @CommandLine.Option( names = { "--commaForPredicate" },
        description = "A set of predicates that, when used multiple times, are separated by commas, even when " +
            "useCommaByDefault is false (Default: ${DEFAULT-VALUE})" )
    private Set<Property> commaForPredicate = FormattingStyle.DEFAULT.commaForPredicate;

    @CommandLine.Option( names = { "--noCommaForPredicate" },
        description = "Use no commas for multiple objects (Default: ${DEFAULT-VALUE})" )
    private Set<Property> noCommaForPredicate = FormattingStyle.DEFAULT.noCommaForPredicate;

    @CommandLine.Option( names = { "--useLongLiterals" },
        description = "Use long form for literals where possible (Default: ${DEFAULT-VALUE})" )
    private boolean useLongLiterals = !FormattingStyle.DEFAULT.useShortLiterals;

    @CommandLine.Option( names = { "--alignObjects" },
        description = "Align objects for same predicates (Default: ${DEFAULT-VALUE})" )
    private boolean alignObjects = FormattingStyle.DEFAULT.alignObjects;

    @CommandLine.Option( names = { "--alignPredicates" },
        description = "Align predicates for same subjects (Default: ${DEFAULT-VALUE})" )
    private boolean alignPredicates = FormattingStyle.DEFAULT.alignPredicates;

    @CommandLine.Option( names = { "--continuationIndentSize" },
        description = "Indentation size after forced line wraps (Default: ${DEFAULT-VALUE})" )
    private int continuationIndentSize = FormattingStyle.DEFAULT.continuationIndentSize;

    @CommandLine.Option( names = { "--doNotInsertFinalNewline" },
        description = "Do not insert newline at end of file (Default: ${DEFAULT-VALUE})" )
    private boolean doNotInsertFinalNewline = !FormattingStyle.DEFAULT.insertFinalNewline;

    @CommandLine.Option( names = { "--indentSize" },
        description = "Indentation size in spaces (Default: ${DEFAULT-VALUE})" )
    private int indentSize = FormattingStyle.DEFAULT.indentSize;

    @CommandLine.Option( names = { "--keepUnusedPrefixes" },
        description = "Keeps prefixes that are not part of any statement (Default: ${DEFAULT-VALUE})" )
    private boolean keepUnusedPrefixes = FormattingStyle.DEFAULT.keepUnusedPrefixes;

    @CommandLine.Option( names = { "--prefixOrder" },
        description = "Sort order for prefixes (Default: ${DEFAULT-VALUE})" )
    private List<String> prefixOrder = FormattingStyle.DEFAULT.prefixOrder;

    @CommandLine.Option( names = { "--subjectOrder" },
        description = "Sort order for subjects by type (Default: ${DEFAULT-VALUE})" )
    private List<Resource> subjectOrder = FormattingStyle.DEFAULT.subjectOrder;

    @CommandLine.Option( names = { "--predicateOrder" },
        description = "Sort order for predicates (Default: ${DEFAULT-VALUE})" )
    private List<Property> predicateOrder = FormattingStyle.DEFAULT.predicateOrder;

    @CommandLine.Option( names = { "--objectOrder" },
        description = "Sort order for objects (Default: ${DEFAULT-VALUE})" )
    private List<RDFNode> objectOrder = FormattingStyle.DEFAULT.objectOrder;

    @CommandLine.Option( names = { "--anonymousNodeIdPattern" },
        description = "Name pattern for blank node IDs (Default: ${DEFAULT-VALUE})" )
    private String anonymousNodeIdPattern =
        FormattingStyle.DEFAULT.anonymousNodeIdGenerator.apply( ResourceFactory.createResource(), 0 );

    @CommandLine.Parameters( paramLabel = "INPUT", description = "File name, URL, or - for stdin", arity = "1",
        index = "0" )
    private String input;

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
                openOutput( input, output != null ? output : "-", "ttl" )
                    .map( outputStream -> writer.write( inputUrl, outputStream, configuration ) )
                    .onFailure( throwable -> exitWithErrorMessage( LOG, loggingMixin, throwable ) );
                return;
            } catch ( final MalformedURLException exception ) {
                exitWithErrorMessage( LOG, loggingMixin, exception );
            }
        }

        openInput( input ).flatMap( inputStream -> {
                final Configuration configuration = input.equals( "-" ) ?
                    configurationBuilder.build() :
                    configurationBuilder.base( "file://" + input ).build();
                return openOutput( input, output != null ? output : "-", "ttl" )
                    .flatMap( outputStream -> writer.write( inputStream, outputStream, configuration ) );
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
        public NumberFormat convert( final String value ) throws Exception {
            return new DecimalFormat( value );
        }
    }

    private abstract class AbstractResourceConverter {
        protected String buildResourceUri( final String resourceUri ) throws Exception {
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
        public Property convert( final String value ) throws Exception {
            final String propertyUri = buildResourceUri( value );
            return ResourceFactory.createProperty( propertyUri );
        }
    }

    private class ResourceConverter extends AbstractResourceConverter implements CommandLine.ITypeConverter<Resource> {
        @Override
        public Resource convert( final String value ) throws Exception {
            final String propertyUri = buildResourceUri( value );
            return ResourceFactory.createResource( propertyUri );
        }
    }

    private class RDFNodeConverter extends AbstractResourceConverter implements CommandLine.ITypeConverter<RDFNode> {
        @Override
        public RDFNode convert( final String value ) throws Exception {
            final String propertyUri = buildResourceUri( value );
            return ResourceFactory.createResource( propertyUri );
        }
    }

}
