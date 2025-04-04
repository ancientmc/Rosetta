package com.ancientmc.rosetta.function;

import com.ancientmc.rosetta.Config;
import com.ancientmc.rosetta.jar.Ids;
import com.ancientmc.rosetta.jar.Jar;
import com.ancientmc.rosetta.jar.type.ClassType;
import com.ancientmc.rosetta.jar.type.Field;
import com.ancientmc.rosetta.jar.type.Method;
import com.ancientmc.rosetta.jar.type.Parameter;

import java.io.File;
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
        super.exec(tsrg, ids);
    }

    public List<String> getLines() throws IOException {
        List<String> lines = new ArrayList<>();

        // first line
        lines.add("tsrg2 obf cnf id");

        List<ClassType> sortedClasses = jar.classes.stream().filter(c -> config.excluded.stream().noneMatch(c.name()::startsWith)).toList();
        sortedClasses.forEach(cls -> {
            List<Field> sortedFields = cls.getFields(jar);
            List<Method> sortedMethods = cls.getMethods(jar);

            String cid = classIds.get(cls);
            addLine(lines, "class", cls.name(), getMappedClass(cls, cid), cid);

            // Add fields in the currently iterated class
            sortedFields.forEach(field -> {
                String fid = fieldIds.get(field);
                addLine(lines, "field", field.name(), getMappedField(field, fid), fid);
            });

            // Add methods in the currently iterated class
            sortedMethods.forEach(method -> {

                // We have to deal with inheritance. If a method is inherited, get the id of the root parent. If not, just get the id of the normal method.
                Method superMethod = method.getSuperMethod(jar);
                String mid = method.inherited() ? methodIds.get(superMethod) : methodIds.get(method);
                addLine(lines, "method", method.name() + " " + method.desc(), getMappedMethod(method, mid), mid);

                if (!method.getParams().isEmpty()) {
                    method.getParams().forEach(param -> {
                        String pid = method.inherited()
                                ? paramIds.get(superMethod.getParams().get(param.index()))
                                : paramIds.get(method.getParams().get(param.index()));
                        addLine(lines, "param", param.index() + " o", "p_" + pid, pid);
                    });
                }
            });
        });

        return lines;
    }

    public String getMappedClass(ClassType cls, String id) {
        return cls.name().contains(config.premapped) ? cls.name() : config.namespace + "c_" + id;
    }

    public String getMappedField(Field field, String id) {
        return field.name().length() <= 2 ? "f_" + id : field.name();
    }

    public String getMappedMethod(Method method, String id) {
        return method.name().length() <= 2 ? "m_" + id : method.name();
    }
}
