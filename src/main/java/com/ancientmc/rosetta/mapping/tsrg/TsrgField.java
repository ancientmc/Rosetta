package com.ancientmc.rosetta.mapping.tsrg;

import com.ancientmc.rosetta.util.Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class TsrgField {
    public String obf;
    public String mapped;
    public String parent;
    public String id;

    public TsrgField(String obf, String mapped, String parent) {
        this.obf = obf;
        this.mapped = mapped;
        this.parent = parent;
    }

    public TsrgField setId(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            String classLine = lines.stream().filter(l -> l.startsWith(parent + " ")).findAny().orElse(null);
            List<String> classBlock = lines.subList(lines.indexOf(classLine), Util.getNextTsrgClass(lines, classLine));
            classBlock.stream().filter(l -> l.contains(mapped + " ")).findAny()
                    .ifPresent(line -> this.id = line.split(" ")[2]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return this;
    }
}
