package com.ancientmc.rosetta.function;

import com.ancientmc.rosetta.jar.IdSet;
import com.ancientmc.rosetta.jar.type.ClassType;
import com.ancientmc.rosetta.jar.type.Field;
import com.ancientmc.rosetta.jar.type.Method;
import com.ancientmc.rosetta.jar.type.Parameter;
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

    public abstract Tsrg buildTsrg();

    public abstract void callIdWriter() throws IOException;

    public void writeIds(File csv, IdSet<ClassType> classIds, IdSet<Field> fieldIds,
                         IdSet<Method> methodIds, IdSet<Parameter> paramIds) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(csv.toPath())) {
            writer.write("type,counter\n");
            writer.write("classes," + classIds.counter + "\n");
            writer.write("fields," + fieldIds.counter + "\n");
            writer.write("methods," + methodIds.counter + "\n");
            writer.write("params," + paramIds.counter + "\n");
        }
    }
}
