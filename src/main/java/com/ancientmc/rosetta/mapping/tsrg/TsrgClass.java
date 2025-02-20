package com.ancientmc.rosetta.mapping.tsrg;

import net.minecraftforge.srgutils.IMappingFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;

public class TsrgClass {
    public String obf;
    public String mapped;
    public String id;
    public List<TsrgMethod> methods;
    public List<TsrgField> fields;

    public TsrgClass(String obf, String mapped) {
        this.obf = obf;
        this.mapped = mapped;
    }

    public TsrgClass setId(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            lines.stream().filter(l -> l.contains(mapped)).findAny()
                    .ifPresent(line -> this.id = line.split(" ")[2]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }
}
