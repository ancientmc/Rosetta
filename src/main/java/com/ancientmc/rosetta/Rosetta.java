package com.ancientmc.rosetta;

import com.ancientmc.rosetta.command.GenerateCommand;
import com.ancientmc.rosetta.command.UpdateCommand;
import picocli.CommandLine;

import static picocli.CommandLine.Command;

@Command(name = "rosetta", subcommands = { CommandLine.HelpCommand.class, GenerateCommand.class, UpdateCommand.class })
public class Rosetta {
    public static void main(String[] args) {
        Rosetta rosetta = new Rosetta();
        CommandLine cli = new CommandLine(rosetta);
        cli.execute(args);
    }
}
