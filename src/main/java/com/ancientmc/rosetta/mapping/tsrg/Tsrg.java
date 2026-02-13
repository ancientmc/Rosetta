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
    private final List<Line<? extends TsrgType>> lines;
    private final List<TsrgClass> classes;

    public Tsrg(File file, List<Line<? extends TsrgType>> lines, List<TsrgClass> classes) {
        this.file = file;
        this.lines = lines;
        this.classes = classes;
    }

    public record Line<T extends TsrgType>(T type) {

        @Override
        public String toString() {
            return type.toString();
        }

        public T getType() {
            return type;
        }
    }

    public List<Line<? extends TsrgType>> getLines() {
        return lines;
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

    public void write() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
            writer.write(HEADER);
            writer.flush();

            for (Line<? extends TsrgType> line : lines) {
                writer.write(line.toString());
                writer.flush();
            }
        }
    }
}

