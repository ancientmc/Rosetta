package com.ancientmc.rosetta;

import com.ancientmc.rosetta.command.*;
import com.ancientmc.rosetta.util.RosettaException;

public class Rosetta {
    private static final CommandRegistry REGISTRY = new CommandRegistry();

    public static void main(String[] args) {
        String name = args[0];

        if (!REGISTRY.isValidCommand(name)) {
            throw new RosettaException(name);
        }

        if (name.equals("generate")) {
            Command command = new GenerateCommand().setArgs(args);
            isArgCountValid(command);
            command.exec();
        }

        if (name.equals("update")) {
            Command command = new UpdateCommand().setArgs(args);
            isArgCountValid(command);
            command.exec();
        }

        if (name.equals("help")) {
            Command command = new HelpCommand().setArgs(args);
            isArgCountValid(command);
            command.exec();
        }
    }

    public static void isArgCountValid(Command command) {
        if (!command.argCountValid()) {
            throw new RosettaException(command);
        }
    }
}
