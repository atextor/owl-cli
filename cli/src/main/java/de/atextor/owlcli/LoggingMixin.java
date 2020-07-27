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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import static picocli.CommandLine.Spec.Target.MIXEE;

public class LoggingMixin {
    private @CommandLine.Spec( MIXEE )
    CommandLine.Model.CommandSpec mixee;

    private boolean[] verbosity = new boolean[0];

    private static LoggingMixin getTopLevelCommandLoggingMixin( final CommandLine.Model.CommandSpec commandSpec ) {
        return ( (OWLCLI) commandSpec.root().userObject() ).loggingMixin;
    }

    public static int executionStrategy( final CommandLine.ParseResult parseResult ) {
        getTopLevelCommandLoggingMixin( parseResult.commandSpec() ).configureLoggers();
        return new CommandLine.RunLast().execute( parseResult );
    }

    @CommandLine.Option( names = { "-v", "--verbose" }, description = {
        "Specify multiple -v options to increase verbosity,",
        "e.g. use `-v`, `-vv` or `-vvv` for more details" } )
    public void setVerbose( final boolean[] verbosity ) {
        getTopLevelCommandLoggingMixin( mixee ).verbosity = verbosity;
    }

    public boolean[] getVerbosity() {
        return getTopLevelCommandLoggingMixin( mixee ).verbosity;
    }

    private Level calcLogLevel() {
        return switch ( getVerbosity().length ) {
            case 0:
                yield Level.WARN;
            case 1:
                yield Level.INFO;
            case 2:
                yield Level.DEBUG;
            default:
                yield Level.TRACE;
        };
    }

    public void configureLoggers() {
        final Level level = getTopLevelCommandLoggingMixin( mixee ).calcLogLevel();
        final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        final ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
        appender.setName( "ConsoleLogger" );
        appender.setContext( loggerContext );

        final ThresholdFilter filter = new ThresholdFilter();
        filter.setLevel( level.toString() );
        filter.setContext( loggerContext );
        filter.start();
        appender.addFilter( filter );

        final PatternLayoutEncoder layoutEncoder = new PatternLayoutEncoder();
        layoutEncoder.setContext( loggerContext );
        layoutEncoder.setPattern( "%date %level %logger{10}: %msg%n" );
        layoutEncoder.start();
        appender.setEncoder( layoutEncoder );
        appender.start();

        final Logger root = loggerContext.getLogger( Logger.ROOT_LOGGER_NAME );
        root.detachAndStopAllAppenders();
        root.setLevel( level );
        root.addAppender( appender );
    }
}
