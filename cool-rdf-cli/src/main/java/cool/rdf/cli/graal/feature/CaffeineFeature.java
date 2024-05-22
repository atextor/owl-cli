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

package cool.rdf.cli.graal.feature;

import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.nativeimage.hosted.Feature;

@Platforms( Platform.HOSTED_ONLY.class )
public class CaffeineFeature implements Feature {
    @Override
    public void beforeAnalysis( final BeforeAnalysisAccess access ) {
        Native.forClass( "com.github.benmanes.caffeine.cache.BBHeader$ReadAndWriteCounterRef" )
            .registerClass().registerFields( "writeCounter" );
        Native.forClass( "com.github.benmanes.caffeine.cache.BBHeader$ReadCounterRef" )
            .registerClass().registerFields( "readCounter" );
        Native.forClass( "com.github.benmanes.caffeine.cache.BLCHeader$DrainStatusRef" )
            .registerClass().registerFields( "drainStatus" );
        Native.forClass( "com.github.benmanes.caffeine.cache.BaseMpscLinkedArrayQueueColdProducerFields" )
            .registerClass().registerFields( "producerLimit" );
        Native.forClass( "com.github.benmanes.caffeine.cache.BaseMpscLinkedArrayQueueConsumerFields" )
            .registerClass().registerFields( "consumerIndex" );
        Native.forClass( "com.github.benmanes.caffeine.cache.BaseMpscLinkedArrayQueueProducerFields" )
            .registerClass().registerFields( "producerIndex" );
        Native.forClass( "com.github.benmanes.caffeine.cache.FS" )
            .registerClass().registerFields( "key", "value" );
        Native.forClass( "com.github.benmanes.caffeine.cache.FSMS" )
            .registerClass().registerAllConstructors().registerAllMethods().registerAllFields();
        Native.forClass( "com.github.benmanes.caffeine.cache.PS" )
            .registerClass().registerAllConstructors().registerFields( "key", "value" );
        Native.forClass( "com.github.benmanes.caffeine.cache.PSMS" )
            .registerClass().registerAllConstructors();
        Native.forClass( "com.github.benmanes.caffeine.cache.PSMW" )
            .registerClass().registerConstructor();
        Native.forClass( "com.github.benmanes.caffeine.cache.SSMS" )
            .registerClass().registerAllConstructors().registerAllMethods().registerAllFields();
        Native.forClass( "com.github.benmanes.caffeine.cache.SSMW" )
            .registerClass().registerAllConstructors().registerAllMethods().registerAllFields();
        Native.forClass( "com.github.benmanes.caffeine.cache.WSMS" )
            .registerClass().registerAllConstructors().registerAllMethods().registerAllFields();
        Native.forClass( "com.github.benmanes.caffeine.cache.WSSMW" )
            .registerClass().registerAllConstructors().registerAllMethods().registerAllFields();
        Native.forClass( "com.github.benmanes.caffeine.cache.FSMW" )
            .registerClass().registerAllConstructors().registerAllMethods().registerAllFields();
        Native.forClass( "com.github.benmanes.caffeine.cache.StripedBuffer" )
            .registerClass().registerFields( "tableBusy" );
        Native.forClass( "com.github.benmanes.caffeine.cache.CacheLoader" )
            .registerClass().registerMethod( "loadAll", Iterable.class );
    }
}
