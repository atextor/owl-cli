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

import de.atextor.owlcli.diagram.graph.Graph;
import org.semanticweb.owlapi.model.OWLAnnotationObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAnnotationSubjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataVisitorEx;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;

import java.util.function.Supplier;

public class DefaultMappingConfiguration implements MappingConfiguration {
    private OWLAxiomVisitorEx<Graph> owlAxiomMapper;
    private OWLClassExpressionVisitorEx<Graph> owlClassExpressionMapper;
    private OWLIndividualVisitorEx<Graph> owlIndividualMapper;
    private OWLPropertyExpressionVisitorEx<Graph> owlPropertyExpressionMapper;
    private OWLObjectVisitorEx<Graph> owlObjectMapper;
    private OWLDataVisitorEx<Graph> owlDataMapper;
    private OWLEntityVisitorEx<Graph> owlEntityMapper;
    private OWLAnnotationObjectVisitorEx<Graph> owlAnnotationObjectMapper;
    private OWLAnnotationSubjectVisitorEx<Graph> owlAnnotationSubjectMapper;
    private IdentifierMapper identifierMapper;
    private NameMapper nameMapper;

    private DefaultMappingConfiguration() {
    }

    @Override
    public OWLAxiomVisitorEx<Graph> getOwlAxiomMapper() {
        return owlAxiomMapper;
    }

    @Override
    public OWLClassExpressionVisitorEx<Graph> getOwlClassExpressionMapper() {
        return owlClassExpressionMapper;
    }

    @Override
    public OWLIndividualVisitorEx<Graph> getOwlIndividualMapper() {
        return owlIndividualMapper;
    }

    @Override
    public OWLPropertyExpressionVisitorEx<Graph> getOwlPropertyExpressionMapper() {
        return owlPropertyExpressionMapper;
    }

    @Override
    public OWLObjectVisitorEx<Graph> getOwlObjectMapper() {
        return owlObjectMapper;
    }

    @Override
    public OWLDataVisitorEx<Graph> getOwlDataMapper() {
        return owlDataMapper;
    }

    @Override
    public OWLEntityVisitorEx<Graph> getOwlEntityMapper() {
        return owlEntityMapper;
    }

    @Override
    public OWLAnnotationObjectVisitorEx<Graph> getOwlAnnotationObjectMapper() {
        return owlAnnotationObjectMapper;
    }

    @Override
    public OWLAnnotationSubjectVisitorEx<Graph> getOwlAnnotationSubjectMapper() {
        return owlAnnotationSubjectMapper;
    }

    @Override
    public IdentifierMapper getIdentifierMapper() {
        return identifierMapper;
    }

    @Override
    public NameMapper getNameMapper() {
        return nameMapper;
    }

    private void setOwlAxiomMapper( final OWLAxiomVisitorEx<Graph> owlAxiomMapper ) {
        this.owlAxiomMapper = owlAxiomMapper;
    }

    private void setOwlClassExpressionMapper( final OWLClassExpressionVisitorEx<Graph> owlClassExpressionMapper ) {
        this.owlClassExpressionMapper = owlClassExpressionMapper;
    }

    private void setOwlIndividualMapper( final OWLIndividualVisitorEx<Graph> owlIndividualMapper ) {
        this.owlIndividualMapper = owlIndividualMapper;
    }

    private void setOwlPropertyExpressionMapper( final OWLPropertyExpressionVisitorEx<Graph> owlPropertyExpressionMapper ) {
        this.owlPropertyExpressionMapper = owlPropertyExpressionMapper;
    }

    private void setOwlObjectMapper( final OWLObjectVisitorEx<Graph> owlObjectMapper ) {
        this.owlObjectMapper = owlObjectMapper;
    }

    private void setOwlDataMapper( final OWLDataVisitorEx<Graph> owlDataMapper ) {
        this.owlDataMapper = owlDataMapper;
    }

    private void setOwlEntityMapper( final OWLEntityVisitorEx<Graph> owlEntityMapper ) {
        this.owlEntityMapper = owlEntityMapper;
    }

    private void setOwlAnnotationObjectMapper( final OWLAnnotationObjectVisitorEx<Graph> owlAnnotationObjectMapper ) {
        this.owlAnnotationObjectMapper = owlAnnotationObjectMapper;
    }

    private void setOwlAnnotationSubjectMapper( final OWLAnnotationSubjectVisitorEx<Graph> owlAnnotationSubjectMapper ) {
        this.owlAnnotationSubjectMapper = owlAnnotationSubjectMapper;
    }

    private void setIdentifierMapper( final IdentifierMapper identifierMapper ) {
        this.identifierMapper = identifierMapper;
    }

