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

public class JenaFeature implements Feature {
    @Override
    public void beforeAnalysis( final BeforeAnalysisAccess access ) {
        initializeAtBuildTime( org.apache.jena.base.module.SubsystemRegistryServiceLoader.class );
        initializeAtBuildTime( org.apache.jena.util.LocationMapper.class );
        initializeAtBuildTime( org.apache.jena.riot.system.stream.JenaIOEnvironment.class );
    }
}
