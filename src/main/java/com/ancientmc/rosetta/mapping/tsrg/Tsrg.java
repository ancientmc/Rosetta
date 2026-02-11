package com.ancientmc.rosetta.mapping.tsrg;

import com.ancientmc.rosetta.mapping.tsrg.type.TsrgType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Tsrg {
    private static final String HEADER = "tsrg2 obf cnf id\n";
    private final File file;
    private final List<Line<? extends TsrgType>> lines;

    public Tsrg(File file, List<Line<? extends TsrgType>> lines) {
        this.file = file;
        this.lines = lines;
    }

    public record Line<T extends TsrgType>(T type) {

        @Override
        public String toString() {
            return type.toLine();
        }
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

