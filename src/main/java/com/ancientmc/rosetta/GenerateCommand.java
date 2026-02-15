package com.ancientmc.rosetta;

import com.ancientmc.rosetta.function.GenerateFunction;
import com.ancientmc.rosetta.jar.Jar;
import com.ancientmc.rosetta.jar.JarReader;
import com.ancientmc.rosetta.util.Util;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;

@Command(name = "--generate")
public class GenerateCommand implements Callable<Integer> {

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
        Config config = new Config(configFile);
        JsonObject inheritance = Util.getJson(inheritanceFile);
        Jar jar = new JarReader(jarFile, inheritance, config).read();

        GenerateFunction function = new GenerateFunction(jar, config, tsrgFile, idCsv);
        function.exec();
        return 0;
    }
}
