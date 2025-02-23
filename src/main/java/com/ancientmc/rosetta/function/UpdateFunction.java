package com.ancientmc.rosetta.function;

import com.ancientmc.rosetta.Config;
import com.ancientmc.rosetta.jar.Jar;
import com.ancientmc.rosetta.jar.type.ClassType;
import com.ancientmc.rosetta.jar.type.Field;
import com.ancientmc.rosetta.jar.type.Method;
import com.ancientmc.rosetta.jar.type.Parameter;
import com.ancientmc.rosetta.mapping.match.*;
import com.ancientmc.rosetta.mapping.tsrg.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.*;

public class UpdateFunction extends Function {
    private final Config config;
    private final Jar jar;
    private final Tsrg oldTsrg;
    private final File oldIds;
    private final Match match;
    private final File newTsrg;
    private final File newIds;

    // The updated counters for each type, which are exported as a CSV file.
    private static int lastClassCounter;
    private static int lastFieldCounter;
    private static int lastMethodCounter;
    private static int lastParamCounter;

    public UpdateFunction(Config config, Jar jar, Tsrg oldTsrg, File oldIds, Match match, File newTsrg, File newIds) {
        this.config = config;
        this.jar = jar;
        this.oldTsrg = oldTsrg;
        this.oldIds = oldIds;
        this.match = match;
        this.newTsrg = newTsrg;
        this.newIds = newIds;
    }

