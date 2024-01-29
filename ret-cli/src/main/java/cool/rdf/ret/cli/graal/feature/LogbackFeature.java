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

package cool.rdf.ret.cli.graal.feature;

import org.graalvm.nativeimage.hosted.Feature;

import static org.graalvm.nativeimage.hosted.RuntimeClassInitialization.initializeAtBuildTime;
import static org.graalvm.nativeimage.hosted.RuntimeReflection.register;

public class LogbackFeature implements Feature {
    @Override
    public void beforeAnalysis( final BeforeAnalysisAccess access ) {
        initializeAtBuildTime( org.slf4j.LoggerFactory.class );
        initializeAtBuildTime( ch.qos.logback.classic.Level.class );
        initializeAtBuildTime( ch.qos.logback.classic.Logger.class );
        initializeAtBuildTime( ch.qos.logback.classic.PatternLayout.class );
        initializeAtBuildTime( ch.qos.logback.core.util.Loader.class );
        initializeAtBuildTime( ch.qos.logback.core.util.StatusPrinter.class );
        initializeAtBuildTime( ch.qos.logback.core.status.InfoStatus.class );
        initializeAtBuildTime( ch.qos.logback.core.status.StatusBase.class );
        initializeAtBuildTime( ch.qos.logback.core.spi.AppenderAttachableImpl.class );
        initializeAtBuildTime( "ch.qos.logback.core.pattern.parser.TokenStream$1" );
        initializeAtBuildTime( ch.qos.logback.core.pattern.parser.Parser.class );

        register( ch.qos.logback.classic.pattern.ClassOfCallerConverter.class );
        register( ch.qos.logback.classic.pattern.ContextNameConverter.class );
        register( ch.qos.logback.classic.pattern.DateConverter.class );
        register( ch.qos.logback.classic.pattern.ExtendedThrowableProxyConverter.class );
        register( ch.qos.logback.classic.pattern.FileOfCallerConverter.class );
        register( ch.qos.logback.classic.pattern.LevelConverter.class );
        register( ch.qos.logback.classic.pattern.LineOfCallerConverter.class );
        register( ch.qos.logback.classic.pattern.LineSeparatorConverter.class );
        register( ch.qos.logback.classic.pattern.LocalSequenceNumberConverter.class );
        register( ch.qos.logback.classic.pattern.LoggerConverter.class );
        register( ch.qos.logback.classic.pattern.MDCConverter.class );
        register( ch.qos.logback.classic.pattern.MarkerConverter.class );
        register( ch.qos.logback.classic.pattern.MessageConverter.class );
        register( ch.qos.logback.classic.pattern.MethodOfCallerConverter.class );
        register( ch.qos.logback.classic.pattern.NopThrowableInformationConverter.class );
        register( ch.qos.logback.classic.pattern.PropertyConverter.class );
        register( ch.qos.logback.classic.pattern.RelativeTimeConverter.class );
        register( ch.qos.logback.classic.pattern.RootCauseFirstThrowableProxyConverter.class );
        register( ch.qos.logback.classic.pattern.ThreadConverter.class );
        register( ch.qos.logback.classic.pattern.ThrowableProxyConverter.class );
        register( ch.qos.logback.classic.pattern.color.HighlightingCompositeConverter.class );
        register( ch.qos.logback.core.pattern.IdentityCompositeConverter.class );
        register( ch.qos.logback.core.pattern.ReplacingCompositeConverter.class );
        register( ch.qos.logback.core.pattern.color.BlackCompositeConverter.class );
        register( ch.qos.logback.core.pattern.color.BoldBlueCompositeConverter.class );
        register( ch.qos.logback.core.pattern.color.BoldCyanCompositeConverter.class );
        register( ch.qos.logback.core.pattern.color.BoldGreenCompositeConverter.class );
        register( ch.qos.logback.core.pattern.color.BoldMagentaCompositeConverter.class );
        register( ch.qos.logback.core.pattern.color.BoldRedCompositeConverter.class );
        register( ch.qos.logback.core.pattern.color.BoldWhiteCompositeConverter.class );
        register( ch.qos.logback.core.pattern.color.BoldYellowCompositeConverter.class );
        register( ch.qos.logback.core.pattern.color.CyanCompositeConverter.class );
        register( ch.qos.logback.core.pattern.color.GrayCompositeConverter.class );
        register( ch.qos.logback.core.pattern.color.GreenCompositeConverter.class );
        register( ch.qos.logback.core.pattern.color.MagentaCompositeConverter.class );
        register( ch.qos.logback.core.pattern.color.RedCompositeConverter.class );
        register( ch.qos.logback.core.pattern.color.WhiteCompositeConverter.class );
        register( ch.qos.logback.core.pattern.color.YellowCompositeConverter.class );
        register( ch.qos.logback.core.util.StatusPrinter.class );
    }
}
