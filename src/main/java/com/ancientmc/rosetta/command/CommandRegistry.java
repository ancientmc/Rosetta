package com.ancientmc.rosetta.command;

import java.util.ArrayList;
import java.util.List;

public class CommandRegistry {
    private List<Command> list = new ArrayList<>();

    public CommandRegistry() {
        this.list = getList();
    }

    private List<Command> getList() {
        add(new HelpCommand());
        add(new GenerateCommand());
        add(new UpdateCommand());
        return list;
    }

    public void add(Command command) {
        list.add(command);
    }

    public Command get(String name) {
        return list.stream().filter(c -> c.name.equals(name)).findAny().orElse(null);
    }

    public boolean isValidCommand(String name) {
        return get(name) != null;
    }

    public String getAllHelpMessages() {
        List<String> messages = new ArrayList<>();
        list.forEach(cmd -> messages.add(cmd.getHelpMessage()));
        return String.join("\n", messages);
    }
}
