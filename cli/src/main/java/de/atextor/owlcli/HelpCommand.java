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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.Set;
import java.util.stream.Stream;

/**
 * Command that gives help about other commands
 */
public class HelpCommand extends CommandBase<HelpCommand.Arguments> {
    /**
     * The commands that the help command knows about
     */
    private final Set<CommandBase<?>> commands;

    protected HelpCommand( final Set<CommandBase<?>> commands ) {
        super( new Arguments() );
        this.commands = commands;
    }

    @Override
    String getCommandName() {
        return "help";
    }

    @Override
    String getHelp() {
        return "The help command prints the detailed help for another command.";
    }

    @Override
    public void run() {
        Stream.concat( commands.stream(), Stream.of( this ) )
            .filter( command -> command.getCommandName().equals( arguments.command ) )
            .map( CommandBase::getHelp )
            .forEach( System.out::println );
    }

    @Parameters( commandDescription = "Print the help for a command" )
    static class Arguments {
        @Parameter( description = "command", required = true )
        String command;
    }
}