    private void setNameMapper( final NameMapper nameMapper ) {
        this.nameMapper = nameMapper;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Supplier<OWLAxiomVisitorEx<Graph>> owlAxiomMapperSupplier = null;
        private Supplier<OWLClassExpressionVisitorEx<Graph>> owlClassExpressionMapperSupplier = null;
        private Supplier<OWLIndividualVisitorEx<Graph>> owlIndividualMapperSupplier = null;
        private Supplier<OWLPropertyExpressionVisitorEx<Graph>> owlPropertyExpressionMapperSupplier = null;
        private Supplier<OWLObjectVisitorEx<Graph>> owlObjectMapperSupplier = null;
        private Supplier<OWLDataVisitorEx<Graph>> owlDataMapperSupplier = null;
        private Supplier<OWLEntityVisitorEx<Graph>> owlEntityMapperSupplier = null;
        private Supplier<OWLAnnotationObjectVisitorEx<Graph>> owlAnnotationObjectMapperSupplier = null;
        private Supplier<OWLAnnotationSubjectVisitorEx<Graph>> owlAnnotationSubjectMapperSupplier = null;
        private Supplier<IdentifierMapper> identifierMapperSupplier = null;
        private Supplier<NameMapper> nameMapperSupplier = null;

        public Builder owlAxiomMapper( final Supplier<OWLAxiomVisitorEx<Graph>> supplier ) {
            owlAxiomMapperSupplier = supplier;
            return this;
        }

        public Builder owlClassExpressionMapper( final Supplier<OWLClassExpressionVisitorEx<Graph>> supplier ) {
            owlClassExpressionMapperSupplier = supplier;
            return this;
        }

        public Builder owlIndividualMapper( final Supplier<OWLIndividualVisitorEx<Graph>> supplier ) {
            owlIndividualMapperSupplier = supplier;
            return this;
        }

        public Builder owlPropertyExpressionMapper( final Supplier<OWLPropertyExpressionVisitorEx<Graph>> supplier ) {
            owlPropertyExpressionMapperSupplier = supplier;
            return this;
        }

        public Builder owlObjectMapper( final Supplier<OWLObjectVisitorEx<Graph>> supplier ) {
            owlObjectMapperSupplier = supplier;
            return this;
        }

        public Builder owlDataMapper( final Supplier<OWLDataVisitorEx<Graph>> supplier ) {
            owlDataMapperSupplier = supplier;
            return this;
        }

        public Builder owlEntityMapper( final Supplier<OWLEntityVisitorEx<Graph>> supplier ) {
            owlEntityMapperSupplier = supplier;
            return this;
        }

        public Builder owlAnnotationObjectMapper( final Supplier<OWLAnnotationObjectVisitorEx<Graph>> supplier ) {
            owlAnnotationObjectMapperSupplier = supplier;
            return this;
        }

        public Builder owlAnnotationSubjectMapper( final Supplier<OWLAnnotationSubjectVisitorEx<Graph>> supplier ) {
            owlAnnotationSubjectMapperSupplier = supplier;
            return this;
        }

        public Builder identifierMapper( final Supplier<IdentifierMapper> supplier ) {
            identifierMapperSupplier = supplier;
            return this;
        }

        public Builder nameMapper( final Supplier<NameMapper> supplier ) {
            nameMapperSupplier = supplier;
            return this;
        }

        public MappingConfiguration build() {
            final DefaultMappingConfiguration mappingConfig = new DefaultMappingConfiguration();

            if ( owlAxiomMapperSupplier == null ) {
                owlAxiomMapper( () -> new OWLAxiomMapper( mappingConfig ) );
            }
            if ( owlClassExpressionMapperSupplier == null ) {
                owlClassExpressionMapper( () -> new OWLClassExpressionMapper( mappingConfig ) );
            }
            if ( owlIndividualMapperSupplier == null ) {
                owlIndividualMapper( () -> new OWLIndividualMapper( mappingConfig ) );
            }
            if ( owlPropertyExpressionMapperSupplier == null ) {
                owlPropertyExpressionMapper( () -> new OWLPropertyExpressionMapper( mappingConfig ) );
            }
            if ( owlObjectMapperSupplier == null ) {
                owlObjectMapper( () -> new OWLObjectMapper( mappingConfig ) );
            }
            if ( owlDataMapperSupplier == null ) {
                owlDataMapper( () -> new OWLDataMapper( mappingConfig ) );
            }
            if ( owlEntityMapperSupplier == null ) {
                owlEntityMapper( () -> new OWLEntityMapper( mappingConfig ) );
            }
            if ( owlAnnotationObjectMapperSupplier == null ) {
                owlAnnotationObjectMapper( () -> new OWLAnnotationObjectMapper( mappingConfig ) );
            }
            if ( owlAnnotationSubjectMapperSupplier == null ) {
                owlAnnotationSubjectMapper( () -> new OWLAnnotationObjectMapper( mappingConfig ) );
            }
            if ( identifierMapperSupplier == null ) {
                identifierMapper( () -> new DefaultIdentifierMapper( mappingConfig ) );
            }
            if ( nameMapperSupplier == null ) {
                nameMapper( () -> new DefaultNameMapper( mappingConfig ) );
            }

            mappingConfig.setOwlAxiomMapper( owlAxiomMapperSupplier.get() );
            mappingConfig.setOwlClassExpressionMapper( owlClassExpressionMapperSupplier.get() );
            mappingConfig.setOwlIndividualMapper( owlIndividualMapperSupplier.get() );
            mappingConfig.setOwlPropertyExpressionMapper( owlPropertyExpressionMapperSupplier.get() );
            mappingConfig.setOwlObjectMapper( owlObjectMapperSupplier.get() );
            mappingConfig.setOwlDataMapper( owlDataMapperSupplier.get() );
            mappingConfig.setOwlEntityMapper( owlEntityMapperSupplier.get() );
            mappingConfig.setOwlAnnotationObjectMapper( owlAnnotationObjectMapperSupplier.get() );
            mappingConfig.setOwlAnnotationSubjectMapper( owlAnnotationSubjectMapperSupplier.get() );
            mappingConfig.setIdentifierMapper( identifierMapperSupplier.get() );
            mappingConfig.setNameMapper( nameMapperSupplier.get() );

            return mappingConfig;
        }
    }
}
