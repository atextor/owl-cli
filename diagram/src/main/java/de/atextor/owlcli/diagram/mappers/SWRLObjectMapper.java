/*
 * This file is part of OWL-CLI.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright (c) 2020, Andreas Textor.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www
 * .gnu.org/licenses/.
 */

package de.atextor.owlcli.diagram.mappers;

import de.atextor.owlcli.diagram.graph.Edge;
import de.atextor.owlcli.diagram.graph.Graph;
import de.atextor.owlcli.diagram.graph.GraphElement;
import de.atextor.owlcli.diagram.graph.Node;
import de.atextor.owlcli.diagram.graph.node.Literal;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.SWRLBuiltInAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLDataPropertyAtom;
import org.semanticweb.owlapi.model.SWRLDataRangeAtom;
import org.semanticweb.owlapi.model.SWRLDifferentIndividualsAtom;
import org.semanticweb.owlapi.model.SWRLIndividualArgument;
import org.semanticweb.owlapi.model.SWRLLiteralArgument;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLObjectVisitorEx;
import org.semanticweb.owlapi.model.SWRLSameIndividualAtom;
import org.semanticweb.owlapi.model.SWRLVariable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SWRLObjectMapper implements SWRLObjectVisitorEx<Graph> {
    private final MappingConfiguration mappingConfig;

    /*
     * During traversal of the rule expression tree, both Literal nodes and other GraphElements
     * (mainly Edges) are collected. The values of the Literal nodes are concatenated in the end
     * to render the final rule representation. In order to differentiate the to-be-concatenated
     * Literal nodes from "regular" Literal nodes that might occur, they are given an internal
     * identifier "marker" IRI.
     */
    private static final IRI LITERAL_ID = IRI.create( "urn:owl-cli:literal-id" );

    public static final Predicate<GraphElement> IS_RULE_SYNTAX_PART =
        graphElement -> graphElement.is( Literal.class ) &&
            graphElement.as( Literal.class ).getId().getIri().map( iri -> iri.equals( LITERAL_ID ) ).orElse( false );

    public SWRLObjectMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public Graph visit( final @Nonnull SWRLClassAtom atom ) {
        return null;
    }

    @Override
    public Graph visit( final @Nonnull SWRLDataRangeAtom atom ) {
        return null;
    }

    @Override
    public Graph visit( final @Nonnull SWRLObjectPropertyAtom atom ) {
        final List<GraphElement> argumentGraphElements = atom.allArguments().flatMap( argument ->
            argument.accept( this ).toStream() ).collect( Collectors.toList() );
        final String arguments = argumentGraphElements.stream()
            .flatMap( element -> element.view( Literal.class ) )
            .filter( IS_RULE_SYNTAX_PART )
            .map( Literal::getValue )
            .collect( Collectors.joining( ", " ) );

        final String label = String.format( "%s(%s)", atom.getPredicate().isNamed() ?
            mappingConfig.getNameMapper().getName( atom.getPredicate().getNamedProperty() ) : "<?>", arguments );

        final Node objectProperty =
            atom.getPredicate().accept( mappingConfig.getOwlPropertyExpressionMapper() ).getNode();
        final Literal literal = new Literal( mappingConfig.getIdentifierMapper()
            .getSyntheticIdForIri( LITERAL_ID ), label );
        final Edge edge = new Edge.Plain( Edge.Type.DASHED_ARROW, literal, objectProperty );

        return Graph.of( literal ).and( objectProperty ).and( edge )
            .and( argumentGraphElements.stream().filter( IS_RULE_SYNTAX_PART.negate() ) );
    }

    @Override
    public Graph visit( final @Nonnull SWRLDataPropertyAtom atom ) {
        return null;
    }

    @Override
    public Graph visit( final @Nonnull SWRLBuiltInAtom atom ) {
        return null;
    }

    @Override
    public Graph visit( final @Nonnull SWRLVariable variable ) {
        return Graph.of( new Literal( mappingConfig.getIdentifierMapper().getSyntheticIdForIri( LITERAL_ID ),
            "?" + variable.getIRI().getFragment() ) );
    }

    @Override
    public Graph visit( final @Nonnull SWRLIndividualArgument argument ) {
        return null;
    }

    @Override
    public Graph visit( final @Nonnull SWRLLiteralArgument argument ) {
        return argument.getLiteral().accept( mappingConfig.getOwlDataMapper() );
    }

    @Override
    public Graph visit( final @Nonnull SWRLSameIndividualAtom node ) {
        return null;
    }

    @Override
    public Graph visit( final @Nonnull SWRLDifferentIndividualsAtom node ) {
        return null;
    }
}
