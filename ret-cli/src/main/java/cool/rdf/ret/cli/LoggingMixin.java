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

/**
 * A <a href="https://picocli.info/#_mixing_options_and_positional_parameters">mixin</a> for logging functionality that is
 * shared across commands. The mixin sets up and uses logback.
 */
public class LoggingMixin {
    @SuppressWarnings( { "unused", "SpellCheckingInspection" } )
    private @CommandLine.Spec( MIXEE )
    CommandLine.Model.CommandSpec mixee;

    private boolean[] verbosity = new boolean[0];

    private static LoggingMixin getTopLevelCommandLoggingMixin( final CommandLine.Model.CommandSpec commandSpec ) {
        return ( (Ret) commandSpec.root().userObject() ).loggingMixin;
    }

    /**
     * The method to set as the CLI execution strategy, to enable the logging mixin
     *
     * @param parseResult the result of the respective arguments parsing process
     * @return the execution result
     */
    public static int executionStrategy( final CommandLine.ParseResult parseResult ) {
        getTopLevelCommandLoggingMixin( parseResult.commandSpec() ).configureLoggers();
        return new CommandLine.RunLast().execute( parseResult );
    }

    /**
     * The option that is injected into commands using this mixin
     *
     * @param verbosity the verbosity, one array entry for every stacked -v occurrence
     */
    @SuppressWarnings( "unused" )
    @CommandLine.Option( names = { "-v", "--verbose" }, description = {
        "Specify multiple -v options to increase verbosity,",
        "e.g. use `-v`, `-vv` or `-vvv` for more details" } )
    public void setVerbose( final boolean[] verbosity ) {
        getTopLevelCommandLoggingMixin( mixee ).verbosity = verbosity;
    }

    /**
     * Gets the configured logging verbosity in raw format, with one array entry for every stacked -v occurrence
     *
     * @return the verbosity
     */
    public boolean[] getVerbosity() {
        return getTopLevelCommandLoggingMixin( mixee ).verbosity;
    }

    /**
     * Determine the logback log level from the configured verbosity
     *
     * @return the log level
     */
    public Level calcLogLevel() {
        return switch ( getVerbosity().length ) {
            case 0 -> Level.WARN;
            case 1 -> Level.INFO;
            case 2 -> Level.DEBUG;
            default -> Level.TRACE;
        };
    }

    /**
     * Set up the logback logging structure
     */
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
