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

package cool.rdf.diagram.owl.printers;

import cool.rdf.diagram.owl.mappers.MappingConfiguration;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDataVisitorEx;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLLiteral;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

/**
 * Serializes {@link org.semanticweb.owlapi.model.OWLDataRange}s into expressions.
 */
public class OWLDataPrinter implements OWLDataVisitorEx<String> {
    private final MappingConfiguration mappingConfig;

    /**
     * Creates a new data printer from a given mapping config
     *
     * @param mappingConfig the config
     */
    public OWLDataPrinter( final MappingConfiguration mappingConfig ) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public String visit( final @Nonnull OWLDataComplementOf node ) {
        return String.format( "not(%s)", node.getDataRange().accept( this ) );
    }

    @Override
    public String visit( final @Nonnull OWLDataOneOf node ) {
        return node.values().map( literal -> literal.accept( this ) )
            .collect( Collectors.joining( ", ", "{", "}" ) );
    }

    @Override
    public String visit( final @Nonnull OWLDataIntersectionOf node ) {
        return node.operands().map( operand -> operand.accept( this ) )
            .collect( Collectors.joining( ", ", "and(", ")" ) );
    }

    @Override
    public String visit( final @Nonnull OWLDataUnionOf node ) {
        return node.operands().map( operand -> operand.accept( this ) )
            .collect( Collectors.joining( ", ", "or(", ")" ) );
    }

    @Override
    public String visit( final @Nonnull OWLDatatypeRestriction node ) {
        return node.getDatatype().accept( this ) + " " + node.facetRestrictions()
            .map( owlFacetRestriction -> owlFacetRestriction.accept( this ) )
            .collect( Collectors.joining( ", ", "[", "]" ) );
    }

    @Override
    public String visit( final @Nonnull OWLFacetRestriction node ) {
        return node.getFacet().getSymbolicForm() + " " + node.getFacetValue().getLiteral();
    }

    @Override
    public String visit( final @Nonnull OWLDatatype node ) {
        return mappingConfig.getNameMapper().getName( node );
    }

    @Override
    public String visit( final @Nonnull OWLLiteral node ) {
        if ( node.isBoolean() || node.isDouble() || node.isFloat() || node.isInteger() ) {
            return node.getLiteral();
        }
        return "\"" + node.getLiteral() + "\"";
    }
}
