package com.ancientmc.rosetta.jar;

import com.ancientmc.rosetta.jar.type.*;

/**
 * Representation of a Java JAR file. More specifically, this is a representation of the Minecraft JAR file.
 * @author moist-mason
 */
public final class Jar {

    // global type sets, representing all members of a given type present within the JAR.
    private final TypeSet<ClassType> classes;
    private final TypeSet<Field> fields;
    private final TypeSet<Method> methods;
    private final TypeSet<Parameter> params;

    public Jar(JarBuilder builder) {
        this.classes = builder.classes;
        this.fields = builder.fields;
        this.methods = builder.methods;
        this.params = builder.params;
    }

    public TypeSet<ClassType> getClasses() {
        return classes;
    }

    public TypeSet<Field> getFields() {
        return fields;
    }

    public TypeSet<Method> getMethods() {
        return methods;
    }

    public TypeSet<Parameter> getParams() {
        return params;
    }

    public ClassType getClass(String id) {
        return classes.get(id);
    }

    public Method getSuperMethod(Method child) {
        ClassType superParent = getClass(child.getSuperParentName());
        if (superParent == null) return null;

        return superParent.getMethod(child.getName(), child.getDesc());
    }
}
