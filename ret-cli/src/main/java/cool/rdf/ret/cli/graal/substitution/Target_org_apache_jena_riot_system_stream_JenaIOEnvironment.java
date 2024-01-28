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

package cool.rdf.ret.cli.graal.substitution;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.TargetClass;
import org.apache.jena.riot.system.stream.JenaIOEnvironment;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

import java.io.Serial;

/**
 * Substitution class for {@link JenaIOEnvironment}
 */
@SuppressWarnings( "unused" )
@TargetClass( JenaIOEnvironment.class )
public final class Target_org_apache_jena_riot_system_stream_JenaIOEnvironment {
    @Alias
    @RecomputeFieldValue( kind = RecomputeFieldValue.Kind.FromAlias )
    static Logger log = new NOPLogger() {
        @Serial
        private static final long serialVersionUID = 5468256729459336798L;
    };
}
