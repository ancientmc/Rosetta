package com.ancientmc.rosetta.jar.type;

/**
 * Java class type.
 */
public final class ClassType implements Type {
    private final String name;
    private final String parentName;

    private TypeSet<Field> fields;
    private TypeSet<Method> methods;

    public ClassType(String name, String parentName) {
        this.name = name;
        this.parentName = parentName;
    }

    public void setChildren(TypeSet<Field> fields, TypeSet<Method> methods) {
        this.fields = fields;
        this.methods = methods;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getParentName() {
        return parentName;
    }

    @Override
    public String getTypeSetId() {
        return name;
    }

    public TypeSet<Field> getFields() {
        return fields;
    }

    public TypeSet<Method> getMethods() {
        return methods;
    }

    public Field getField(String name) {
        return fields.get(name);
    }

    public Method getMethod(String name, String desc) {
        String id = String.join(".", getTypeSetId(), name, desc);
        return methods.get(id);
    }
}
