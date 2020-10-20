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
import de.atextor.owlcli.write.Write;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@CommandLine.Command( name = "write",
    description = "Serialize an ontology",
    descriptionHeading = "%n@|bold Description|@:%n%n",
    parameterListHeading = "%n@|bold Parameters|@:%n",
    optionListHeading = "%n@|bold Options|@:%n",
    footer = "%nSee the online documentation for details:%n" +
        "https://atextor.de/owl-cli/main/" + OWLCLIConfig.VERSION + "/usage.html#write-command"
)
public class OWLCLIWriteCommand extends AbstractCommand implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger( OWLCLIWriteCommand.class );

    private static final Configuration config = Write.DEFAULT_CONFIGURATION;

    @CommandLine.Mixin
    LoggingMixin loggingMixin;

    @CommandLine.Option( names = { "--format" },
        description = "Output file format, one of ${COMPLETION-CANDIDATES} (Default: ${DEFAULT-VALUE})" )
    private final Configuration.Format format = config.format;

    @CommandLine.Parameters( paramLabel = "INPUT", description = "File name or - for stdin", arity = "1",
        index = "0" )
    private String input;

    @CommandLine.Parameters( paramLabel = "OUTPUT",
        description = "File name or - for stdout. If left out, the input file name is used, e.g. foo.ttl -> " +
            "foo.svg or stdout if INPUT is -.",
        arity = "0..1", index = "1" )
    private String output;

    @Override
    public void run() {
        final Configuration configuration = Configuration.builder()
            .format( format )
            .build();

        openInput( input ).flatMap( inputStream -> {
            openOutput( output ).forEach( outputStream -> {
                System.out.println( "x" );
            } )
        } ).onFailure( this::exitWithErrorMessage );
    }

    protected void exitWithErrorMessage( final Throwable throwable ) {
        if ( loggingMixin.getVerbosity().length == 0 ) {
            System.err.println( "Error: " + throwable.getMessage() );
        } else {
            LOG.warn( "Error: " + throwable.getMessage() );
        }
        commandFailed();
    }
}
