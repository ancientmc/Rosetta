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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateFunction {
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
                MatchClass oldClass = match.getClass(cls);
                TsrgClass intermediateClass = tsrg.getIntermediateClass(oldClass);
                lines.add(String.join(" ", cls.name, intermediateClass.mapped, intermediateClass.id) + "\n");
            } else {
                String id = newClassIds.get(cls);
                lines.add(String.join(" ", cls.name, config.namespace + "c_" + id, id) + "\n");
            }

            List<Field> sortedFields = jar.fields.stream().filter(f -> f.parentName.equals(cls.name)).toList();
            for (Field field : sortedFields) {
                if (isMatchedField(field, match)) {
                    MatchField oldField = match.getField(field);
                    TsrgField intermediateField = tsrg.getIntermediateField(oldField);
                    lines.add("\t" + String.join(" ", field.name, intermediateField.mapped, intermediateField.id) + "\n");
                } else {
                    String id = newFieldIds.get(field);
                    lines.add("\t" + String.join(" ", field.name, "f_" + id, id) + "\n");
                }
            }

            List<Method> sortedMethods = jar.methods.stream().filter(m -> m.parentName.equals(cls.name)).toList();
            for (Method method : sortedMethods) {
                String id;
                if (isMatchedMethod(method, match)) {
                    MatchMethod oldMethod = match.getMethod(method);
                    TsrgMethod intermediateMethod = tsrg.getIntermediateMethod(oldMethod);
                    id = intermediateMethod.id;
                    lines.add("\t" + String.join(" ", method.name, method.desc, intermediateMethod.mapped, id) + "\n");
                } else {
                    if (method.inherited) {
                        Method superMethod = getSuperMethod(method);
                        if (isMatchedMethod(superMethod, match)) { // if the method's parent is old
                            MatchMethod oldSuperMethod = match.getMethod(superMethod);
                            id = tsrg.getIntermediateMethod(oldSuperMethod).id;
                        } else { // if the method's parent id is also new
                            id = newMethodIds.get(superMethod);
                        }
                    } else {
                        id = newMethodIds.get(method);
                    }
                    String mapped = method.name.contains("init>") ? method.name : "m_" + id;
                    lines.add("\t" + String.join(" ", method.name, method.desc, mapped, id) + "\n");

                }

                // Params
                if (!method.params.isEmpty()) {
                    for (int i = 0; i < method.params.size(); i++) {
                        Parameter param = method.params.get(i);
                        if (isMatchedMethod(param.method, match)) {
                            MatchMethod oldMethod = match.getMethod(method);
                            TsrgMethod intermediateMethod = tsrg.getIntermediateMethod(oldMethod);
                            if (!intermediateMethod.params.isEmpty()) {
                                addOldParam(lines, param, oldMethod, intermediateMethod);
                            } else {
                                addNewParam(lines, param, newParamIds);
                            }
                        } else {
                            if (param.method.inherited) {
                                Method superMethod = getSuperMethod(method);
                                Parameter superParam = superMethod.params.get(param.index);
                                if (isMatchedMethod(superMethod, match)) {
                                    MatchMethod oldSuperMethod = match.getMethod(superMethod);
                                    TsrgMethod superIntermediate = tsrg.getIntermediateMethod(oldSuperMethod);
                                    if (!superIntermediate.params.isEmpty()) {
                                        addOldParam(lines, param, oldSuperMethod, superIntermediate);
                                    } else {
                                        addNewParam(lines, param, newParamIds);
                                    }                                } else {
                                    addNewParam(lines, superParam, newParamIds);
                                }
                            } else {
                                addNewParam(lines, param, newParamIds);
                            }
                        }
                    }
                }
            }
        });

        return lines;
    }

    public static void write(File tsrg, List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tsrg))) {
            for (String line : lines) {
                writer.write(line);
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
            writer.write(String.join(",", "params", Integer.toString(lastParamCounter)));
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
            if ((!isMatchedMethod(param.method, match) && !param.method.inherited) || (isMatchedMethod(param.method, match)
                    && match.getMethod(param.method).params.stream().noneMatch(p -> p.newIndex == param.index)))  { // fix
                newParams.put(param, getFormattedId(counter));
                counter++;
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

    public Method getSuperMethod(Method method) {
        ClassType superParent = jar.getClass(method.superParentName);
        if (superParent != null) {
            return superParent.getMethod(jar, method.name, method.desc);
        }
        return null;
    }

    public static void addOldParam(List<String> lines, Parameter param, MatchMethod matchMethod, TsrgMethod tsrgMethod) {
        MatchParameter matchParam = matchMethod.getParameter(param.index);
        if (matchParam != null) {
            TsrgParameter tsrgParam = tsrgMethod.getParameter(matchParam.oldIndex);
            lines.add("\t\t" + String.join(" ", Integer.toString(param.index), "o", tsrgParam.name, tsrgParam.id) + "\n");
        }
    }

    public static void addNewParam(List<String> lines, Parameter param, Map<Parameter, String> newParamIds) {
        String pid = newParamIds.get(param);
        lines.add("\t\t" + String.join(" ", Integer.toString(param.index), "o", "p_" + pid, pid) + "\n");
    }
}
