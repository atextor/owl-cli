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
import org.semanticweb.owlapi.model.SWRLAtom;
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
    private final MappingConfiguration mappingConfig;

    public SWRLObjectMapper( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public Graph visit( final @Nonnull SWRLClassAtom atom ) {
        final List<GraphElement> argumentGraphElements = argumentElements( atom );
        final String arguments = printArgumentElements( argumentGraphElements );

        final String label = String.format( "%s(%s)",
            atom.getPredicate().accept( mappingConfig.getOwlClassExpressionPrinter() ), arguments );

        final Graph classGraph = atom.getPredicate().accept( mappingConfig.getOwlClassExpressionMapper() );
        final Literal literal = new Literal( mappingConfig.getIdentifierMapper()
            .getSyntheticIdForIri( LITERAL_ID ), label );
        final Edge edge = new Edge.Plain( Edge.Type.DASHED_ARROW, literal, classGraph.getNode() );

        return Graph.of( literal ).and( classGraph ).and( edge )
            .and( argumentGraphElements.stream().filter( IS_RULE_SYNTAX_PART.negate() ) );
    }

    @Override
    public Graph visit( final @Nonnull SWRLDataRangeAtom atom ) {
        final List<GraphElement> argumentGraphElements = argumentElements( atom );
        final String arguments = printArgumentElements( argumentGraphElements );
        final String label = String.format( "%s(%s)", atom.getPredicate().accept( mappingConfig.getOwlDataPrinter() ),
            arguments );
        final Literal literal = new Literal( mappingConfig.getIdentifierMapper()
            .getSyntheticIdForIri( LITERAL_ID ), label );
        return Graph.of( literal );
    }

    private List<GraphElement> argumentElements( final SWRLAtom atom ) {
        return atom.allArguments().flatMap( argument ->
            argument.accept( this ).toStream() ).collect( Collectors.toList() );
    }

    private String printArgumentElements( final List<GraphElement> argumentElements ) {
        return argumentElements.stream()
            .flatMap( element -> element.view( Literal.class ) )
            .filter( IS_RULE_SYNTAX_PART )
            .map( Literal::getValue )
            .collect( Collectors.joining( ", " ) );
    }

    @Override
    public Graph visit( final @Nonnull SWRLObjectPropertyAtom atom ) {
        final List<GraphElement> argumentGraphElements = argumentElements( atom );
        final String arguments = printArgumentElements( argumentGraphElements );

        final String label = String.format( "%s(%s)", atom.getPredicate()
            .accept( mappingConfig.getOwlPropertyExpressionPrinter() ), arguments );

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
        final List<GraphElement> argumentGraphElements = argumentElements( atom );
        final String arguments = printArgumentElements( argumentGraphElements );

        final String label = String.format( "%s(%s)", atom.getPredicate()
            .accept( mappingConfig.getOwlPropertyExpressionPrinter() ), arguments );

        final Node dataProperty =
            atom.getPredicate().accept( mappingConfig.getOwlPropertyExpressionMapper() ).getNode();
        final Literal literal = new Literal( mappingConfig.getIdentifierMapper()
            .getSyntheticIdForIri( LITERAL_ID ), label );
        final Edge edge = new Edge.Plain( Edge.Type.DASHED_ARROW, literal, dataProperty );

        return Graph.of( literal ).and( dataProperty ).and( edge )
            .and( argumentGraphElements.stream().filter( IS_RULE_SYNTAX_PART.negate() ) );
    }

    @Override
    public Graph visit( final @Nonnull SWRLBuiltInAtom atom ) {
        return null;
    }

    @Override
    public Graph visit( final @Nonnull SWRLVariable variable ) {
        // Do not call namemapper for variable IRI fragment: variable name is not subject to be
        // rendered with prefix
        return Graph.of( new Literal( mappingConfig.getIdentifierMapper().getSyntheticIdForIri( LITERAL_ID ),
            "?" + variable.getIRI().getFragment() ) );
    }

    @Override
    public Graph visit( final @Nonnull SWRLIndividualArgument argument ) {
        final Node individual = argument.getIndividual().accept( mappingConfig.getOwlIndividualMapper() ).getNode();
        final String label = argument.getIndividual().accept( mappingConfig.getOwlIndividualPrinter() );
        final Literal literal = new Literal( mappingConfig.getIdentifierMapper()
            .getSyntheticIdForIri( LITERAL_ID ), label );
        final Edge edge = new Edge.Plain( Edge.Type.DASHED_ARROW, literal, individual );
        return Graph.of( literal ).and( individual ).and( edge );
    }

    @Override
    public Graph visit( final @Nonnull SWRLLiteralArgument argument ) {
        return argument.getLiteral().accept( mappingConfig.getOwlDataMapper() );
    }

    @Override
    public Graph visit( final @Nonnull SWRLSameIndividualAtom atom ) {
        final List<GraphElement> argumentGraphElements = argumentElements( atom );
        final String arguments = printArgumentElements( argumentGraphElements );
        final String label = String.format( "sameAs(%s)", arguments );
        final Literal literal = new Literal( mappingConfig.getIdentifierMapper()
            .getSyntheticIdForIri( LITERAL_ID ), label );
        return atom.individualsInSignature().map( owlIndividual -> {
            final Node individual = owlIndividual.accept( mappingConfig.getOwlIndividualMapper() ).getNode();
            final Edge edge = new Edge.Plain( Edge.Type.DASHED_ARROW, literal, individual );
            return Graph.of( individual ).and( edge );
        } ).reduce( Graph.of( literal ), Graph::and );
    }

    @Override
    public Graph visit( final @Nonnull SWRLDifferentIndividualsAtom node ) {
        return null;
    }
}
