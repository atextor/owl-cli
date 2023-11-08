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

import de.atextor.ret.diagram.owl.graph.Graph;
import de.atextor.ret.diagram.owl.printers.OWLClassExpressionPrinter;
import de.atextor.ret.diagram.owl.printers.OWLDataPrinter;
import de.atextor.ret.diagram.owl.printers.OWLIndividualPrinter;
import de.atextor.ret.diagram.owl.printers.OWLPropertyExpressionPrinter;
import org.semanticweb.owlapi.model.OWLAnnotationObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAnnotationSubjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataVisitorEx;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;
import org.semanticweb.owlapi.model.SWRLObjectVisitorEx;

import java.util.Optional;

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
    private SWRLObjectVisitorEx<Graph> swrlObjectMapper;
    private IdentifierMapper identifierMapper;
    private NameMapper nameMapper;
    private OWLDataVisitorEx<String> owlDataPrinter;
    private OWLPropertyExpressionVisitorEx<String> owlPropertyExpressionPrinter;
    private OWLIndividualVisitorEx<String> owlIndividualPrinter;
    private OWLClassExpressionVisitorEx<String> owlClassExpressionPrinter;

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
    public SWRLObjectVisitorEx<Graph> getSwrlObjectMapper() {
        return swrlObjectMapper;
    }

    @Override
    public IdentifierMapper getIdentifierMapper() {
        return identifierMapper;
    }

    @Override
    public NameMapper getNameMapper() {
        return nameMapper;
    }

    @Override
    public OWLDataVisitorEx<String> getOwlDataPrinter() {
        return owlDataPrinter;
    }

    @Override
    public OWLIndividualVisitorEx<String> getOwlIndividualPrinter() {
        return owlIndividualPrinter;
    }

    @Override
    public OWLPropertyExpressionVisitorEx<String> getOwlPropertyExpressionPrinter() {
        return owlPropertyExpressionPrinter;
    }

    @Override
    public OWLClassExpressionVisitorEx<String> getOwlClassExpressionPrinter() {
        return owlClassExpressionPrinter;
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

    public void setSwrlObjectMapper( final SWRLObjectVisitorEx<Graph> swrlObjectMapper ) {
        this.swrlObjectMapper = swrlObjectMapper;
    }

    private void setIdentifierMapper( final IdentifierMapper identifierMapper ) {
        this.identifierMapper = identifierMapper;
    }

    private void setNameMapper( final NameMapper nameMapper ) {
        this.nameMapper = nameMapper;
    }

    private void setOwlDataPrinter( final OWLDataVisitorEx<String> owlDataPrinter ) {
        this.owlDataPrinter = owlDataPrinter;
    }

    private void setOwlPropertyExpressionPrinter( final OWLPropertyExpressionVisitorEx<String> owlPropertyExpressionPrinter ) {
        this.owlPropertyExpressionPrinter = owlPropertyExpressionPrinter;
    }

    private void setOwlIndividualPrinter( final OWLIndividualVisitorEx<String> owlIndividualPrinter ) {
        this.owlIndividualPrinter = owlIndividualPrinter;
    }

    private void setOwlClassExpressionPrinter( final OWLClassExpressionVisitorEx<String> owlClassExpressionPrinter ) {
        this.owlClassExpressionPrinter = owlClassExpressionPrinter;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Optional<OWLAxiomVisitorEx<Graph>> owlAxiomMapper = Optional.empty();
        private Optional<OWLClassExpressionVisitorEx<Graph>> owlClassExpressionMapper = Optional.empty();
        private Optional<OWLIndividualVisitorEx<Graph>> owlIndividualMapper = Optional.empty();
        private Optional<OWLPropertyExpressionVisitorEx<Graph>> owlPropertyExpressionMapper = Optional.empty();
        private Optional<OWLObjectVisitorEx<Graph>> owlObjectMapper = Optional.empty();
        private Optional<OWLDataVisitorEx<Graph>> owlDataMapper = Optional.empty();
        private Optional<OWLEntityVisitorEx<Graph>> owlEntityMapper = Optional.empty();
        private Optional<OWLAnnotationObjectVisitorEx<Graph>> owlAnnotationObjectMapper = Optional.empty();
        private Optional<OWLAnnotationSubjectVisitorEx<Graph>> owlAnnotationSubjectMapper = Optional.empty();
        private Optional<SWRLObjectVisitorEx<Graph>> swrlObjectMapper = Optional.empty();
        private Optional<IdentifierMapper> identifierMapper = Optional.empty();
        private Optional<NameMapper> nameMapper = Optional.empty();
        private Optional<OWLDataVisitorEx<String>> owlDataPrinter = Optional.empty();
        private Optional<OWLPropertyExpressionVisitorEx<String>> owlPropertyExpressionPrinter = Optional.empty();
        private Optional<OWLIndividualVisitorEx<String>> owlIndividualPrinter = Optional.empty();
        private Optional<OWLClassExpressionVisitorEx<String>> owlClassExpressionPrinter = Optional.empty();

        public Builder owlAxiomMapper( final OWLAxiomVisitorEx<Graph> mapper ) {
            owlAxiomMapper = Optional.of( mapper );
            return this;
        }

        public Builder owlClassExpressionMapper( final OWLClassExpressionVisitorEx<Graph> mapper ) {
            owlClassExpressionMapper = Optional.of( mapper );
            return this;
        }

        public Builder owlIndividualMapper( final OWLIndividualVisitorEx<Graph> mapper ) {
            owlIndividualMapper = Optional.of( mapper );
            return this;
        }

        public Builder owlPropertyExpressionMapper( final OWLPropertyExpressionVisitorEx<Graph> mapper ) {
            owlPropertyExpressionMapper = Optional.of( mapper );
            return this;
        }

        public Builder owlObjectMapper( final OWLObjectVisitorEx<Graph> mapper ) {
            owlObjectMapper = Optional.of( mapper );
            return this;
        }

        public Builder owlDataMapper( final OWLDataVisitorEx<Graph> mapper ) {
            owlDataMapper = Optional.of( mapper );
            return this;
        }

        public Builder owlEntityMapper( final OWLEntityVisitorEx<Graph> mapper ) {
            owlEntityMapper = Optional.of( mapper );
            return this;
        }

        public Builder owlAnnotationObjectMapper( final OWLAnnotationObjectVisitorEx<Graph> mapper ) {
            owlAnnotationObjectMapper = Optional.of( mapper );
            return this;
        }

        public Builder owlAnnotationSubjectMapper( final OWLAnnotationSubjectVisitorEx<Graph> mapper ) {
            owlAnnotationSubjectMapper = Optional.of( mapper );
            return this;
        }

        public Builder swrlObjectMapper( final SWRLObjectVisitorEx<Graph> mapper ) {
            swrlObjectMapper = Optional.of( mapper );
            return this;
        }

        public Builder identifierMapper( final IdentifierMapper mapper ) {
            identifierMapper = Optional.of( mapper );
            return this;
        }

        public Builder nameMapper( final NameMapper mapper ) {
            nameMapper = Optional.of( mapper );
            return this;
        }

        public Builder owlDataPrinter( final OWLDataVisitorEx<String> printer ) {
            owlDataPrinter = Optional.of( printer );
            return this;
        }

        public Builder owlPropertyExpressionPrinter( final OWLPropertyExpressionVisitorEx<String> printer ) {
            owlPropertyExpressionPrinter = Optional.of( printer );
            return this;
        }

        public Builder owlIndividualPrinter( final OWLIndividualVisitorEx<String> printer ) {
            owlIndividualPrinter = Optional.of( printer );
            return this;
        }

        public Builder owlClassExpressionPrinter( final OWLClassExpressionVisitorEx<String> printer ) {
            owlClassExpressionPrinter = Optional.of( printer );
            return this;
        }

        public MappingConfiguration build() {
            final DefaultMappingConfiguration mappingConfig = new DefaultMappingConfiguration();

            mappingConfig.setOwlAxiomMapper( owlAxiomMapper.orElseGet( () -> new OWLAxiomMapper( mappingConfig ) ) );
            mappingConfig.setOwlClassExpressionMapper( owlClassExpressionMapper
                .orElseGet( () -> new OWLClassExpressionMapper( mappingConfig ) ) );
            mappingConfig.setOwlIndividualMapper( owlIndividualMapper
                .orElseGet( () -> new OWLIndividualMapper( mappingConfig ) ) );
            mappingConfig.setOwlPropertyExpressionMapper( owlPropertyExpressionMapper
                .orElseGet( () -> new OWLPropertyExpressionMapper( mappingConfig ) ) );
            mappingConfig.setOwlObjectMapper( owlObjectMapper.orElseGet( () -> new OWLObjectMapper( mappingConfig ) ) );
            mappingConfig.setOwlDataMapper( owlDataMapper.orElseGet( () -> new OWLDataMapper( mappingConfig ) ) );
            mappingConfig.setOwlEntityMapper( owlEntityMapper.orElseGet( () -> new OWLEntityMapper( mappingConfig ) ) );
            mappingConfig.setOwlAnnotationObjectMapper( owlAnnotationObjectMapper
                .orElseGet( () -> new OWLAnnotationObjectMapper( mappingConfig ) ) );
            mappingConfig.setOwlAnnotationSubjectMapper( owlAnnotationSubjectMapper
                .orElseGet( () -> new OWLAnnotationObjectMapper( mappingConfig ) ) );
            mappingConfig
                .setSwrlObjectMapper( swrlObjectMapper.orElseGet( () -> new SWRLObjectMapper( mappingConfig ) ) );
            mappingConfig.setIdentifierMapper( identifierMapper.orElseGet( DefaultIdentifierMapper::new ) );
            mappingConfig.setNameMapper( nameMapper.orElseGet( () -> new DefaultNameMapper( mappingConfig ) ) );
            mappingConfig.setOwlDataPrinter( owlDataPrinter.orElseGet( () -> new OWLDataPrinter( mappingConfig ) ) );
            mappingConfig.setOwlPropertyExpressionPrinter( owlPropertyExpressionPrinter
                .orElseGet( () -> new OWLPropertyExpressionPrinter( mappingConfig ) ) );
            mappingConfig.setOwlIndividualPrinter( owlIndividualPrinter.orElseGet(
                () -> new OWLIndividualPrinter( mappingConfig ) ) );
            mappingConfig.setOwlClassExpressionPrinter( owlClassExpressionPrinter.orElseGet(
                () -> new OWLClassExpressionPrinter( mappingConfig ) ) );

            return mappingConfig;
        }
    }
}
