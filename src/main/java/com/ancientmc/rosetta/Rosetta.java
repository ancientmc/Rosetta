package com.ancientmc.rosetta;

import com.ancientmc.rosetta.command.*;
import com.ancientmc.rosetta.util.RosettaException;

public class Rosetta {
    public static void main(String[] args) {
        String name = args[0];
        CommandRegistry registry = new CommandRegistry();

        if (!registry.isValidCommand(name)) {
            throw new RosettaException(name);
        }

        switch (name) {
            case "generate" -> exec(new GenerateCommand().setArgs(args));
            case "update" -> exec(new UpdateCommand().setArgs(args));
            case "help" -> exec(new HelpCommand().setArgs(args));
        }
    }

    public static void exec(Command command) {
        isArgCountValid(command);
        command.exec();
    }

    public static void isArgCountValid(Command command) {
        if (!command.argCountValid()) {
            throw new RosettaException(command);
        }
    }
}
