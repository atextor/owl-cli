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

package cool.rdf.diagram.owl;

import cool.rdf.diagram.owl.mappers.NameMapper;
import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.model.IRI;

public class TestNameMapper implements NameMapper {

    @Override
    public String getName( final HasIRI object ) {
        return getName( object.getIRI() );
    }

    @Override
    public String getName( final IRI object ) {
        return object.getFragment();
    }
}
