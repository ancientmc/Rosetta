package com.ancientmc.rosetta.jar.type;

import com.ancientmc.rosetta.jar.Jar;

import java.util.List;

public record ClassType(String name, String parentName) {
    public List<Field> getFields(Jar jar) {
        return jar.fields.stream().filter(f -> f.parentName().equals(this.name)).toList();
    }

    public List<Method> getMethods(Jar jar) {
        return jar.methods.stream().filter(m -> m.parentName().equals(this.name)).toList();
    }

    public Method getMethod(Jar jar, String name, String desc) {
        return getMethods(jar).stream().filter(m -> m.name().equals(name) && m.desc().equals(desc)).findAny().orElse(null);
    }
}
