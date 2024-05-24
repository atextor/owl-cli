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

import cool.rdf.core.model.RdfNode;
import cool.rdf.core.model.RdfProperty;
import cool.rdf.core.model.RdfResource;
import cool.rdf.core.model.RdfStatement;
import lombok.experimental.Delegate;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

public class DefaultRdfStatement implements RdfStatement {
    private final @Delegate Statement statement;

    public DefaultRdfStatement( final Statement statement ) {
        this.statement = statement;
    }

    @Override
    public RdfResource subject() {
        final Resource resource = getResource();
        return resource instanceof final RdfResource rdfResource ? rdfResource : new DefaultRdfResource( resource );
    }

    @Override
    public RdfProperty predicate() {
        final Property property = getPredicate();
        return property instanceof final RdfProperty rdfProperty ? rdfProperty : new DefaultRdfProperty( property );
    }

    @Override
    public RdfNode object() {
        final RDFNode object = getObject();
        return object instanceof final RdfNode rdfNode ? rdfNode : new DefaultRdfNode( object );
    }
}