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

package de.atextor.owlcli;

import de.atextor.owlcli.write.Configuration;
import de.atextor.owlcli.write.RdfWriter;
import de.atextor.turtle.formatter.FormattingStyle;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sys.JenaSystem;
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
import java.util.Optional;
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

    static {
        JenaSystem.setSubsystemRegistry( new StaticJenaSubsystemRegistry() );
        JenaSystem.init();
    }

    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    @CommandLine.Option( names = { "-o", "--out" },
        description = "Output file format, one of ${COMPLETION-CANDIDATES} (Default: ${DEFAULT-VALUE})" )
    private Configuration.Format outputFormat = config.outputFormat;

    @CommandLine.Option( names = { "-i", "--input" },
        description = "Input file format, one of ${COMPLETION-CANDIDATES} (Default: ${DEFAULT-VALUE})" )
    private Configuration.Format inputFormat = config.inputFormat;

    @CommandLine.Option( names = { "-p", "--prefix" },
        description = "Known prefixes to add as @prefix when used. (Default: ${DEFAULT-VALUE})",
        mapFallbackValue = CommandLine.Option.NULL_VALUE )
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
    private NumberFormat doubleFormat = FormattingStyle.DEFAULT.doubleFormat;

    @CommandLine.Option( names = { "--endOfLine" },
        description = "End of line style, one of ${COMPLETION-CANDIDATES} (Default: ${DEFAULT-VALUE})" )
    private FormattingStyle.EndOfLineStyle endOfLineStyle = FormattingStyle.DEFAULT.endOfLine;

    @CommandLine.Option( names = { "--indent" },
        description = "Indent style, one of ${COMPLETION-CANDIDATES} (Default: ${DEFAULT-VALUE})" )
    private FormattingStyle.IndentStyle indentStyle = FormattingStyle.DEFAULT.indentStyle;

    @CommandLine.Option( names = { "--firstPredicateInNewLine" },
        description = "Write first predicate in new line of block (Default: ${DEFAULT-VALUE})" )
    private boolean firstPredicateInNewLine = FormattingStyle.DEFAULT.firstPredicateInNewLine;

    @CommandLine.Option( names = { "--useAForRdfType" },
        description = "Write 'a' instead of 'rdf:type' (Default: ${DEFAULT-VALUE})" )
    private boolean useAForRdfType = FormattingStyle.DEFAULT.useAForRdfType;

    @CommandLine.Option( names = { "--useCommaByDefault" },
        description = "Use commas for multiple objects (Default: ${DEFAULT-VALUE})" )
    private boolean useCommaByDefault = FormattingStyle.DEFAULT.useCommaByDefault;

    @CommandLine.Option( names = { "--commaForPredicate" },
        description = "Use commas for multiple objects (Default: ${DEFAULT-VALUE})" )
    private Set<Property> commaForPredicate = FormattingStyle.DEFAULT.commaForPredicate;

    @CommandLine.Option( names = { "--noCommaForPredicate" },
        description = "Use no commas for multiple objects (Default: ${DEFAULT-VALUE})" )
    private Set<Property> noCommaForPredicate = FormattingStyle.DEFAULT.noCommaForPredicate;

    @CommandLine.Option( names = { "--useShortLiterals" },
        description = "Use short form for literals where possible (Default: ${DEFAULT-VALUE})" )
    private boolean useShortLiterals = FormattingStyle.DEFAULT.useShortLiterals;

    @CommandLine.Option( names = { "--alignObjects" },
        description = "Align objects for same predicates (Default: ${DEFAULT-VALUE})" )
    private boolean alignObjects = FormattingStyle.DEFAULT.alignObjects;

    @CommandLine.Option( names = { "--alignPredicates" },
        description = "Align predicates for same subjects (Default: ${DEFAULT-VALUE})" )
    private boolean alignPredicates = FormattingStyle.DEFAULT.alignPredicates;

    @CommandLine.Option( names = { "--continuationIndentSize" },
        description = "Indentation size after forced line wraps (Default: ${DEFAULT-VALUE})" )
    private int continuationIndentSize = FormattingStyle.DEFAULT.continuationIndentSize;

    @CommandLine.Option( names = { "--insertFinalNewline" },
        description = "Insert newline at end of file (Default: ${DEFAULT-VALUE})" )
    private boolean insertFinalNewline = FormattingStyle.DEFAULT.insertFinalNewline;

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
            .doubleFormat( doubleFormat )
            .endOfLine( endOfLineStyle )
            .indentStyle( indentStyle )
            .firstPredicateInNewLine( firstPredicateInNewLine )
            .useAForRdfType( useAForRdfType )
            .useCommaByDefault( useCommaByDefault )
            .commaForPredicate( commaForPredicate )
            .noCommaForPredicate( noCommaForPredicate )
            .useShortLiterals( useShortLiterals )
            .alignObjects( alignObjects )
            .alignPredicates( alignPredicates )
            .continuationIndentSize( continuationIndentSize )
            .insertFinalNewline( insertFinalNewline )
            .indentSize( indentSize )
            .keepUnusedPrefixes( keepUnusedPrefixes )
            .prefixOrder( prefixOrder )
            .subjectOrder( subjectOrder )
            .predicateOrder( predicateOrder )
            .objectOrder( objectOrder )
            .anonymousNodeIdGenerator( buildAnonymousNodeIdGenerator( anonymousNodeIdPattern ) )
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
                final Configuration configuration = configurationBuilder.base( "file://" + input ).build();
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
        private Optional<URI> wellKnownUriByPrefix( String prefix ) {
            return FormattingStyle.DEFAULT.knownPrefixes.stream()
                .filter( knownPrefix -> knownPrefix.getPrefix().equals( prefix ) )
                .findAny()
                .map( FormattingStyle.KnownPrefix::getIri );
        }

        protected String buildResourceUri( String resourceUri ) throws Exception {
            for ( Map.Entry<String, URI> entry : OWLCLIWriteCommand.this.prefixMap.entrySet() ) {
                final URI uri = entry.getValue() == null ?
                    wellKnownUriByPrefix( entry.getKey() )
                        .orElseThrow( () -> new Exception( "Used prefix " + entry.getKey() + " is not well-known" ) ) :
                    entry.getValue();
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
