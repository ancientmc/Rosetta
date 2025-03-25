package com.ancientmc.rosetta.function;

import com.ancientmc.rosetta.Config;
import com.ancientmc.rosetta.jar.Ids;
import com.ancientmc.rosetta.jar.Jar;
import com.ancientmc.rosetta.jar.type.ClassType;
import com.ancientmc.rosetta.jar.type.Field;
import com.ancientmc.rosetta.jar.type.Method;
import com.ancientmc.rosetta.jar.type.Parameter;
import com.ancientmc.rosetta.mapping.match.*;
import com.ancientmc.rosetta.mapping.tsrg.*;

import java.io.File;
import java.util.*;

public class UpdateFunction extends Function {
    private final Config config;
    private final Jar jar;
    private final Tsrg oldTsrg;
    private final Match match;
    private final File newTsrg;
    private final File newIds;

    // The updated counters for each type, which are exported as a CSV file.

    private final Map<ClassType, String> newClassIds;
    private final Map<Field, String> newFieldIds;
    private final Map<Method, String> newMethodIds;
    private final Map<Parameter, String> newParamIds;


    public UpdateFunction(Config config, Jar jar, Tsrg oldTsrg, File oldIds, Match match, File newTsrg, File newIds) {
        this.config = config;
        this.jar = jar;
        this.oldTsrg = oldTsrg;
        this.match = match;
        this.newTsrg = newTsrg;
        this.newIds = newIds;

        this.newClassIds = Ids.getNewClassIds(jar.classes, match, oldIds);
        this.newFieldIds = Ids.getNewFieldIds(jar.fields, match, oldIds);
        this.newMethodIds = Ids.getNewMethodIds(jar.methods, match, oldIds);
        this.newParamIds = Ids.getNewParameterIds(jar.params, match, oldIds);
    }

    public void exec() {
        super.exec(newTsrg, newIds);
    }

    public List<String> getLines() {
        List<String> lines = new ArrayList<>();

        // first line
        lines.add("tsrg2 obf cnf id");

        List<ClassType> sortedClasses = jar.classes.stream().filter(c -> config.excluded.stream().noneMatch(c.name()::startsWith)).toList();
        sortedClasses.forEach(cls -> {
            List<Field> sortedFields = cls.getFields(jar);
            List<Method> sortedMethods = cls.getMethods(jar);

            if (match.isMatchedClass(cls)) {
                MatchClass matchClass = match.getClass(cls);
                TsrgClass tsrgClass = oldTsrg.getClass(matchClass);
                addLine(lines, "class", cls.name(), tsrgClass.mapped(), tsrgClass.getId(), false);
            } else {
                String cid = newClassIds.get(cls);
                addLine(lines, "class", cls.name(), config.namespace + "c_" + cid, cid, true);
            }

            for (Field field : sortedFields) {
                if (match.isMatchedField(field)) {
                    MatchField oldField = match.getField(field);
                    TsrgField tsrgField = oldTsrg.getField(oldField);
                    addLine(lines, "field", field.name(), tsrgField.mapped(), tsrgField.getId(), false);
                } else {
                    String fid = newFieldIds.get(field);
                    addLine(lines, "field", field.name(), "f_" + fid, fid, true);
                }
            }

            for (Method method : sortedMethods) {
                String mid = "";
                if (method.inherited()) {
                    Method superMethod = method.getSuperMethod(jar);
                    if (match.isMatchedMethod(superMethod)) {
                        addMatchedMethod(lines, superMethod, newParamIds);
                    } else {
                        mid = newMethodIds.get(superMethod);
                        addLine(lines, "method", superMethod.name() + " " + superMethod.desc(), getMappedMethod(superMethod.name(), mid), mid, true);
                        addParams(lines, superMethod, newParamIds);
                    }
                } else {
                    if (match.isMatchedMethod(method)) {
                        addMatchedMethod(lines, method, newParamIds);
                    } else {
                        mid = newMethodIds.get(method);
                        addLine(lines, "method", method.name() + " " + method.desc(), getMappedMethod(method.name(), mid), mid, true);
                        addParams(lines, method, newParamIds);
                    }
                }
            }
        });

        return lines;
    }

    public static String getMappedMethod(String name, String id) {
        return name.endsWith("init>") ? name : "m_" + id;
    }

    public void addMatchedMethod(List<String> lines, Method method, Map<Parameter, String> newParamIds) {
        MatchMethod matchMethod = match.getMethod(method);
        TsrgMethod tsrgMethod = oldTsrg.getMethod(matchMethod);
        addLine(lines, "method", method.name() + " " + method.desc(), tsrgMethod.mapped(), tsrgMethod.getId(), false);
        addParams(lines, method, tsrgMethod, matchMethod, newParamIds);
    }

    // For matched methods
    public static void addParams(List<String> lines, Method method, TsrgMethod tsrgMethod, MatchMethod matchMethod, Map<Parameter, String> newParamIds) {
        if (!method.getParams().isEmpty()) {
            for (Parameter param : method.getParams()) {
                MatchParameter matchParam = matchMethod.getParams().stream().filter(mp -> mp.newIndex() == param.index()).findAny().orElse(null);

                if (matchParam != null) {
                    addOldParam(lines, param, matchMethod, tsrgMethod);
                } else {
                    addNewParam(lines, param, newParamIds);
                }
            }
        }
    }

    // For new methods
    public static void addParams(List<String> lines, Method method, Map<Parameter, String> newParamIds) {
        if (!method.getParams().isEmpty()) {
            method.getParams().forEach(p -> addNewParam(lines, p, newParamIds));
        }
    }

    public static void addOldParam(List<String> lines, Parameter param, MatchMethod matchMethod, TsrgMethod tsrgMethod) {
        MatchParameter matchParam = matchMethod.getParameter(param.index());

        if (matchParam != null) {
            TsrgParameter tsrgParam = tsrgMethod.getParameter(matchParam.oldIndex());
            addLine(lines, "param", param.index() + " o", tsrgParam.name(), tsrgParam.getId(), false);
        }
    }

    public static void addNewParam(List<String> lines, Parameter param, Map<Parameter, String> newParamIds) {
        String pid = newParamIds.get(param);
        addLine(lines, "param", param.index() + " o", "p_" + pid, pid, true);
    }
}