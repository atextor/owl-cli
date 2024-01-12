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

package de.atextor.ret.cli;

import de.atextor.ret.core.Version;
import de.atextor.ret.infer.Configuration;
import de.atextor.ret.infer.Inferrer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import static de.atextor.ret.cli.RetInfer.COMMAND_NAME;

/**
 * The 'infer' subcommand
 */
@CommandLine.Command( name = COMMAND_NAME,
    description = "Runs an OWL reasoner on an ontology",
    descriptionHeading = "%n@|bold Description|@:%n%n",
    parameterListHeading = "%n@|bold Parameters|@:%n",
    optionListHeading = "%n@|bold Options|@:%n",
    footer = "%nSee the online documentation for details:%n" +
        "https://atextor.de/owl-cli/main/" + Version.VERSION + "/usage.html#infer-command"
)
public class RetInfer extends AbstractCommand implements Runnable {
    /**
     * The name of this subcommand
     */
    public static final String COMMAND_NAME = "infer";

    private static final Logger LOG = LoggerFactory.getLogger( RetInfer.class );

    @SuppressWarnings( "unused" )
    private static final Configuration config = Inferrer.DEFAULT_CONFIGURATION;

    @SuppressWarnings( "unused" )
    @CommandLine.Mixin
    LoggingMixin loggingMixin;

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
        final Configuration.ConfigurationBuilder configurationBuilder = Configuration.builder();

        final Inferrer inferrer = new Inferrer();

        if ( input.toLowerCase().startsWith( "http:" ) || input.toLowerCase().startsWith( "https:" ) ) {
            final Configuration configuration = configurationBuilder.build();
            try {
                final URL inputUrl = new URL( input );
                openOutput( input, output != null ? Optional.of( output ) : Optional.of( "-" ), "ttl" )
                    .map( outputStream -> inferrer.infer( inputUrl, outputStream, configuration ) )
                    .onFailure( throwable -> exitWithErrorMessage( LOG, loggingMixin, throwable ) );
                return;
            } catch ( final MalformedURLException exception ) {
                exitWithErrorMessage( LOG, loggingMixin, exception );
            }
        }

        openInput( input ).flatMap( inputStream -> {
                final Configuration configuration = configurationBuilder.build();
                return openOutput( input, output != null ? Optional.of( output ) : Optional.of( "-" ), "ttl" )
                    .flatMap( outputStream -> inferrer.infer( inputStream, outputStream, configuration ) );
            }
        ).onFailure( throwable -> exitWithErrorMessage( LOG, loggingMixin, throwable ) );
    }

    @Override
    public String commandName() {
        return COMMAND_NAME;
    }
}
