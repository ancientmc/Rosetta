package com.ancientmc.rosetta.command;

import com.ancientmc.rosetta.Config;
import com.ancientmc.rosetta.function.UpdateFunction;
import com.ancientmc.rosetta.jar.Jar;
import com.ancientmc.rosetta.mapping.match.Match;
import com.ancientmc.rosetta.mapping.tsrg.Tsrg;

import java.io.File;

public class UpdateCommand extends Command {
    public UpdateCommand() {
        super("update");
    }

    @Override
    public void exec() {
        File configFile = getFile(0);
        File jarFile = getFile(1);
        File inheritance = getFile(2);
        File oldTsrgFile = getFile(3);
        File oldIds = getFile(4);
        File matchFile = getFile(5);
        File newTsrgFile = getFile(6);
        File newIds = getFile(7);

        Config config = Config.load(configFile);
        Jar jar = Jar.load(jarFile, inheritance, config);
        Tsrg oldTsrg = Tsrg.load(oldTsrgFile);
        Match match = Match.load(matchFile);

        UpdateFunction function = new UpdateFunction(config, jar, oldTsrg, oldIds, match, newTsrgFile, newIds);
        function.exec();
    }

    @Override
    public int argCount() {
        return 8;
    }

    @Override
    public String getHelpMessage() {
        return String.join(" ", name, "<config>", "<jar>", "<inheritance-json>", "<old-tsrg>", "<old-ids>", "<match>", "<new-tsrg>", "<new-ids>");
    }
}
