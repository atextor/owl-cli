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

package de.atextor.owlcli;

import com.google.common.collect.ImmutableSet;
import de.atextor.owlcli.diagram.diagram.Configuration;
import io.vavr.control.Try;
import org.semanticweb.owlapi.io.OWLParserFactory;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFactory;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLStorerFactory;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLOntologyFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLOntologyManagerImpl;
import uk.ac.manchester.cs.owl.owlapi.concurrent.NonConcurrentOWLOntologyBuilder;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AbstractCommand {

    protected void commandFailed() {
        System.exit( 1 );
    }

    protected Try<OutputStream> openOutput( final @Nonnull String input, final String output,
                                            final Configuration.Format targetFormat ) {
        if ( output != null ) {
            // Output is given as - --> write to stdout
            if ( output.equals( "-" ) ) {
                return Try.success( System.out );
            }

            // Output is given as something else --> open as file
            try {
                return Try.success( new FileOutputStream( output ) );
            } catch ( final FileNotFoundException exception ) {
                return Try.failure( exception );
            }
        }

        if ( input.equals( "-" ) ) {
            // Input is stdin, outout is not given -> write to stdout
            return Try.success( System.out );
        }

        // Input is something else, output is not given -> interpret input as filename,
        // change input's file extension to target format and use as output file name
        final String outputFilename = input.replaceFirst( "[.][^.]+$",
            "." + targetFormat.toString().toLowerCase() );
        if ( outputFilename.equals( input ) ) {
            return Try.failure( new ErrorMessage( "Can't determine an ouput filename" ) );
        }

        try {
            return Try.success( new FileOutputStream( outputFilename ) );
        } catch ( final FileNotFoundException exception ) {
            return Try.failure( exception );
        }
    }

    protected Try<InputStream> openInput( final String input ) {
        if ( input.equals( "-" ) ) {
            return Try.success( System.in );
        }
        try {
            final File inputFile = new File( input );
            if ( !inputFile.exists() ) {
                return Try.failure( new FileNotFoundException( input ) );
            }
            return Try.success( new FileInputStream( inputFile ) );
        } catch ( final FileNotFoundException exception ) {
            return Try.failure( exception );
        }
    }

    protected OWLOntologyManager createOWLOntologyManager() {
        final ImmutableSet<OWLParserFactory> parserFactories = ImmutableSet.<OWLParserFactory>builder()
            .add( new org.semanticweb.owlapi.rio.RioBinaryRdfParserFactory() )
            .add( new org.semanticweb.owlapi.rio.RioJsonLDParserFactory() )
            .add( new org.semanticweb.owlapi.rio.RioJsonParserFactory() )
            .add( new org.semanticweb.owlapi.rio.RioN3ParserFactory() )
            .add( new org.semanticweb.owlapi.rio.RioNQuadsParserFactory() )
            .add( new org.semanticweb.owlapi.rio.RioNTriplesParserFactory() )
            .add( new org.semanticweb.owlapi.rio.RioRDFaParserFactory() )
            .add( new org.semanticweb.owlapi.rio.RioRDFXMLParserFactory() )
            .add( new org.semanticweb.owlapi.rio.RioTrigParserFactory() )
            .add( new org.semanticweb.owlapi.rio.RioTrixParserFactory() )
            .add( new org.semanticweb.owlapi.rio.RioTurtleParserFactory() )
            .add( new org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntaxOntologyParserFactory() )
            .add( new org.semanticweb.owlapi.krss2.parser.KRSS2OWLParserFactory() )
            .add( new org.semanticweb.owlapi.rdf.turtle.parser.TurtleOntologyParserFactory() )
            .add( new org.semanticweb.owlapi.functional.parser.OWLFunctionalSyntaxOWLParserFactory() )
            .add( new org.semanticweb.owlapi.owlxml.parser.OWLXMLParserFactory() )
            .add( new org.semanticweb.owlapi.rdf.rdfxml.parser.RDFXMLParserFactory() )
            .add( new org.semanticweb.owlapi.dlsyntax.parser.DLSyntaxOWLParserFactory() )
            .add( new org.semanticweb.owlapi.oboformat.OBOFormatOWLAPIParserFactory() )
            .add( new org.semanticweb.owlapi.rio.RioBinaryRdfParserFactory() )
            .add( new org.semanticweb.owlapi.rio.RioJsonLDParserFactory() )
            .add( new org.semanticweb.owlapi.rio.RioJsonParserFactory() )
            .add( new org.semanticweb.owlapi.rio.RioN3ParserFactory() )
            .add( new org.semanticweb.owlapi.rio.RioNQuadsParserFactory() )
            .add( new org.semanticweb.owlapi.rio.RioNTriplesParserFactory() )
            .add( new org.semanticweb.owlapi.rio.RioRDFaParserFactory() )
            .add( new org.semanticweb.owlapi.rio.RioRDFXMLParserFactory() )
            .add( new org.semanticweb.owlapi.rio.RioTrigParserFactory() )
            .add( new org.semanticweb.owlapi.rio.RioTrixParserFactory() )
            .add( new org.semanticweb.owlapi.rio.RioTurtleParserFactory() )
            .build();

        final Set<OWLOntologyFactory> ontologyFactories = ImmutableSet.<OWLOntologyFactory>builder()
            .add( new OWLOntologyFactoryImpl( new NonConcurrentOWLOntologyBuilder() ) )
            .build();

        final Set<OWLStorerFactory> storerFactories = ImmutableSet.<OWLStorerFactory>builder()
            .add( new org.semanticweb.owlapi.rio.RioBinaryRdfStorerFactory() )
            .add( new org.semanticweb.owlapi.rio.RioJsonLDStorerFactory() )
            .add( new org.semanticweb.owlapi.rio.RioJsonStorerFactory() )
            .add( new org.semanticweb.owlapi.rio.RioN3StorerFactory() )
            .add( new org.semanticweb.owlapi.rio.RioNQuadsStorerFactory() )
            .add( new org.semanticweb.owlapi.rio.RioNTriplesStorerFactory() )
            .add( new org.semanticweb.owlapi.rio.RioRDFXMLStorerFactory() )
            .add( new org.semanticweb.owlapi.rio.RioTrigStorerFactory() )
            .add( new org.semanticweb.owlapi.rio.RioTrixStorerFactory() )
            .add( new org.semanticweb.owlapi.rio.RioTurtleStorerFactory() )
            .add( new org.semanticweb.owlapi.rdf.rdfxml.renderer.RDFXMLStorerFactory() )
            .add( new org.semanticweb.owlapi.owlxml.renderer.OWLXMLStorerFactory() )
            .add( new org.semanticweb.owlapi.functional.renderer.FunctionalSyntaxStorerFactory() )
            .add( new org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterSyntaxStorerFactory() )
            .add( new org.semanticweb.owlapi.krss2.renderer.KRSS2OWLSyntaxStorerFactory() )
            .add( new org.semanticweb.owlapi.rdf.turtle.renderer.TurtleStorerFactory() )
            .add( new org.semanticweb.owlapi.latex.renderer.LatexStorerFactory() )
            .add( new org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxHTMLStorerFactory() )
            .add( new org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxStorerFactory() )
            .add( new org.semanticweb.owlapi.oboformat.OBOFormatStorerFactory() )
            .add( new org.semanticweb.owlapi.rio.RioBinaryRdfStorerFactory() )
            .add( new org.semanticweb.owlapi.rio.RioJsonLDStorerFactory() )
            .add( new org.semanticweb.owlapi.rio.RioJsonStorerFactory() )
            .add( new org.semanticweb.owlapi.rio.RioN3StorerFactory() )
            .add( new org.semanticweb.owlapi.rio.RioNQuadsStorerFactory() )
            .add( new org.semanticweb.owlapi.rio.RioNTriplesStorerFactory() )
            .add( new org.semanticweb.owlapi.rio.RioRDFXMLStorerFactory() )
            .add( new org.semanticweb.owlapi.rio.RioTrigStorerFactory() )
            .add( new org.semanticweb.owlapi.rio.RioTrixStorerFactory() )
            .add( new org.semanticweb.owlapi.rio.RioTurtleStorerFactory() )
            .build();

        final OWLDataFactory dataFactory = new OWLDataFactoryImpl();
        final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        final OWLOntologyManager manager = new OWLOntologyManagerImpl( dataFactory, readWriteLock );

        manager.setOntologyFactories( ontologyFactories );
        manager.setOntologyParsers( parserFactories );
        manager.setOntologyStorers( storerFactories );

        return manager;
    }

    /**
     * Loads an ontology from an input stream
     *
     * @param inputStream the input stream
     * @return {@link Try.Success} with the {@link OWLOntology} on success, and a {@link Try.Failure} with the
     * {@link OWLOntologyCreationException} otherwise.
     */
    public Try<OWLOntology> loadOntology( final InputStream inputStream ) {
        final OWLOntologyManager manager = createOWLOntologyManager();
        final OWLOntology ontology;
        try {
            ontology = manager.loadOntologyFromOntologyDocument( inputStream );
            return Try.success( ontology );
        } catch ( final OWLOntologyCreationException exception ) {
            return Try.failure( exception );
        }
    }
}
