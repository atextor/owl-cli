/*
 * Copyright 2022 Andreas Textor
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

package de.atextor.owlcli.substitution;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.apache.jena.base.module.SubsystemLifecycle;
import org.apache.jena.base.module.SubsystemRegistry;
import org.apache.jena.base.module.SubsystemRegistryServiceLoader;
import org.apache.jena.rdfs.sys.InitRDFS;
import org.apache.jena.riot.system.InitRIOT;
import org.apache.jena.sparql.system.InitARQ;
import org.apache.jena.sys.InitJenaCore;

import java.util.List;

@TargetClass( SubsystemRegistryServiceLoader.class )
@SuppressWarnings( "unused" )
public final class Target_org_apache_jena_base_module_SubsystemRegistryServiceLoader<T extends SubsystemLifecycle> implements SubsystemRegistry<T> {
    @Override
    @Substitute
    @SuppressWarnings( "unchecked" )
    public void load() {
        add( (T) new InitJenaCore() );
        add( (T) new InitRIOT() );
        add( (T) new InitARQ() );
        add( (T) new InitRDFS() );
    }

    @Override
    @Alias
    public void add( final T module ) {
    }

    @Override
    @Alias
    public boolean isRegistered( final T module ) {
        return false;
    }

    @Override
    @Alias
    public void remove( final T module ) {

    }

    @Override
    @Alias
    public int size() {
        return 0;
    }

    @Override
    @Alias
    public boolean isEmpty() {
        return false;
    }

    @Override
    @Alias
    public List<T> snapshot() {
        return null;
    }
}
