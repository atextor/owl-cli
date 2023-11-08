/*
 * Copyright 2021 Andreas Textor
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

package de.atextor.ret.diagram.owl.mappers;

import de.atextor.ret.diagram.owl.graph.Edge;
import de.atextor.ret.diagram.owl.graph.Graph;
import de.atextor.ret.diagram.owl.graph.GraphElement;
import de.atextor.ret.diagram.owl.graph.Node;
import de.atextor.ret.diagram.owl.graph.node.Literal;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.SWRLArgument;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLBinaryAtom;
import org.semanticweb.owlapi.model.SWRLBuiltInAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLDataPropertyAtom;
import org.semanticweb.owlapi.model.SWRLDataRangeAtom;
import org.semanticweb.owlapi.model.SWRLDifferentIndividualsAtom;
import org.semanticweb.owlapi.model.SWRLIArgument;
import org.semanticweb.owlapi.model.SWRLIndividualArgument;
import org.semanticweb.owlapi.model.SWRLLiteralArgument;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLObjectVisitorEx;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLSameIndividualAtom;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.vocab.Namespaces;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SWRLObjectMapper implements SWRLObjectVisitorEx<Graph> {
    /**
     * During traversal of the rule expression tree, both Literal nodes and other GraphElements
     * (mainly Edges) are collected. The values of the Literal nodes are concatenated in the end
     * to render the final rule representation. In order to differentiate the to-be-concatenated
     * Literal nodes from "regular" Literal nodes that might occur, they are given an internal
     * identifier "marker" IRI. The concatenation is done in
     * {@link de.atextor.ret.diagram.owl.mappers.OWLAxiomMapper#visit(SWRLRule)}.
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
        return visitPropertyAtom( atom, atom.getPredicate() );
    }

    private <T extends SWRLArgument> Graph visitPropertyAtom( final SWRLBinaryAtom<SWRLIArgument, T> atom,
                                                              final OWLPropertyExpression predicate ) {
        final List<GraphElement> argumentGraphElements = argumentElements( atom );
        final String arguments = printArgumentElements( argumentGraphElements );

        final String label = String.format(
            "%s(%s)", predicate.accept( mappingConfig.getOwlPropertyExpressionPrinter() ), arguments );
        final Node dataProperty =
            predicate.accept( mappingConfig.getOwlPropertyExpressionMapper() ).getNode();
        final Literal literal = new Literal( mappingConfig.getIdentifierMapper()
            .getSyntheticIdForIri( LITERAL_ID ), label );
        final Edge edge = new Edge.Plain( Edge.Type.DASHED_ARROW, literal, dataProperty );

        return Graph.of( literal ).and( dataProperty ).and( edge )
            .and( argumentGraphElements.stream().filter( IS_RULE_SYNTAX_PART.negate() ) );
    }

    @Override
    public Graph visit( final @Nonnull SWRLDataPropertyAtom atom ) {
        return visitPropertyAtom( atom, atom.getPredicate() );
    }

    @Override
    public Graph visit( final @Nonnull SWRLBuiltInAtom atom ) {
        final List<GraphElement> argumentGraphElements = argumentElements( atom );
        final String arguments = printArgumentElements( argumentGraphElements );
        final String builtin = Namespaces.SWRLB.inNamespace( atom.getPredicate() ) ?
            String.format( "swrlb:%s", atom.getPredicate().getFragment() ) :
            mappingConfig.getNameMapper().getName( atom.getPredicate() );
        final String label = String.format( "%s(%s)", builtin, arguments );
        return Graph.of( new Literal( mappingConfig.getIdentifierMapper().getSyntheticIdForIri( LITERAL_ID ),
            label ) );
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
        final OWL2Datatype literalType = argument.getLiteral().getDatatype().getBuiltInDatatype();
        final boolean quote = switch ( literalType.getCategory() ) {
            case CAT_STRING_WITH_LANGUAGE_TAG, CAT_STRING_WITHOUT_LANGUAGE_TAG, CAT_URI -> true;
            default -> false;
        };
        final String labelContent = argument.getLiteral().accept( mappingConfig.getOwlDataPrinter() );
        final String label = quote ? "\"" + labelContent + "\"" : labelContent;
        final Literal literal = new Literal( mappingConfig.getIdentifierMapper()
            .getSyntheticIdForIri( LITERAL_ID ), label );
        return Graph.of( literal );
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
