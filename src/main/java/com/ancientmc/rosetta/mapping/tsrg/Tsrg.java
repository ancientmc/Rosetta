package com.ancientmc.rosetta.mapping.tsrg;

import com.ancientmc.rosetta.mapping.tsrg.type.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Tsrg {
    private static final String HEADER = "tsrg2 obf cnf id\n";
    private final File file;
    private final List<Line<? extends TsrgType>> lines;
    private final List<TsrgClass> classes;
    private final List<TsrgField> fields;
    private final List<TsrgMethod> methods;
    private final List<TsrgParameter> params;

    public Tsrg(TsrgBuilder builder) {
        this.file = builder.file;
        this.lines = builder.lines;
        this.classes = builder.classes;
        this.fields = builder.fields;
        this.methods = builder.methods;
        this.params = builder.params;
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

    public List<TsrgClass> getClasses() {
        return classes;
    }

    public List<TsrgField> getFields() {
        return fields;
    }

    public List<TsrgMethod> getMethods() {
        return methods;
    }

    public List<TsrgParameter> getParams() {
        return params;
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

