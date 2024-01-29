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
    }
}
