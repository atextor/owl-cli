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

package cool.rdf.ret.cli.substitution;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.apache.jena.util.FileManagerImpl;
import org.apache.jena.util.Locator;

import java.util.List;

/**
 * Substitution class for {@link FileManagerImpl}
 */
@SuppressWarnings( { "unused", "allJavadoc" } )
@TargetClass( FileManagerImpl.class )
public final class Target_org_apache_jena_util_FileManagerImpl {
    /**
     * See fmHandlers field in original FileManagerImpl
     */
    @SuppressWarnings( { "unused", "ProtectedMemberInFinalClass" } )
    @Alias
    protected List<Locator> fmHandlers;

    /**
     * See addLocator() method in original FileManagerImpl
     *
     * @param loc see original method
     */
    @SuppressWarnings( "unused" )
    @Substitute
    public void addLocator( final Locator loc ) {
        fmHandlers.add( loc );
    }
}