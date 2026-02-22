package com.ancientmc.rosetta.jar.type;

public final class Field implements Type, ChildType<ClassType> {
    private final String name;
    private final ClassType parent;

    public Field(String name, ClassType parent) {
        this.name = name;
        this.parent = parent;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ClassType getParent() {
        return parent;
    }

    @Override
    public String getParentName() {
        return parent.getName();
    }

    @Override
    public String getTypeSetId() {
        return String.join(".", parent.getTypeSetId(), name);
    }
}
