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
import org.apache.jena.rdf.model.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Set;
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

    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    @CommandLine.Option( names = { "-o", "--out" },
        description = "Output file format, one of ${COMPLETION-CANDIDATES} (Default: ${DEFAULT-VALUE})" )
    private Configuration.Format outputFormat = config.outputFormat;

    @CommandLine.Option( names = { "-i", "--input" },
        description = "Input file format, one of ${COMPLETION-CANDIDATES} (Default: ${DEFAULT-VALUE})" )
    private Configuration.Format inputFormat = config.inputFormat;

    @CommandLine.Option( names = { "-p", "--prefix" },
        description = "Prefixes to add as @prefix. (Default: ${DEFAULT-VALUE})" )
    private Map<String, URI> prefixMap = FormattingStyle.DEFAULT.knownPrefixes.stream().collect( Collectors.toMap(
        FormattingStyle.KnownPrefix::getPrefix, FormattingStyle.KnownPrefix::getIri ) );

    @CommandLine.Option( names = { "--prefixalign" },
        description = "Alignment of @prefix statements, one of ${COMPLETION-CANDIDATES} (Default: ${DEFAULT-VALUE})" )
    private FormattingStyle.Alignment alignPrefixes = FormattingStyle.DEFAULT.alignPrefixes;

    @CommandLine.Option( names = { "--encoding" },
        description = "Output encoding, one of ${COMPLETION-CANDIDATES} (Default: ${DEFAULT-VALUE})" )
    private FormattingStyle.Charset encoding = FormattingStyle.DEFAULT.charset;

    @CommandLine.Option( names = { "--doubleFormat" },
        description = "Defines how double numbers are formatted (Default: ${DEFAULT-VALUE})" )
    private NumberFormat doubleFormat = FormattingStyle.DEFAULT.doubleFormat;

    @CommandLine.Option( names = { "--endofline" },
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

    @CommandLine.Parameters( paramLabel = "INPUT", description = "File name, URL, or - for stdin", arity = "1",
        index = "0" )
    private String input;

    @CommandLine.Parameters( paramLabel = "OUTPUT",
        description = "File name or - for stdout. If left out, output is written to stdout.",
        arity = "0..1", index = "1" )
    private String output;

    @Override
    public void run() {

        final Configuration.ConfigurationBuilder configurationBuilder = Configuration.builder()
            .outputFormat( outputFormat )
            .inputFormat( inputFormat );

        initJena();

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
    }

    static class NumberFormatConverter implements CommandLine.ITypeConverter<NumberFormat> {
        @Override
        public NumberFormat convert( final String value ) throws Exception {
            return new DecimalFormat( value );
        }
    }

    class PropertyConverter implements CommandLine.ITypeConverter<Property> {
        @Override
        public Property convert( final String value ) throws Exception {
            final String propertyUri = OWLCLIWriteCommand.this.prefixMap.entrySet().stream()
                .filter( entry -> value.startsWith( entry.getKey() + ":" ) )
                .findAny()
                .map( entry -> entry.getValue().toString() + value.substring( ( entry.getKey() + ":" ).length() ) )
                .orElse( value );
            return ResourceFactory.createProperty( propertyUri );
        }
    }
}
