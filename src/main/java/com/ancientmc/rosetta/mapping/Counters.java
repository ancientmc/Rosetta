package com.ancientmc.rosetta.mapping;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Counters {
    private final Map<String, Integer> entries;

    private Counters(Map<String, Integer> entries) {
        this.entries = entries;
    }

    public static Counters read(File csv) throws IOException {
        List<String> lines = Files.readAllLines(csv.toPath());
        Map<String, Integer> entries = new HashMap<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] split = line.split(",");
            int count = Integer.parseInt(split[1]);
            entries.put(split[0], count);
        }

        return new Counters(entries);
    }

    public int getCounter(String type) {
        return entries.get(type);
    }
}
