package com.ancientmc.rosetta.command;

import com.ancientmc.rosetta.util.RosettaException;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help");
    }

    @Override
    public void exec() {
        Command command;
        String first = this.args.getFirst();
        if (first.equals("generate")) {
            command = new GenerateCommand();
            exec(command);
        } else if (first.equals("update")) {
            command = new UpdateCommand();
            exec(command);
        } else if (first.equals("all")) {
            CommandRegistry registry = new CommandRegistry();
            exec(registry);
        } else {
            throw new RosettaException(args.getFirst());
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
