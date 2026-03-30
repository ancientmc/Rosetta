package com.ancientmc.rosetta.mapping.match;

import com.ancientmc.rosetta.mapping.match.type.MatchClass;
import com.ancientmc.rosetta.mapping.match.type.MatchField;
import com.ancientmc.rosetta.mapping.match.type.MatchMethod;
import com.ancientmc.rosetta.mapping.match.type.MatchParameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

/**
 * Builds a Match file object from a read file.
 */
public class MatchReader {
    private final File file;

    public final List<MatchClass> classes = new LinkedList<>();
    public final List<MatchField> fields = new LinkedList<>();
    public final List<MatchMethod> methods = new LinkedList<>();
    public final List<MatchParameter> params = new LinkedList<>();

    public MatchReader(File file) {
        this.file = file;
    }

    public Match read() throws IOException {
        List<String> lines = Files.readAllLines(file.toPath());

        // dummy inits to avoid null bs. Reassigned on each new instance.
        MatchClass currentClass = MatchClass.dummy();
        MatchMethod currentMethod = MatchMethod.dummy();

        for (String line : lines) {
            if (line.startsWith("c\tL")) {
                currentMethod.setParams(params);
                currentClass.setChildren(fields, methods);
                currentClass = getClass(line);
                classes.add(currentClass);
            } else if (line.startsWith("\tf\t")) {
                currentMethod.setParams(params);
                MatchField field = getField(line, currentClass);
                fields.add(field);
            } else if (line.startsWith("\tm\t")) {
                currentMethod.setParams(params);
                currentMethod = getMethod(line, currentClass);
                methods.add(currentMethod);
            } else if (line.startsWith("\t\tma\t")) {
                MatchParameter param = getParam(line, currentMethod);
                params.add(param);
            } else if (line.startsWith("\t\tmv\t") || line.startsWith("\tmu\t")
                    || line.startsWith("\tfu\t") || line.startsWith("\t\tmvu")
                    || line.startsWith("\t\tmau\t")) {
                currentMethod.setParams(params);
            }

            if (line.equals(lines.getLast())) {
                currentMethod.setParams(params);
                currentClass.setChildren(fields, methods);
            }
        }

        return new Match(this);
    }

    public MatchClass getClass(String line) {
        String[] split = line.split("\t");
        return new MatchClass(stripClass(split[1]), stripClass(split[2]));
    }

    public MatchField getField(String line, MatchClass parent) {
        String[] split = line.split("\t");
        return new MatchField(stripField(split[2]), stripField(split[3]), parent);
    }

    public MatchMethod getMethod(String line, MatchClass parent) {
        String[] split = line.split("\t");
        String oldMtd = split[2];
        String newMtd = split[3];
        return new MatchMethod(getMethodName(oldMtd), getMethodName(newMtd), getMethodDesc(oldMtd), getMethodDesc(newMtd), parent);
    }

    public MatchParameter getParam(String line, MatchMethod parent) {
        String[] split = line.split("\t");
        return new MatchParameter(Integer.parseInt(split[3]), Integer.parseInt(split[4]), parent);
    }

    // Lclass; -> class
    public String stripClass(String base) {
        return base.substring(base.indexOf('L') + 1, base.lastIndexOf(';'));
    }

    // field;;X -> field
    public String stripField(String base) {
        return base.substring(0, base.indexOf(';'));
    }

    public String getMethodName(String base) {
        return base.substring(0, base.indexOf('('));
    }

    public String getMethodDesc(String base) {
        return base.substring(base.indexOf('('));
    }
}
