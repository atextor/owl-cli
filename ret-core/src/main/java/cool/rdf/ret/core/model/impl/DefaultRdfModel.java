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

import cool.rdf.ret.core.model.RdfModel;
import cool.rdf.ret.core.model.RdfProperty;
import cool.rdf.ret.core.model.RdfResource;
import cool.rdf.ret.core.model.RdfStatement;
import lombok.experimental.Delegate;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.stream.Stream;

import static cool.rdf.ret.core.util.IteratorStream.stream;

public class DefaultRdfModel implements RdfModel {
    private final @Delegate Model model;

    public DefaultRdfModel( final Model model ) {
        this.model = model;
    }

    private RdfResource mapResource( final Resource resource ) {
        return new DefaultRdfResource( resource );
    }

    private RdfProperty mapProperty( final Property property ) {
        return new DefaultRdfProperty( property );
    }

    private RDFNode mapResourceRdfNode( final RDFNode node ) {
        return node.isResource() ? mapResource( node.asResource() ) : node;
    }

    private RdfStatement mapResourcesInStatement( final Statement statement ) {
        return new DefaultRdfStatement( createStatement( mapResource( statement.getSubject() ), mapProperty( statement.getPredicate() ),
            mapResourceRdfNode( statement.getObject() ) ) );
    }

    @Override
    public Stream<RdfResource> subjects() {
        return stream( listSubjects() ).map( this::mapResource );
    }

    @Override
    public Stream<String> namespaces() {
        return stream( listNameSpaces() );
    }

    @Override
    public Stream<RdfResource> resourcesWithProperty( final Property property ) {
        return stream( listResourcesWithProperty( property ) ).map( this::mapResource );
    }

    @Override
    public Stream<RdfResource> resourcesWithProperty( final Property property, final RDFNode object ) {
        return stream( listResourcesWithProperty( property, object ) ).map( this::mapResource );
    }

    @Override
    public Stream<RDFNode> objects() {
        return stream( listObjects() ).map( this::mapResourceRdfNode );
    }

    @Override
    public Stream<RDFNode> objectsOfProperty( final Property property ) {
        return stream( listObjectsOfProperty( property ) ).map( this::mapResourceRdfNode );
    }

    @Override
    public Stream<RDFNode> objectsOfProperty( final Resource subject, final Property property ) {
        return stream( listObjectsOfProperty( subject, property ) ).map( this::mapResourceRdfNode );
    }

    @Override
    public Stream<RdfStatement> statements() {
        return stream( listStatements() ).map( this::mapResourcesInStatement );
    }

    @Override
    public Stream<RdfStatement> statements( final Resource subject, final Property predicate, final RDFNode object ) {
        return stream( listStatements( subject, predicate, object ) ).map( this::mapResourcesInStatement );
    }

    @Override
    public Stream<RdfStatement> literalStatements( final Resource subject, final Property predicate, final boolean object ) {
        return stream( listLiteralStatements( subject, predicate, object ) ).map( this::mapResourcesInStatement );
    }

    @Override
    public Stream<RdfStatement> literalStatements( final Resource subject, final Property predicate, final char object ) {
        return stream( listLiteralStatements( subject, predicate, object ) ).map( this::mapResourcesInStatement );
    }

    @Override
    public Stream<RdfStatement> literalStatements( final Resource subject, final Property predicate, final long object ) {
        return stream( listLiteralStatements( subject, predicate, object ) ).map( this::mapResourcesInStatement );
    }

    @Override
    public Stream<RdfStatement> literalStatements( final Resource subject, final Property predicate, final int object ) {
        return stream( listLiteralStatements( subject, predicate, object ) ).map( this::mapResourcesInStatement );
    }

    @Override
    public Stream<RdfStatement> literalStatements( final Resource subject, final Property predicate, final float object ) {
        return stream( listLiteralStatements( subject, predicate, object ) ).map( this::mapResourcesInStatement );
    }

    @Override
    public Stream<RdfStatement> literalStatements( final Resource subject, final Property predicate, final double object ) {
        return stream( listLiteralStatements( subject, predicate, object ) ).map( this::mapResourcesInStatement );
    }

    @Override
    public Stream<RdfStatement> literalStatements( final Resource subject, final Property predicate, final String object ) {
        return stream( listStatements( subject, predicate, object ) ).map( this::mapResourcesInStatement );
    }

    @Override
    public Stream<RdfStatement> literalStatements( final Resource subject, final Property predicate, final String object,
        final String language ) {
        return stream( listStatements( subject, predicate, object, language ) ).map( this::mapResourcesInStatement );
    }

    @Override
    public Stream<RdfResource> resourcesWithProperty( final Property p, final boolean o ) {
        return stream( listResourcesWithProperty( p, o ) ).map( this::mapResource );
    }

    @Override
    public Stream<RdfResource> resourcesWithProperty( final Property p, final long o ) {
        return stream( listResourcesWithProperty( p, o ) ).map( this::mapResource );
    }

    @Override
    public Stream<RdfResource> resourcesWithProperty( final Property p, final char o ) {
        return stream( listResourcesWithProperty( p, o ) ).map( this::mapResource );
    }

    @Override
    public Stream<RdfResource> resourcesWithProperty( final Property p, final float o ) {
        return stream( listResourcesWithProperty( p, o ) ).map( this::mapResource );
    }

    @Override
    public Stream<RdfResource> resourcesWithProperty( final Property p, final double o ) {
        return stream( listResourcesWithProperty( p, o ) ).map( this::mapResource );
    }

    @Override
    public Stream<RdfResource> resourcesWithProperty( final Property p, final Object o ) {
        return stream( listResourcesWithProperty( p, o ) ).map( this::mapResource );
    }

    @Override
    public Stream<RdfResource> subjectsWithProperty( final Property p, final String o ) {
        return stream( listSubjectsWithProperty( p, o ) ).map( this::mapResource );
    }

    @Override
    public Stream<RdfResource> resourcesWithProperty( final Property p, final String o, final String l ) {
        return stream( listSubjectsWithProperty( p, o, l ) ).map( this::mapResource );
    }
}
