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

package cool.rdf.ret.core.model.impl;

import cool.rdf.ret.core.model.RdfResource;
import lombok.experimental.Delegate;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.shared.PropertyNotFoundException;
import org.apache.jena.vocabulary.RDF;

import java.util.List;

import static cool.rdf.ret.core.util.IteratorStream.stream;

public class DefaultRdfResource extends DefaultRdfNode implements RdfResource {
    private final @Delegate Resource resource;

    public DefaultRdfResource( final Resource resource ) {
        super( resource );
        this.resource = resource;
    }

    public Statement property( final Property property ) {
        final List<Statement> statements = stream( listProperties( property ) ).toList();
        if ( statements.size() != 1 ) {
            throw new PropertyNotFoundException( property );
        }
        return statements.get( 0 );
    }

    @Override
    public Literal literalValue( final Property property ) {
        final RDFNode object = property( property ).getObject();
        if ( !object.isLiteral() ) {
            throw new PropertyNotFoundException( property );
        }
        return object.asLiteral();
    }

    @Override
    public boolean isList() {
        return resource.equals( RDF.Nodes.nil ) || ( resource.hasProperty( RDF.rest ) && resource.hasProperty( RDF.first ) );
    }
}
