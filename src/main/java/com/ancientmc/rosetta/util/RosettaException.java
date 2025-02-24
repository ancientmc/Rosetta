package com.ancientmc.rosetta.util;

import com.ancientmc.rosetta.command.Command;
import com.ancientmc.rosetta.command.CommandRegistry;

public class RosettaException extends RuntimeException {
    public RosettaException(String name) {
        super(String.join(" ", "Command", name, "not found in registry. Available options are: generate, update, help"));
        this.printStackTrace();
        System.out.println(getRegistry().getAllHelpMessages());
    }

    public RosettaException(Command command) {
        super(String.join(" ", "Command ", command.name, "has invalid argument count.",
                "Expected count is", Integer.toString(command.argCount())));
        this.printStackTrace();
        System.out.println(command.getHelpMessage());
    }

    public RosettaException(Throwable throwable) {
        throw new RuntimeException(throwable);
    }

    public CommandRegistry getRegistry() {
        return new CommandRegistry();
    }
}
