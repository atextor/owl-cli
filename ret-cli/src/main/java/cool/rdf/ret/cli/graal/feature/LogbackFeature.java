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

import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.nativeimage.hosted.Feature;

@Platforms( Platform.HOSTED_ONLY.class )
public class LogbackFeature implements Feature {
    @Override
    public void beforeAnalysis( final BeforeAnalysisAccess access ) {
        Native.forClass( org.slf4j.LoggerFactory.class ).initializeAtBuildTime();
        Native.forClass( ch.qos.logback.classic.Level.class ).initializeAtBuildTime();
        Native.forClass( ch.qos.logback.classic.Logger.class ).initializeAtBuildTime();
        Native.forClass( ch.qos.logback.classic.PatternLayout.class ).initializeAtBuildTime();
        Native.forClass( ch.qos.logback.core.util.Loader.class ).initializeAtBuildTime();
        Native.forClass( ch.qos.logback.core.util.StatusPrinter.class ).initializeAtBuildTime();
        Native.forClass( ch.qos.logback.core.status.InfoStatus.class ).initializeAtBuildTime();
        Native.forClass( ch.qos.logback.core.status.StatusBase.class ).initializeAtBuildTime();
        Native.forClass( ch.qos.logback.core.spi.AppenderAttachableImpl.class ).initializeAtBuildTime();
        Native.forClass( "ch.qos.logback.core.pattern.parser.TokenStream$1" ).initializeAtBuildTime();
        Native.forClass( ch.qos.logback.core.pattern.parser.Parser.class ).initializeAtBuildTime();

        Native.forClass( ch.qos.logback.classic.pattern.ClassOfCallerConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.classic.pattern.ContextNameConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.classic.pattern.DateConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.classic.pattern.ExtendedThrowableProxyConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.classic.pattern.FileOfCallerConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.classic.pattern.LevelConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.classic.pattern.LineOfCallerConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.classic.pattern.LineSeparatorConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.classic.pattern.LocalSequenceNumberConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.classic.pattern.LoggerConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.classic.pattern.MDCConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.classic.pattern.MarkerConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.classic.pattern.MessageConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.classic.pattern.MethodOfCallerConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.classic.pattern.NopThrowableInformationConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.classic.pattern.PropertyConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.classic.pattern.RelativeTimeConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.classic.pattern.RootCauseFirstThrowableProxyConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.classic.pattern.ThreadConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.classic.pattern.ThrowableProxyConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.classic.pattern.color.HighlightingCompositeConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.core.pattern.IdentityCompositeConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.core.pattern.ReplacingCompositeConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.core.pattern.color.BlackCompositeConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.core.pattern.color.BoldBlueCompositeConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.core.pattern.color.BoldCyanCompositeConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.core.pattern.color.BoldGreenCompositeConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.core.pattern.color.BoldMagentaCompositeConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.core.pattern.color.BoldRedCompositeConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.core.pattern.color.BoldWhiteCompositeConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.core.pattern.color.BoldYellowCompositeConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.core.pattern.color.CyanCompositeConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.core.pattern.color.GrayCompositeConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.core.pattern.color.GreenCompositeConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.core.pattern.color.MagentaCompositeConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.core.pattern.color.RedCompositeConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.core.pattern.color.WhiteCompositeConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.core.pattern.color.YellowCompositeConverter.class ).registerClass();
        Native.forClass( ch.qos.logback.core.util.StatusPrinter.class ).registerClass();

        Native.withModule( LogbackFeature.class.getClassLoader().getUnnamedModule() )
            .addResource( "org/slf4j/impl/StaticLoggerBinder.class" );
    }
}
