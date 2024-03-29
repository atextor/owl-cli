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

import de.atextor.owlcli.infer.Configuration;
import de.atextor.owlcli.infer.Inferrer;
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
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@CommandLine.Command( name = "infer",
    description = "Runs an OWL reasoner on an ontology",
    descriptionHeading = "%n@|bold Description|@:%n%n",
    parameterListHeading = "%n@|bold Parameters|@:%n",
    optionListHeading = "%n@|bold Options|@:%n",
    footer = "%nSee the online documentation for details:%n" +
        "https://atextor.de/owl-cli/main/" + OWLCLIConfig.VERSION + "/usage.html#infer-command"
)
public class OWLCLIInferCommand extends AbstractCommand implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger( OWLCLIInferCommand.class );

    private static final Configuration config = Inferrer.DEFAULT_CONFIGURATION;

    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    @CommandLine.Parameters( paramLabel = "INPUT", description = "File name, URL, or - for stdin", arity = "1",
        index = "0" )
    private String input;

    @CommandLine.Parameters( paramLabel = "OUTPUT",
        description = "File name or - for stdout. If left out, output is written to stdout.",
        arity = "0..1", index = "1" )
    private String output;

    @Override
    public void run() {
        final Configuration.ConfigurationBuilder configurationBuilder = Configuration.builder();

        final Inferrer inferrer = new Inferrer();

        if ( input.toLowerCase().startsWith( "http:" ) || input.toLowerCase().startsWith( "https:" ) ) {
            final Configuration configuration = configurationBuilder.build();
            try {
                final URL inputUrl = new URL( input );
                openOutput( input, output != null ? output : "-", "ttl" )
                    .map( outputStream -> inferrer.infer( inputUrl, outputStream, configuration ) )
                    .onFailure( throwable -> exitWithErrorMessage( LOG, loggingMixin, throwable ) );
                return;
            } catch ( final MalformedURLException exception ) {
                exitWithErrorMessage( LOG, loggingMixin, exception );
            }
        }

        openInput( input ).flatMap( inputStream -> {
                final Configuration configuration = configurationBuilder.build();
                return openOutput( input, output != null ? output : "-", "ttl" )
                    .flatMap( outputStream -> inferrer.infer( inputStream, outputStream, configuration ) );
            }
        ).onFailure( throwable -> exitWithErrorMessage( LOG, loggingMixin, throwable ) );
    }
}
