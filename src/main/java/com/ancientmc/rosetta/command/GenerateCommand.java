package com.ancientmc.rosetta.command;

import com.ancientmc.rosetta.Config;
import com.ancientmc.rosetta.function.GenerateFunction;
import com.ancientmc.rosetta.jar.Jar;
import com.ancientmc.rosetta.jar.JarReader;
import com.ancientmc.rosetta.Util;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;

@Command(name = "--generate")
public class GenerateCommand implements Callable<Integer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateCommand.class);

    @Option(names = "--config")
    File configFile;

    @Option(names = "--jar")
    File jarFile;

    @Option(names = "--inheritance")
    File inheritanceFile;

    @Option(names = "--tsrg")
    File tsrgFile;

    @Option(names = "--ids")
    File idCsv;

    @Override
    public Integer call() throws Exception {
        log();
        Config config = new Config(configFile);
        JsonObject inheritance = Util.getJson(inheritanceFile);
        Jar jar = new JarReader(jarFile, inheritance, config).read();

        GenerateFunction function = new GenerateFunction(jar, config, tsrgFile, idCsv);
        function.exec();
        return 0;
    }

    public void log() {
        LOGGER.info("GENERATE COMMAND");
        LOGGER.info("Config file -> {}", configFile.getAbsolutePath());
        LOGGER.info("JAR file -> {}", jarFile.getAbsolutePath());
        LOGGER.info("Inheritance file -> {}", inheritanceFile.getAbsolutePath());
        LOGGER.info("TSRG file -> {}", tsrgFile.getAbsolutePath());
        LOGGER.info("IDs file -> {}", idCsv.getAbsolutePath());
    }
}
