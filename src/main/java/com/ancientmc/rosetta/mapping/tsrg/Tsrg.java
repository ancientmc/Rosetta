package com.ancientmc.rosetta.mapping.tsrg;

import com.ancientmc.rosetta.mapping.tsrg.type.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

public class Tsrg {
    private static final String HEADER = "tsrg2 obf cnf id\n";
    private final File file;
    private final List<TsrgType> lines;
    private final List<TsrgClass> classes;

    public Tsrg(File file, List<TsrgType> lines, List<TsrgClass> classes) {
        this.file = file;
        this.lines = lines;
        this.classes = classes;
    }

    public List<TsrgClass> getClasses() {
        return classes;
    }

    public List<TsrgField> getFields() {
        List<TsrgField> fields = new LinkedList<>();
        classes.forEach(cls -> fields.addAll(cls.getFields()));
        return fields;
    }

    public List<TsrgMethod> getMethods() {
        List<TsrgMethod> methods = new LinkedList<>();
        classes.forEach(cls -> methods.addAll(cls.getMethods()));
        return methods;
    }

    public TsrgClass getClass(String name) {
        return classes.stream().filter(c -> c.getObf().equals(name)).findAny().orElseThrow();
    }

    public TsrgField getField(String name, String parentName) {
        return getFields().stream().filter(f -> f.getParent().getObf().equals(parentName)
                && f.getObf().equals(name)).findAny().orElseThrow();
    }

    public TsrgMethod getMethod(String name, String desc, String parentName) {
        return getMethods().stream().filter(m -> m.getParent().getObf().equals(parentName)
                && m.getObf().equals(name) && m.getDesc().equals(desc))
                .findAny().orElseThrow();
    }

    public void write() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
            writer.write(HEADER);
            writer.flush();

            for (TsrgType line : lines) {
                writer.write(line.toString() + "\n");
                writer.flush();
            }
        }
    }
}

