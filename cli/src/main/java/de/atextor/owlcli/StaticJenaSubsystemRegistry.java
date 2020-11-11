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

import org.apache.jena.sys.InitJenaCore;
import org.apache.jena.sys.JenaSubsystemLifecycle;
import org.apache.jena.sys.JenaSubsystemRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements Jena's internal module registry, but unlike the default implementation
 * {@link org.apache.jena.sys.JenaSubsystemRegistryBasic}, it does not depend on the service loader
 * mechanism, which is disabled in OWL-CLI's GraalVM build.
 * The modules list therefore must contain all classes that are mentioned in the respective
 * META-INF/services/org.apache.jena.sys.JenaSubsystemLifcycle of the used Jena dependencies.
 */
public class StaticJenaSubsystemRegistry implements JenaSubsystemRegistry {
    private final List<JenaSubsystemLifecycle> modules = List.of( new InitJenaCore() );

    @Override
    public void load() {
        // Nothing to do
    }

    @Override
    public void add( final JenaSubsystemLifecycle module ) {
        // Nothing to do
    }

    @Override
    public boolean isRegistered( final JenaSubsystemLifecycle module ) {
        return modules.contains( module );
    }

    @Override
    public void remove( final JenaSubsystemLifecycle module ) {
        // Nothing to do
    }

    @Override
    public int size() {
        return modules.size();
    }

    @Override
    public boolean isEmpty() {
        return modules.isEmpty();
    }

    @Override
    public List<JenaSubsystemLifecycle> snapshot() {
        return new ArrayList<>( modules );
    }
}
