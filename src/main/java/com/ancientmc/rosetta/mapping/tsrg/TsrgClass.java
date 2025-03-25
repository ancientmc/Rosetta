package com.ancientmc.rosetta.mapping.tsrg;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public record TsrgClass(String obf, String mapped, File file) {
    public String getId() {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            String line = lines.stream().filter(l -> l.contains(mapped + " ")).findAny().orElse(null);

            if (line != null) {
                return line.split(" ")[2];
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
