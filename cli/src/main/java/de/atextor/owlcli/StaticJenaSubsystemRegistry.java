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

package de.atextor.owlcli;

import org.apache.jena.sys.InitJenaCore;
import org.apache.jena.sys.JenaSubsystemLifecycle;
import org.apache.jena.sys.JenaSubsystemRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements Jena's internal module registry, but unlike the default implementation
 * {@link org.apache.jena.sys.JenaSubsystemRegistryBasic}, it does not depend on the service loader
 * mechanism, which is disabled in OWL-CLI's GraalVM build.
 * The modules list therefore must contain all classes that are mentioned in the respective
 * META-INF/services/org.apache.jena.sys.JenaSubsystemLifcycle of the used Jena dependencies.
 */
public class StaticJenaSubsystemRegistry implements JenaSubsystemRegistry {
    private final List<JenaSubsystemLifecycle> modules = List.of( new InitJenaCore() );

    @Override
    public void load() {
        // Nothing to do
    }

    @Override
    public void add( final JenaSubsystemLifecycle module ) {
        // Nothing to do
    }

    @Override
    public boolean isRegistered( final JenaSubsystemLifecycle module ) {
        return modules.contains( module );
    }

    @Override
    public void remove( final JenaSubsystemLifecycle module ) {
        // Nothing to do
    }

    @Override
    public int size() {
        return modules.size();
    }

    @Override
    public boolean isEmpty() {
        return modules.isEmpty();
    }

    @Override
    public List<JenaSubsystemLifecycle> snapshot() {
        return new ArrayList<>( modules );
    }
}
