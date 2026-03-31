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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;

@Command(name = "--update")
public class UpdateCommand implements Callable<Integer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateCommand.class);

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
        log();
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

    private void log() {
        LOGGER.info("UPDATE COMMAND");
        LOGGER.info("Config file -> {}", configFile.getAbsolutePath());
        LOGGER.info("JAR file -> {}", jarFile.getAbsolutePath());
        LOGGER.info("Inheritance file -> {}", inheritanceFile.getAbsolutePath());
        LOGGER.info("Old TSRG file -> {}", oldTsrgFile.getAbsolutePath());
        LOGGER.info("New TSRG file -> {}", oldTsrgFile.getAbsolutePath());
        LOGGER.info("Old IDs file -> {}", oldIdCsv.getAbsolutePath());
        LOGGER.info("New IDs file -> {}", newIdCsv.getAbsolutePath());
    }
}
