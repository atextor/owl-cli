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

package de.atextor.ret.cli;

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

    public Level calcLogLevel() {
        return switch ( getVerbosity().length ) {
            case 0 -> Level.WARN;
            case 1 -> Level.INFO;
            case 2 -> Level.DEBUG;
            default -> Level.TRACE;
        };
    }

    public void configureLoggers() {
        final Level level = getTopLevelCommandLoggingMixin( mixee ).calcLogLevel();
        final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        final ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
        appender.setName( "ConsoleLogger" );
        appender.setContext( loggerContext );
        appender.setTarget( "System.err" );

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
