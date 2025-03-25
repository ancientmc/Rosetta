package com.ancientmc.rosetta.jar;

import com.ancientmc.rosetta.jar.type.*;
import com.ancientmc.rosetta.mapping.match.Match;
import com.ancientmc.rosetta.mapping.match.MatchMethod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Ids {
    public static int classCounter;
    public static int fieldCounter;
    public static int methodCounter;
    public static int paramCounter;

    public static Map<ClassType, String> getClassIds(List<ClassType> classes) {
        Map<ClassType, String> ids = new HashMap<>();

        for (int i = 0; i < classes.size(); i++) {
            ClassType entry = classes.get(i);

            String id = getFormattedId(i + 1);
            ids.put(entry, id);
        }

        classCounter = ids.size();
        return ids;
    }

    public static Map<Field, String> getFieldIds(List<Field> fields) {
        Map<Field, String> ids = new HashMap<>();

        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            String id = getFormattedId(i + 1);
            ids.put(field, id);
        }

        fieldCounter = ids.size();
        return ids;
    }

    public static Map<Method, String> getMethodIds(List<Method> methods) {
        Map<Method, String> ids = new HashMap<>();
        List<Method> sortedMethods = methods.stream().filter(m -> !m.inherited()).toList();

        for (int i = 0; i < sortedMethods.size(); i++) {
            Method method = sortedMethods.get(i);
            String id = getFormattedId(i + 1);
            ids.put(method, id);
        }

        methodCounter = ids.size();
        return ids;
    }

    public static Map<Parameter, String> getParamIds(List<Parameter> params) {
        Map<Parameter, String> ids = new HashMap<>();
        List<Parameter> sortedParams = params.stream().filter(p -> !p.method().inherited()).toList();

        for (int i = 0; i < sortedParams.size(); i++) {
            Parameter param = sortedParams.get(i);
            String id = getFormattedId(i + 1);
            ids.put(param, id);
        }

        paramCounter = ids.size();
        return ids;
    }

    public static Map<ClassType, String> getNewClassIds(List<ClassType> classes, Match match, File ids) {
        int counter = getCount(ids, "classes") + 1;
        Map<ClassType, String> newClasses = new HashMap<>();

        for (ClassType cls : classes) {
            if (!match.isMatchedClass(cls)) {
                newClasses.put(cls, getFormattedId(counter));
                counter++;
            }
        }

        classCounter = counter;
        return newClasses;
    }

    public static Map<Field, String> getNewFieldIds(List<Field> fields, Match match, File ids) {
        int counter = getCount(ids, "fields") + 1;
        Map<Field, String> newField = new HashMap<>();

        for (Field field : fields) {
            if (!match.isMatchedField(field)) {
                newField.put(field, getFormattedId(counter));
                counter++;
            }
        }

        fieldCounter = counter;
        return newField;
    }

    public static Map<Method, String> getNewMethodIds(List<Method> methods, Match match, File ids) {
        int counter = getCount(ids, "methods") + 1;
        Map<Method, String> newMethods = new HashMap<>();

        for (Method method : methods) {
            if (!match.isMatchedMethod(method) && !method.inherited()) {
                newMethods.put(method, getFormattedId(counter));
                counter++;
            }
        }

        methodCounter = counter;
        return newMethods;
    }

    public static Map<Parameter, String> getNewParameterIds(List<Parameter> params, Match match, File ids) {
        int counter = getCount(ids, "params") + 1;
        Map<Parameter, String> newParams = new HashMap<>();

        // 1) param's method is new
        // 2) param's method is old but the param is new
        for (Parameter param : params) {
            if (!param.method().inherited()) {
                if (!match.isMatchedMethod(param.method()))  {
                    newParams.put(param, getFormattedId(counter));
                    counter++;
                } else {
                    MatchMethod matchMethod = match.getMethod(param.method());
                    if (!matchMethod.getParams().isEmpty()) {
                        if (matchMethod.getParams().stream().noneMatch(mp -> mp.newIndex() == param.index())) {
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

        paramCounter = counter;
        return newParams;
    }

    public static int getCount(File ids, String type) {
        try {
            String line = Files.readAllLines(ids.toPath()).stream().filter(l -> l.startsWith(type)).findAny().orElse(null);

            if (line != null) {
                String[] split = line.split(",");
                return Integer.parseInt(split[1]);
            }

            return -1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFormattedId(int id) {
        return new DecimalFormat("00000").format(id);
    }

    /**
     * @return the type names and their associated counter as a hash map.
     */
    public static Map<String, Integer> getIdCounters() {
        Map<String, Integer> counters = new LinkedHashMap<>();
        counters.put("classes", classCounter);
        counters.put("fields", fieldCounter);
        counters.put("methods", methodCounter);
        counters.put("params", paramCounter);

        return counters;
    }
}
