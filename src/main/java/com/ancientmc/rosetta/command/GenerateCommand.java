package com.ancientmc.rosetta.command;

import com.ancientmc.rosetta.Config;
import com.ancientmc.rosetta.function.GenerateFunction;
import com.ancientmc.rosetta.jar.Jar;

import java.io.File;

public class GenerateCommand extends Command {

    public GenerateCommand() {
        super("generate");
    }

    @Override
    public void exec() {
        File configFile = getFile(0);
        File jarFile = getFile(1);
        File inheritance = getFile(2);
        File tsrg = getFile(3);
        File ids = getFile(4);

        Config config = Config.load(configFile);
        Jar jar = Jar.load(jarFile, inheritance, config);

        GenerateFunction function = new GenerateFunction(jar, config, tsrg, ids);
        function.exec();
    }

    @Override
    public int argCount() {
        return 5;
    }

    @Override
    public String getHelpMessage() {
        return String.join(" ", name, "<config>", "<jar>", "<inheritance-json>", "<tsrg>", "<ids>");
    }
}
