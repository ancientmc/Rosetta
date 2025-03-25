package com.ancientmc.rosetta.mapping.tsrg;

import com.ancientmc.rosetta.util.Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public record TsrgField(String obf, String mapped, String parent, File file) {
    public String getId() {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            String classLine = lines.stream().filter(l -> l.startsWith(parent + " ")).findAny().orElse(null);

            List<String> classBlock = lines.subList(lines.indexOf(classLine), Util.getNextTsrgClass(lines, classLine));
            String line = classBlock.stream().filter(l -> l.contains(mapped + " ")).findAny().orElse(null);

            if (line != null) {
                return line.split(" ")[2];
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
