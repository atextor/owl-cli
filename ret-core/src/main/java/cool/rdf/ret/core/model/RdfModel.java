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

package cool.rdf.ret.core.model;

import cool.rdf.ret.core.model.impl.DefaultRdfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public interface RdfModel extends Model {
    String DEFAULT_EMPTY_PREFIX = "urn:ret:empty";

    static RdfModel fromModel( final Model model ) {
        return new DefaultRdfModel( model );
    }

    /**
     * Parses a string containing an RDF document in a given syntax into a model
     *
     * @param document the string containing literal RDF (i.e., not a file name or URL)
     * @param syntax the RDF syntax
     * @return the loaded model
     */
    static RdfModel fromDocument( final String document, final Lang syntax ) {
        return fromDocument( new ByteArrayInputStream( document.getBytes( StandardCharsets.UTF_8 ) ), syntax );
    }

    /**
     * Loads an RDF model in a given syntax from an input stream. The stream is not closed.
     *
     * @param input the input stream
     * @param syntax the syntax
     * @return the model
     */
    static RdfModel fromDocument( final InputStream input, final Lang syntax ) {
        return fromDocument( input, syntax, DEFAULT_EMPTY_PREFIX );
    }

    /**
     * Loads an RDF model in a given syntax from an input stream, with a given base URI. The stream is not closed.
     *
     * @param input the input stream
     * @param syntax the syntax
     * @param base the base URI
     * @return the model
     */
    static RdfModel fromDocument( final InputStream input, final Lang syntax, final String base ) {
        return fromModel( RDFParser.create()
            .source( input )
            .lang( syntax )
            .base( base )
            .toModel() );
    }

    /**
     * Parses a string containing an RDF/Turtle document into a model
     *
     * @param document the string containing literal RDF (i.e., not a file name or URL)
     * @return the loaded model
     */
    static RdfModel fromTurtle( final String document ) {
        return fromDocument( document, Lang.TURTLE );
    }

    static RdfModel fromTurtle( final URI uri ) {
        return fromDocument( uri, Lang.TURTLE );
    }

    static RdfModel fromDocument( final URI uri, final Lang syntax ) {
        return fromDocument( uri, syntax, DEFAULT_EMPTY_PREFIX );
    }

    static RdfModel fromDocument( final URI uri, final Lang syntax, final String base ) {
        return fromModel( RDFParser.create()
            .source( uri.toString() )
            .lang( syntax )
            .base( base )
            .toModel() );
    }

    /**
     * Similar to {@link Model#listSubjects()}}, list all resources which are subjects of statements.
     * <p>Subsequent operations on those resource may modify this model.</p>
     *
     * @return a stream of resources which are subjects of statements
     */
    Stream<RdfResource> subjects();

    /**
     * Similar to {@link Model#listNameSpaces()}, list all namespaces used by predicates and types in the model.
     *
     * @return a stream of every predicate and type namespace
     * @see org.apache.jena.shared.PrefixMapping
     */
    Stream<String> namespaces();

    /**
     * Similar to {@link Model#listResourcesWithProperty(Property)}, list all resources in this model that have the property.
     *
     * @param property the property
     * @return a stream of resources with the property
     */
    Stream<RdfResource> resourcesWithProperty( Property property );

    /**
     * Similar to {@link Model#listResourcesWithProperty(Property, RDFNode)}, list all resources in this model that have the property
     * with the object.
     *
     * @param property the property
     * @param object the object
     * @return a stream of resources with the property and the object
     */
    Stream<RdfResource> resourcesWithProperty( Property property, RDFNode object );

    /**
     * Similar to {@link Model#listObjects()}, list all objects in the model.
     *
     * @return a stream of all objects
     */
    Stream<RDFNode> objects();

    /**
     * Similar to {@link Model#listObjectsOfProperty(Property)}, list all objects of a given property
     *
     * @param property the property
     * @return the stream of objects
     */
    Stream<RDFNode> objectsOfProperty( Property property );

    /**
     * Similar to {@link Model#listObjectsOfProperty(Resource, Property)}, list the values of a property of a resource.
     *
     * @param subject the resource
     * @param property the property
     * @return a stream of values of the property of the resource
     */
    Stream<RDFNode> objectsOfProperty( Resource subject, Property property );

    /**
     * Similar to {@link Model#listStatements()}, lists all statements in a model.
     *
     * @return a stream of statements
     */
    Stream<RdfStatement> statements();

    /**
     * Similar to {@link Model#listStatements(Resource, Property, RDFNode)}, list all statements matching a pattern.
     * The statements selected are those whose subject matches the <code>subject</code> argument, whose predicate matches the
     * <code>predicate</code> argument and whose object matches the <code>object</code> argument. If an argument is <code>null</code> it
     * matches anything.</p>
     *
     * @param subject the subject of the pattern
     * @param predicate the predicate of the pattern
     * @param object the object of the pattern
     * @return a stream of matching statements
     */
    Stream<RdfStatement> statements( Resource subject, Property predicate, RDFNode object );

    /**
     * Similar to {@link org.apache.jena.rdf.model.ModelCon#listLiteralStatements(Resource, Property, boolean)}, list statements with
     * given subject, predicate and object values.
     *
     * @param subject the subject
     * @param predicate the predicate
     * @param object the object
     * @return a stream of matching statements
     */
    Stream<RdfStatement> literalStatements( Resource subject, Property predicate, boolean object );

    /**
     * Similar to {@link org.apache.jena.rdf.model.ModelCon#listLiteralStatements(Resource, Property, char)}, list statements with
     * given subject, predicate and object values.
     *
     * @param subject the subject
     * @param predicate the predicate
     * @param object the object
     * @return a stream of matching statements
     */
    Stream<RdfStatement> literalStatements( Resource subject, Property predicate, char object );

    /**
     * Similar to {@link org.apache.jena.rdf.model.ModelCon#listLiteralStatements(Resource, Property, long)}, list statements with
     * given subject, predicate and object values.
     *
     * @param subject the subject
     * @param predicate the predicate
     * @param object the object
     * @return a stream of matching statements
     */
    Stream<RdfStatement> literalStatements( Resource subject, Property predicate, long object );

    /**
     * Similar to {@link org.apache.jena.rdf.model.ModelCon#listLiteralStatements(Resource, Property, int)}, list statements with
     * given subject, predicate and object values.
     *
     * @param subject the subject
     * @param predicate the predicate
     * @param object the object
     * @return a stream of matching statements
     */
    Stream<RdfStatement> literalStatements( Resource subject, Property predicate, int object );

    /**
     * Similar to {@link org.apache.jena.rdf.model.ModelCon#listLiteralStatements(Resource, Property, float)}, list statements with
     * given subject, predicate and object values.
     *
     * @param subject the subject
     * @param predicate the predicate
     * @param object the object
     * @return a stream of matching statements
     */
    Stream<RdfStatement> literalStatements( Resource subject, Property predicate, float object );

    /**
     * Similar to {@link org.apache.jena.rdf.model.ModelCon#listLiteralStatements(Resource, Property, double)}, list statements with
     * given subject, predicate and object values.
     *
     * @param subject the subject
     * @param predicate the predicate
     * @param object the object
     * @return a stream of matching statements
     */
    Stream<RdfStatement> literalStatements( Resource subject, Property predicate, double object );

    /**
     * Similar to {@link org.apache.jena.rdf.model.ModelCon#listStatements(Resource, Property, String)}, list statements with
     * given subject, predicate and object values.
     *
     * @param subject the subject
     * @param predicate the predicate
     * @param object the object
     * @return a stream of matching statements
     */
    Stream<RdfStatement> literalStatements( Resource subject, Property predicate, String object );

    /**
     * Similar to {@link org.apache.jena.rdf.model.ModelCon#listStatements(Resource, Property, String, String)}, list statements with
     * given subject, predicate and object values.
     *
     * @param subject the subject
     * @param predicate the predicate
     * @param object the object
     * @param language the language code of the string
     * @return a stream of matching statements
     */
    Stream<RdfStatement> literalStatements( Resource subject, Property predicate, String object, String language );

    /**
     * Similar to {@link org.apache.jena.rdf.model.ModelCon#listResourcesWithProperty(Property, boolean)}, list the resources in this
     * model which have value o' for property p, where o' is the type literal corresponding to o.
     *
     * @param p the property
     * @param o the object
     * @return a stream of matching resources
     */
    Stream<RdfResource> resourcesWithProperty( Property p, boolean o );

    /**
     * Similar to {@link org.apache.jena.rdf.model.ModelCon#listResourcesWithProperty(Property, long)}, list the resources in this
     * model which have value o' for property p, where o' is the type literal corresponding to o.
     *
     * @param p the property
     * @param o the object
     * @return a stream of matching resources
     */
    Stream<RdfResource> resourcesWithProperty( Property p, long o );

    /**
     * Similar to {@link org.apache.jena.rdf.model.ModelCon#listResourcesWithProperty(Property, char)}, list the resources in this
     * model which have value o' for property p, where o' is the type literal corresponding to o.
     *
     * @param p the property
     * @param o the object
     * @return a stream of matching resources
     */
    Stream<RdfResource> resourcesWithProperty( Property p, char o );

    /**
     * Similar to {@link org.apache.jena.rdf.model.ModelCon#listResourcesWithProperty(Property, float)}, list the resources in this
     * model which have value o' for property p, where o' is the type literal corresponding to o.
     *
     * @param p the property
     * @param o the object
     * @return a stream of matching resources
     */
    Stream<RdfResource> resourcesWithProperty( Property p, float o );

    /**
     * Similar to {@link org.apache.jena.rdf.model.ModelCon#listResourcesWithProperty(Property, double)}, list the resources in this
     * model which have value o' for property p, where o' is the type literal corresponding to o.
     *
     * @param p the property
     * @param o the object
     * @return a stream of matching resources
     */
    Stream<RdfResource> resourcesWithProperty( Property p, double o );

    /**
     * Similar to {@link org.apache.jena.rdf.model.ModelCon#listResourcesWithProperty(Property, Object)}, list the resources in this
     * model which have value o' for property p, where o' is the type literal corresponding to o.
     *
     * @param p the property
     * @param o the object
     * @return a stream of matching resources
     */
    Stream<RdfResource> resourcesWithProperty( Property p, Object o );

    /**
     * Similar to {@link org.apache.jena.rdf.model.ModelCon#listSubjectsWithProperty(Property, String)}, list subjects with a given
     * property and value
     *
     * @param p the property
     * @param o the object
     * @return a stream of matching resources
     */
    Stream<RdfResource> subjectsWithProperty( Property p, String o );

    /**
     * Similar to {@link org.apache.jena.rdf.model.ModelCon#listSubjectsWithProperty(Property, String, String)}, list subjects with a given
     * property and value
     *
     * @param p the property
     * @param o the object
     * @param l the language tag of the string
     * @return a stream of matching resources
     */
    Stream<RdfResource> resourcesWithProperty( Property p, String o, String l );
}
