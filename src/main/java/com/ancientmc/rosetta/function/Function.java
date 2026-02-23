package com.ancientmc.rosetta.function;

import com.ancientmc.rosetta.mapping.tsrg.Tsrg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class Function {
    public void exec() throws IOException {
        Tsrg tsrg = buildTsrg();
        tsrg.write();
        callIdWriter();
    }

    protected abstract Tsrg buildTsrg();

    protected abstract void callIdWriter() throws IOException;

    public void writeIds(File csv, int classes, int fields, int methods, int params) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(csv.toPath())) {
            writer.write("type,counter\n");
            writer.write("classes," + classes + "\n");
            writer.write("fields," + fields + "\n");
            writer.write("methods," + methods + "\n");
            writer.write("params," + params);
            writer.flush();
        }
    }
}
