package com.ancientmc.rosetta.command;

import com.ancientmc.rosetta.util.RosettaException;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help");
    }

    @Override
    public void exec() {
        Command command;
        switch (args.getFirst()) {
            case "generate" -> {
                command = new GenerateCommand();
                exec(command);
            }
            case "update" -> {
                command = new UpdateCommand();
                exec(command);
            }
            case "all" -> {
                CommandRegistry registry = new CommandRegistry();
                exec(registry);
            }
            default -> throw new RosettaException(args.getFirst());
        }
    }

    public void exec(Command command) {
        System.out.println(command.getHelpMessage());
    }

    public void exec(CommandRegistry registry) {
        System.out.println(registry.getAllHelpMessages());
    }

    @Override
    public int argCount() {
        return 0;
    }

    @Override
    public boolean argCountValid() {
        return args.isEmpty() || args.size() == 1;
    }

    @Override
    public String getHelpMessage() {
        return name + " <command-name>, or 'all' to print all commands";
    }
}
