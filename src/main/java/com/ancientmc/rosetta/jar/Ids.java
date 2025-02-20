package com.ancientmc.rosetta.jar;

import com.ancientmc.rosetta.jar.type.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ids {
    public static Map<ClassType, String> getClassIds(List<ClassType> classes) {
        Map<ClassType, String> ids = new HashMap<>();
        for (int i = 0; i < classes.size(); i++) {
            ClassType entry = classes.get(i);

            NumberFormat format = new DecimalFormat("00000");
            String id = format.format(i + 1);
            ids.put(entry, id);
        }
        return ids;
    }

    public static Map<Field, String> getFieldIds(List<Field> fields) {
        Map<Field, String> ids = new HashMap<>();
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            NumberFormat format = new DecimalFormat("00000");
            String id = format.format(i + 1);
            ids.put(field, id);
        }
        return ids;
    }

    public static Map<Method, String> getMethodIds(List<Method> methods) {
        Map<Method, String> ids = new HashMap<>();

        List<Method> sortedMethods = methods.stream().filter(m -> !m.inherited).toList();
        for (int i = 0; i < sortedMethods.size(); i++) {
            Method method = sortedMethods.get(i);
            NumberFormat format = new DecimalFormat("00000");
            String id = format.format(i + 1);
            ids.put(method, id);
        }
        return ids;
    }

    public static Map<Parameter, String> getParamIds(List<Parameter> params) {
        Map<Parameter, String> ids = new HashMap<>();

        List<Parameter> sortedParams = params.stream().filter(p -> !p.method.inherited).toList();
        for (int i = 0; i < sortedParams.size(); i++) {
            Parameter param = sortedParams.get(i);
            NumberFormat format = new DecimalFormat("00000");
            String id = format.format(i + 1);
            ids.put(param, id);
        }

        return ids;
    }
}