    @Override
    public void exec() {
        try {
            List<String> lines = getLines(jar, oldTsrg, match, config, oldIds);
            write(newTsrg, lines);
            writeIds(newIds);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getLines(Jar jar, Tsrg tsrg, Match match, Config config, File ids) throws IOException {
        List<String> lines = new ArrayList<>();

        // Maps of types that are new to our new version, and not found in the match file.
        Map<ClassType, String> newClassIds = getNewClassIds(jar.classes, match, ids);
        Map<Field, String> newFieldIds = getNewFieldIds(jar.fields, match, ids);
        Map<Method, String> newMethodIds = getNewMethodIds(jar.methods, match, ids);
        Map<Parameter, String> newParamIds = getNewParameterIds(jar.params, match, ids);

        // first line
        lines.add("tsrg2 obf cnf id");

        List<ClassType> sortedClasses = jar.classes.stream().filter(c -> config.excludedPackages.stream().noneMatch(c.name::startsWith)).toList();
        sortedClasses.forEach(cls -> {
            if (isMatchedClass(cls, match)) {
                MatchClass matchClass = match.getClass(cls);
                TsrgClass tsrgClass = tsrg.getClass(matchClass);
                addLine(lines, "class", cls.name, tsrgClass.mapped, tsrgClass.id);
            } else {
                String cid = newClassIds.get(cls);
                addLine(lines, "class", cls.name, config.namespace + cid, cid);
            }

            List<Field> sortedFields = jar.fields.stream().filter(f -> f.parentName.equals(cls.name)).toList();
            for (Field field : sortedFields) {
                if (isMatchedField(field, match)) {
                    MatchField oldField = match.getField(field);
                    TsrgField tsrgField = tsrg.getField(oldField);
                    addLine(lines, "field", field.name, tsrgField.mapped, tsrgField.id);
                } else {
                    String fid = newFieldIds.get(field);
                    addLine(lines, "field", field.name, "f_" + fid, fid);
                }
            }

            List<Method> sortedMethods = jar.methods.stream().filter(m -> m.parentName.equals(cls.name)).toList();
            for (Method method : sortedMethods) {
                String mid = "";
                if (method.inherited) {
                    Method superMethod = getSuperMethod(method);
                    if (isMatchedMethod(superMethod, match)) {
                        MatchMethod matchSuperMethod = match.getMethod(superMethod);
                        TsrgMethod tsrgSuperMethod = tsrg.getMethod(matchSuperMethod);
                        addLine(lines, "method", superMethod.name + " " + superMethod.desc, tsrgSuperMethod.mapped, tsrgSuperMethod.id);
                        addParams(lines, superMethod, tsrgSuperMethod, matchSuperMethod, newParamIds);
                    } else {
                        mid = newMethodIds.get(superMethod);
                        addLine(lines, "method", superMethod.name + " " + superMethod.desc, getMappedMethod(superMethod.name, mid), mid);
                        addParams(lines, superMethod, newParamIds);
                    }
                } else {
                    if (isMatchedMethod(method, match)) {
                        MatchMethod matchMethod = match.getMethod(method);
                        TsrgMethod tsrgMethod = tsrg.getMethod(matchMethod);
                        addLine(lines, "method", method.name + " " + method.desc, tsrgMethod.mapped, tsrgMethod.id);
                        addParams(lines, method, tsrgMethod, matchMethod, newParamIds);
                    } else {
                        mid = newMethodIds.get(method);
                        addLine(lines, "method", method.name + " " + method.desc, getMappedMethod(method.name, mid), mid);
                        addParams(lines, method, newParamIds);
                    }
                }
            }
        });

        return lines;
    }

    public static void write(File tsrg, List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tsrg))) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeIds(File ids) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ids))) {
            writer.write(String.join(",", "classes", Integer.toString(lastClassCounter - 1)) + "\n");
            writer.write(String.join(",", "fields", Integer.toString(lastFieldCounter - 1)) + "\n");
            writer.write(String.join(",", "methods", Integer.toString(lastMethodCounter - 1)) + "\n");
            writer.write(String.join(",", "params", Integer.toString(lastParamCounter - 1)));
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isMatchedClass(ClassType cls, Match match) {
        return match.classes.stream().anyMatch(mc -> mc.newName.equals(cls.name));
    }

    public static boolean isMatchedField(Field field, Match match) {
        return match.fields.stream().anyMatch(mf -> mf.newName.equals(field.name) && mf.newParent.equals(field.parentName));
    }

    public static boolean isMatchedMethod(Method method, Match match) {
        return match.methods.stream().anyMatch(mm -> mm.newName.equals(method.name) && mm.newParent.equals(method.parentName) && mm.newDesc.equals(method.desc));
    }

    public static Map<ClassType, String> getNewClassIds(List<ClassType> classes, Match match, File ids) throws IOException {
        int counter = getCount(ids, "classes") + 1;
        Map<ClassType, String> newClasses = new HashMap<>();

        for (ClassType cls : classes) {
            if (!isMatchedClass(cls, match)) {
                newClasses.put(cls, getFormattedId(counter));
                counter++;
            }
        }

        lastClassCounter = counter;
        return newClasses;
    }

    public static Map<Field, String> getNewFieldIds(List<Field> fields, Match match, File ids) throws IOException {
        int counter = getCount(ids, "fields") + 1;
        Map<Field, String> newField = new HashMap<>();

        for (Field field : fields) {
            if (!isMatchedField(field, match)) {
                newField.put(field, getFormattedId(counter));
                counter++;
            }
        }

        lastFieldCounter = counter;
        return newField;
    }

    public static Map<Method, String> getNewMethodIds(List<Method> methods, Match match, File ids) throws IOException {
        int counter = getCount(ids, "methods") + 1;
        Map<Method, String> newMethods = new HashMap<>();

        for (Method method : methods) {
            if (!isMatchedMethod(method, match) && !method.inherited) {
                newMethods.put(method, getFormattedId(counter));
                counter++;
            }
        }

        lastMethodCounter = counter;
        return newMethods;
    }

    public static Map<Parameter, String> getNewParameterIds(List<Parameter> params, Match match, File ids) throws IOException {
        int counter = getCount(ids, "params") + 1;
        Map<Parameter, String> newParams = new HashMap<>();

        // 1) param's method is new
        // 2) param's method is old but the param is new
        for (Parameter param : params) {
            if (!param.method.inherited) {
                if (!isMatchedMethod(param.method, match))  { // fix
                    newParams.put(param, getFormattedId(counter));
                    counter++;
                } else {
                    MatchMethod matchMethod = match.getMethod(param.method);
                    if (!matchMethod.params.isEmpty()) {
                        if (matchMethod.params.stream().noneMatch(mp -> mp.newIndex == param.index)) {
                            System.out.println("CLASS=" + param.method.parentName + " METHOD=" + param.method.name + " INDEX=" + param.index);
                            newParams.put(param, getFormattedId(counter));
                            counter++;
                        }
                    } else { // Adds new ids to a matched method without params.
                        newParams.put(param, getFormattedId(counter));
                        counter++;
                    }
                }
            }
        }

        lastParamCounter = counter;
        return newParams;
    }

    public static int getCount(File ids, String type) throws IOException {
        String line = Files.readAllLines(ids.toPath()).stream().filter(l -> l.startsWith(type)).findAny().orElse(null);
        if (line != null) {
            String[] split = line.split(",");
            return Integer.parseInt(split[1]);
        }
        return -1;
    }

    public static String getFormattedId(int id) {
        return new DecimalFormat("00000").format(id);
    }

    public static String getMappedMethod(String name, String id) {
        return name.endsWith("init>") ? name : "m_" + id;
    }

    public Method getSuperMethod(Method method) {
        ClassType superParent = jar.getClass(method.superParentName);
        if (superParent != null) {
            return superParent.getMethod(jar, method.name, method.desc);
        }
        return null;
    }

    // For matched methods
    public static void addParams(List<String> lines, Method method, TsrgMethod tsrgMethod, MatchMethod matchMethod, Map<Parameter, String> newParamIds) {
        if (!method.params.isEmpty()) {
            for (Parameter param : method.params) {
                MatchParameter matchParam = matchMethod.params.stream().filter(mp -> mp.newIndex == param.index).findAny().orElse(null);
                if (matchParam != null) {
                    System.out.println("\tMatched param of index " + param.index);
                    addOldParam(lines, param, matchMethod, tsrgMethod);
                } else {
                    System.out.println("\tNew param of index " + param.index);
                    addNewParam(lines, param, newParamIds);
                }
            }
        }
    }

    // For new methods
    public static void addParams(List<String> lines, Method method, Map<Parameter, String> newParamIds) {
        if (!method.params.isEmpty()) {
            method.params.forEach(p -> addNewParam(lines, p, newParamIds));
        }
    }

    public static void addOldParam(List<String> lines, Parameter param, MatchMethod matchMethod, TsrgMethod tsrgMethod) {
        MatchParameter matchParam = matchMethod.getParameter(param.index);
        if (matchParam != null) {
            TsrgParameter tsrgParam = tsrgMethod.getParameter(matchParam.oldIndex);
            System.out.println("\t\told id is " + tsrgParam.id);
            addLine(lines, "param", param.index + " o", tsrgParam.name, tsrgParam.id);
        }
    }

    public static void addNewParam(List<String> lines, Parameter param, Map<Parameter, String> newParamIds) {
        String pid = newParamIds.get(param);
        System.out.println("creating new id " + pid);
        lines.add("\t\t" + String.join(" ", Integer.toString(param.index), "o", "p_" + pid, pid));
    }
}