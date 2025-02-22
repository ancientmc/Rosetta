package com.ancientmc.rosetta.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Command object.
 */
public abstract class Command {

    /**
     * The command name.
     */
    public String name;

    /**
     * The args passed into the command.
     */
    public List<String> args = new ArrayList<>();

    public Command(String name) {
        this.name = name;
    }

    public Command setArgs(String[] args) {
        // skip index of zero, as 0 is the name of the command.
        this.args.addAll(Arrays.asList(args).subList(1, args.length));
        return this;
    }

    /**
     * Retrieves the argument as a file.
     * @param arg The argument index.
     * @return The file
     */
    public File getFile(int arg) {
        return new File(args.get(arg));
    }

    /**
     * Checks if the number of input arguments is equal to the required number of arguments.
     * @return true of the input number of arguments is correct.
     */
    public boolean argCountValid() {
        return args.size() == argCount();
    }

    /**
     * Main execution method for this command.
     */
    public abstract void exec();

    /**
     * @return The number of required arguments for the given command.
     */
    public abstract int argCount();

    /**
     * The help message is the message that displays for this command after an error, mis-input, or by using the help command.
     * @return The message.
     */
    public abstract String getHelpMessage();
}
