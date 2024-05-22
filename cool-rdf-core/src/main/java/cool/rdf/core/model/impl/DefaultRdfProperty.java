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

package cool.rdf.core.model.impl;

import cool.rdf.core.model.RdfProperty;
import lombok.experimental.Delegate;
import org.apache.jena.rdf.model.Property;

public class DefaultRdfProperty extends DefaultRdfResource implements RdfProperty {
    private final @Delegate Property property;

    public DefaultRdfProperty( final Property property ) {
        super( property );
        this.property = property;
    }
}
