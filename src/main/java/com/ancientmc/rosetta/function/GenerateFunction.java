package com.ancientmc.rosetta.function;

import com.ancientmc.rosetta.Config;
import com.ancientmc.rosetta.jar.Ids;
import com.ancientmc.rosetta.jar.Jar;
import com.ancientmc.rosetta.jar.type.ClassType;
import com.ancientmc.rosetta.jar.type.Field;
import com.ancientmc.rosetta.jar.type.Method;
import com.ancientmc.rosetta.jar.type.Parameter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenerateFunction extends Function {
    private final Jar jar;
    private final Config config;
    private final File tsrg;
    private final File ids;
    private final Map<ClassType, String> classIds;
    private final Map<Field, String> fieldIds;
    private final Map<Method, String> methodIds;
    private final Map<Parameter, String> paramIds;

    public GenerateFunction(Jar jar, Config config, File tsrg, File ids) {
        this.jar = jar;
        this.config = config;
        this.tsrg = tsrg;
        this.ids = ids;

        this.classIds = Ids.getClassIds(jar.classes);
        this.fieldIds = Ids.getFieldIds(jar.fields);
        this.methodIds = Ids.getMethodIds(jar.methods);
        this.paramIds = Ids.getParamIds(jar.params);
    }

    public void exec() {
        try {
            List<String> lines = getLines();

            write(lines);
            writeIds();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getLines() throws IOException {
        List<String> lines = new ArrayList<>();

        // first line
        lines.add("tsrg2 obf cnf id");

        List<ClassType> sortedClasses = jar.classes.stream().filter(c -> config.excludedPackages.stream().noneMatch(c.name::startsWith)).toList();
        sortedClasses.forEach(cls -> {
            String cid = classIds.get(cls);
            addLine(lines, "class", cls.name, getDeobfClass(cls, cid), cid);

            // Get fields in the currently iterated class
            List<Field> sortedFields = cls.getFields(jar);
            sortedFields.forEach(field -> {
                String fid = fieldIds.get(field);
                addLine(lines, "field", field.name, getDeobfField(field, fid), fid);
            });

            // Get methods in the currently iterated class
            List<Method> sortedMethods = cls.getMethods(jar);
            sortedMethods.forEach(method -> {

                // We have to deal with inheritance. If a method is inherited, get the id of the root parent. If not, just get the id of the normal method.
                String mid = method.inherited ? methodIds.get(getSuperMethod(method)) : methodIds.get(method);
                addLine(lines, "method", method.name + " " + method.desc, getDeobfMethod(method, mid), mid);

                if (!method.params.isEmpty()) {
                    method.params.forEach(param -> {
                        String pid = method.inherited
                                ? paramIds.get(getSuperMethod(method).params.get(param.index))
                                : paramIds.get(method.params.get(param.index));
                        addLine(lines, "param", Integer.toString(param.index) + " o", "p_" + pid, pid);
                    });
                }
            });
        });

        return lines;
    }

    public void write(List<String> lines) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tsrg))) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
            writer.flush();
        }
    }

    public void writeIds() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ids))) {
            writer.write(String.join(",", "type", "counter") + "\n");
            writer.write(String.join(",", "classes", Integer.toString(classIds.size())) + "\n");
            writer.write(String.join(",", "fields", Integer.toString(fieldIds.size())) + "\n");
            writer.write(String.join(",", "methods", Integer.toString(methodIds.size())) + "\n");
            writer.write(String.join(",", "params", Integer.toString(paramIds.size())) + "\n");
            writer.flush();
        }
    }

    public String getDeobfClass(ClassType cls, String id) {
        return cls.name.contains(config.premappedClass) ? cls.name : config.namespace + "c_" + id;
    }

    public String getDeobfField(Field field, String id) {
        return field.name.length() <= 2 ? "f_" + id : field.name;
    }

    public String getDeobfMethod(Method method, String id) {
        return method.name.length() <= 2 ? "m_" + id : method.name;
    }

    public Method getSuperMethod(Method method) {
        ClassType superParent = jar.getClass(method.superParentName);
        if (superParent != null) {
            return superParent.getMethod(jar, method.name, method.desc);
        }
        return null;
    }
}
