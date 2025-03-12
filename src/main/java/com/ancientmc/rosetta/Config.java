package com.ancientmc.rosetta;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Config {
    public List<String> excluded;
    public String premapped;
    public String namespace;

    private Config(List<String> lines) {
        lines.forEach(line -> {
            String[] split = line.split("=");
            switch (split[0]) {
                case "excluded" ->
                        excluded = toList(split[1]);
                case "premapped" ->
                        premapped = split[1];
                case "namespace" ->
                        namespace = split[1];
            }
        });
    }

    public static Config load(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            return new Config(lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> toList(String entry) {
        String bracketless = entry.substring(entry.indexOf('[') + 1, entry.indexOf(']'));
        String[] split = bracketless.split(",");
        if (split.length > 0) {
            return Arrays.asList(split);
        } else {
            return Collections.singletonList(""); // blank if no exclusions are present.
        }
    }
}
