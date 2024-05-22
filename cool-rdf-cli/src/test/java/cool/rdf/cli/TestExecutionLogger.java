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

package cool.rdf.cli;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class TestExecutionLogger implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
    private static final Logger LOG = LoggerFactory.getLogger( TestExecutionLogger.class );

    @Override
    public void beforeTestExecution( final ExtensionContext context ) throws Exception {
        LOG.info( "Run test " + context.getDisplayName() );
    }

    @Override
    public void afterTestExecution( final ExtensionContext context ) {
        final Optional<Throwable> executionException = context.getExecutionException();
        if ( executionException.isPresent() ) {
            LOG.info( "Exception in test {}:", context.getDisplayName(), executionException.get() );
        } else {
            LOG.info( "         {}: success", context.getDisplayName() );
        }
    }
}
