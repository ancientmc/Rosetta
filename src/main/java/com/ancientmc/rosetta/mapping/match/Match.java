package com.ancientmc.rosetta.mapping.match;

import com.ancientmc.rosetta.jar.type.ClassType;
import com.ancientmc.rosetta.jar.type.Field;
import com.ancientmc.rosetta.jar.type.Method;
import com.ancientmc.rosetta.util.Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Match {
    private final File file;
    public List<MatchClass> classes;
    public List<MatchField> fields;
    public List<MatchMethod> methods;
    public List<MatchParameter> params;

    private Match(File file) {
        this.file = file;
        this.classes = getClasses();
        this.fields = getFields();
        this.methods = getMethods();
        this.params = getParams();
    }

    public static Match load(File file) {
        return new Match(file);
    }

    public List<MatchClass> getClasses() {
        try {
            List<MatchClass> classes = new ArrayList<>();
            List<String> lines = Files.readAllLines(file.toPath()).stream().filter(l -> l.startsWith("c\t")).toList();

            lines.forEach(line -> {
                String[] split = line.split("\t");
                String oldName = split[1].substring(1, split[1].indexOf(';')); // L<class_name>; -> <class_name>
                String newName = split[2].substring(1, split[2].indexOf(';'));
                classes.add(new MatchClass(oldName, newName));
            });
            return classes;
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public List<MatchField> getFields() {
        try {
            List<MatchField> fields = new ArrayList<>();
            List<String> matchLines = Files.readAllLines(file.toPath());
            this.classes.forEach(cls -> {
                String classLine = matchLines.stream().filter(line -> line.startsWith("c\tL" + cls.oldName() + ";")).findAny().orElse(null);
                List<String> classBlock = matchLines.subList(matchLines.indexOf(classLine) + 1, Util.getNextMatchClass(matchLines, classLine));

                classBlock.forEach(line -> {
                    if (line.startsWith("\tf\t")) { // field prefix
                        String[] split = line.split("\t");
                        String oldName = split[2].substring(0, split[2].indexOf(";;")); // <field_name>;;<descriptor> -> <field_name>
                        String newName = split[3].substring(0, split[3].indexOf(";;"));
                        fields.add(new MatchField(cls.oldName(), cls.newName(), oldName, newName));
                    }
                });
            });

            return fields;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<MatchMethod> getMethods() {
        List<MatchMethod> methods = new ArrayList<>();

        try {
            List<String> matchLines = Files.readAllLines(file.toPath());
            this.classes.forEach(cls -> {
                String classLine = matchLines.stream().filter(line -> line.startsWith("c\tL" + cls.oldName() + ";")).findAny().orElse(null);
                List<String> classBlock = matchLines.subList(matchLines.indexOf(classLine) + 1, Util.getNextMatchClass(matchLines, classLine));

                classBlock.forEach(line -> {
                    if (line.startsWith("\tm\t")) { // method prefix
                        String[] split = line.split("\t");
                        String oldName = split[2].substring(0, split[2].indexOf('(')); // method name and descriptor are strung together, so we just separate them
                        String newName = split[3].substring(0, split[3].indexOf('('));
                        String oldDesc = split[2].substring(split[2].indexOf('('));
                        String newDesc = split[3].substring(split[3].indexOf('('));


                        methods.add(new MatchMethod(cls.oldName(), cls.newName(), oldName, newName, oldDesc, newDesc, classBlock, line));
                    }
                });
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return methods;
    }

    public List<MatchParameter> getParams() {
        List<MatchParameter> params = new ArrayList<>();

        for (MatchMethod method : this.methods) {
            if (!method.getParams().isEmpty()) {
                params.addAll(method.getParams());
            }
        }

        return params;
    }

    public MatchClass getClass(ClassType cls) {
        return classes.stream().filter(c -> c.newName().equals(cls.name())).findAny().orElse(null);
    }

    public MatchField getField(Field field) {
        return fields.stream().filter(f -> f.newParent().equals(field.parentName()) && f.newName().equals(field.name())).findAny().orElse(null);
    }

    public MatchMethod getMethod(Method method) {
        return methods.stream().filter(m -> m.newParent().equals(method.parentName()) && m.newName().equals(method.name()) && m.newDesc().equals(method.desc())).findAny().orElse(null);
    }

    public boolean isMatchedClass(ClassType cls) {
        return classes.stream().anyMatch(mc -> mc.newName().equals(cls.name()));
    }

    public boolean isMatchedField(Field field) {
        return fields.stream().anyMatch(mf -> mf.newName().equals(field.name()) && mf.newParent().equals(field.parentName()));
    }

    public boolean isMatchedMethod(Method method) {
        return methods.stream().anyMatch(mm -> mm.newName().equals(method.name()) && mm.newParent().equals(method.parentName()) && mm.newDesc().equals(method.desc()));
    }
}
