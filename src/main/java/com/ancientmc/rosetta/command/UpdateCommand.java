package com.ancientmc.rosetta.command;

import com.ancientmc.rosetta.Config;
import com.ancientmc.rosetta.Util;
import com.ancientmc.rosetta.function.UpdateFunction;
import com.ancientmc.rosetta.jar.Jar;
import com.ancientmc.rosetta.jar.JarReader;
import com.ancientmc.rosetta.mapping.Counters;
import com.ancientmc.rosetta.mapping.match.Match;
import com.ancientmc.rosetta.mapping.match.MatchReader;
import com.ancientmc.rosetta.mapping.tsrg.Tsrg;
import com.ancientmc.rosetta.mapping.tsrg.TsrgReader;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;

@Command(name = "--update")
public class UpdateCommand implements Callable<Integer> {
    @Option(names = "--config")
    File configFile;

    @Option(names = "--jar")
    File jarFile;

    @Option(names = "--inheritance")
    File inheritanceFile;

    @Option(names = "--match")
    File matchFile;

    @Option(names = "--old-tsrg")
    File oldTsrgFile;

    @Option(names = "--new-tsrg")
    File newTsrgFile;

    @Option(names = "--old-ids")
    File oldIdCsv;

    @Option(names = "--new-ids")
    File newIdCsv;

    @Override
    public Integer call() throws Exception {
        Config config = new Config(configFile);
        JsonObject inheritance = Util.getJson(inheritanceFile);
        Jar jar = new JarReader(jarFile, inheritance, config).read();
        Tsrg oldTsrg = new TsrgReader(oldTsrgFile).read();
        Match match = new MatchReader(matchFile).read();
        Counters counters = Counters.read(oldIdCsv);

        UpdateFunction function = new UpdateFunction(jar, config, match, oldTsrg, counters, newTsrgFile, newIdCsv);
        function.exec();

        return 0;
    }
}
