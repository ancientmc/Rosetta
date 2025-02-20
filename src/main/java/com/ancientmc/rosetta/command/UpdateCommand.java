package com.ancientmc.rosetta.command;

import java.io.File;
import java.io.FileNotFoundException;

public class UpdateCommand extends Command {
    public UpdateCommand() {
        super("update");
    }

    @Override
    public void exec() {
        // Nothing here yet.
    }

    @Override
    public int argCount() {
        return 7;
    }

    @Override
    public String getHelpMessage() {
        return String.join(" ", name, "<config>", "<jar>", "<inheritance-json>", "<old-tsrg>", "<old-ids>", "<match>", "<new-tsrg>", "<new-ids>");
    }
}
