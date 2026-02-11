package com.ancientmc.rosetta;

import picocli.CommandLine;

import static picocli.CommandLine.Command;

@Command(name = "rosetta", subcommands = { CommandLine.HelpCommand.class, GenerateCommand.class })
public class Rosetta {
    public static void main(String[] args) {
        Rosetta rosetta = new Rosetta();
        CommandLine cli = new CommandLine(rosetta);
        cli.execute(args);
    }
}
