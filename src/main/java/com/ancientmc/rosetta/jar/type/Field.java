package com.ancientmc.rosetta.jar.type;

public final class Field implements Type, ChildType<ClassType> {
    private final String name;
    private final ClassType parent;
    private final String desc;

    public Field(String name, ClassType parent, String desc) {
        this.name = name;
        this.parent = parent;
        this.desc = desc;
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

    public String getDesc() {
        return desc;
    }
}
