package de.atextor.owldiagram.mappers;

import de.atextor.owldiagram.graph.GraphElement;
import org.semanticweb.owlapi.model.OWLAnnotationObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataVisitorEx;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class DefaultMappingConfiguration implements MappingConfiguration {
    private OWLAxiomVisitorEx<Stream<GraphElement>> owlAxiomMapper;
    private OWLClassExpressionVisitorEx<Result> owlClassExpressionMapper;
    private OWLIndividualVisitorEx<Result> owlIndividualMapper;
    private OWLPropertyExpressionVisitorEx<Result> owlPropertyExpressionMapper;
    private OWLObjectVisitorEx<Result> owlObjectMapper;
    private OWLDataVisitorEx<Result> owlDataMapper;
    private OWLEntityVisitorEx<Result> owlEntityMapper;
    private OWLAnnotationObjectVisitorEx<Result> owlAnnotationObjectMapper;
    private IdentifierMapper identifierMapper;
    private NameMapper nameMapper;

    private DefaultMappingConfiguration() {
    }

    @Override
    public OWLAxiomVisitorEx<Stream<GraphElement>> getOwlAxiomMapper() {
        return owlAxiomMapper;
    }

    @Override
    public OWLClassExpressionVisitorEx<Result> getOwlClassExpressionMapper() {
        return owlClassExpressionMapper;
    }

    @Override
    public OWLIndividualVisitorEx<Result> getOwlIndividualMapper() {
        return owlIndividualMapper;
    }

    @Override
    public OWLPropertyExpressionVisitorEx<Result> getOwlPropertyExpressionMapper() {
        return owlPropertyExpressionMapper;
    }

    @Override
    public OWLObjectVisitorEx<Result> getOwlObjectMapper() {
        return owlObjectMapper;
    }

    @Override
    public OWLDataVisitorEx<Result> getOwlDataMapper() {
        return owlDataMapper;
    }

    @Override
    public OWLEntityVisitorEx<Result> getOwlEntityMapper() {
        return owlEntityMapper;
    }

    @Override
    public OWLAnnotationObjectVisitorEx<Result> getOwlAnnotationObjectMapper() {
        return owlAnnotationObjectMapper;
    }

    @Override
    public IdentifierMapper getIdentifierMapper() {
        return identifierMapper;
    }

    @Override
    public NameMapper getNameMapper() {
        return nameMapper;
    }

    private void setOwlAxiomMapper( final OWLAxiomVisitorEx<Stream<GraphElement>> owlAxiomMapper ) {
        this.owlAxiomMapper = owlAxiomMapper;
    }

    private void setOwlClassExpressionMapper( final OWLClassExpressionVisitorEx<Result> owlClassExpressionMapper ) {
        this.owlClassExpressionMapper = owlClassExpressionMapper;
    }

    private void setOwlIndividualMapper( final OWLIndividualVisitorEx<Result> owlIndividualMapper ) {
        this.owlIndividualMapper = owlIndividualMapper;
    }

    private void setOwlPropertyExpressionMapper( final OWLPropertyExpressionVisitorEx<Result> owlPropertyExpressionMapper ) {
        this.owlPropertyExpressionMapper = owlPropertyExpressionMapper;
    }

    private void setOwlObjectMapper( final OWLObjectVisitorEx<Result> owlObjectMapper ) {
        this.owlObjectMapper = owlObjectMapper;
    }

    private void setOwlDataMapper( final OWLDataVisitorEx<Result> owlDataMapper ) {
        this.owlDataMapper = owlDataMapper;
    }

    private void setOwlEntityMapper( final OWLEntityVisitorEx<Result> owlEntityMapper ) {
        this.owlEntityMapper = owlEntityMapper;
    }

    private void setOwlAnnotationObjectMapper( final OWLAnnotationObjectVisitorEx<Result> owlAnnotationObjectMapper ) {
        this.owlAnnotationObjectMapper = owlAnnotationObjectMapper;
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
        private Supplier<OWLAxiomVisitorEx<Stream<GraphElement>>> owlAxiomMapperSupplier = null;
        private Supplier<OWLClassExpressionVisitorEx<Result>> owlClassExpressionMapperSupplier = null;
        private Supplier<OWLIndividualVisitorEx<Result>> owlIndividualMapperSupplier = null;
        private Supplier<OWLPropertyExpressionVisitorEx<Result>> owlPropertyExpressionMapperSupplier = null;
        private Supplier<OWLObjectVisitorEx<Result>> owlObjectMapperSupplier = null;
        private Supplier<OWLDataVisitorEx<Result>> owlDataMapperSupplier = null;
        private Supplier<OWLEntityVisitorEx<Result>> owlEntityMapperSupplier = null;
        private Supplier<OWLAnnotationObjectVisitorEx<Result>> owlAnnotationObjectMapperSupplier = null;
        private Supplier<IdentifierMapper> identifierMapperSupplier = null;
        private Supplier<NameMapper> nameMapperSupplier = null;

        public Builder owlAxiomMapper( final Supplier<OWLAxiomVisitorEx<Stream<GraphElement>>> supplier ) {
            owlAxiomMapperSupplier = supplier;
            return this;
        }

        public Builder owlClassExpressionMapper( final Supplier<OWLClassExpressionVisitorEx<Result>> supplier ) {
            owlClassExpressionMapperSupplier = supplier;
            return this;
        }

        public Builder owlIndividualMapper( final Supplier<OWLIndividualVisitorEx<Result>> supplier ) {
            owlIndividualMapperSupplier = supplier;
            return this;
        }

        public Builder owlPropertyExpressionMapper( final Supplier<OWLPropertyExpressionVisitorEx<Result>> supplier ) {
            owlPropertyExpressionMapperSupplier = supplier;
            return this;
        }

        public Builder owlObjectMapper( final Supplier<OWLObjectVisitorEx<Result>> supplier ) {
            owlObjectMapperSupplier = supplier;
            return this;
        }

        public Builder owlDataMapper( final Supplier<OWLDataVisitorEx<Result>> supplier ) {
            owlDataMapperSupplier = supplier;
            return this;
        }

        public Builder owlEntityMapper( final Supplier<OWLEntityVisitorEx<Result>> supplier ) {
            owlEntityMapperSupplier = supplier;
            return this;
        }

        public Builder owlAnnotationObjectMapper( final Supplier<OWLAnnotationObjectVisitorEx<Result>> supplier ) {
            owlAnnotationObjectMapperSupplier = supplier;
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
            if ( identifierMapperSupplier == null ) {
                identifierMapper( () -> new IdentifierMapperImpl( mappingConfig ) );
            }
            if ( nameMapperSupplier == null ) {
                nameMapper( () -> new NameMapperImpl( mappingConfig ) );
            }

            mappingConfig.setOwlAxiomMapper( owlAxiomMapperSupplier.get() );
            mappingConfig.setOwlClassExpressionMapper( owlClassExpressionMapperSupplier.get() );
            mappingConfig.setOwlIndividualMapper( owlIndividualMapperSupplier.get() );
            mappingConfig.setOwlPropertyExpressionMapper( owlPropertyExpressionMapperSupplier.get() );
            mappingConfig.setOwlObjectMapper( owlObjectMapperSupplier.get() );
            mappingConfig.setOwlDataMapper( owlDataMapperSupplier.get() );
            mappingConfig.setOwlEntityMapper( owlEntityMapperSupplier.get() );
            mappingConfig.setOwlAnnotationObjectMapper( owlAnnotationObjectMapperSupplier.get() );
            mappingConfig.setIdentifierMapper( identifierMapperSupplier.get() );
            mappingConfig.setNameMapper( nameMapperSupplier.get() );

            return mappingConfig;
        }
    }
}
