package com.ancientmc.rosetta.jar.type;

public final class Parameter implements Type, ChildType<Method> {
    private final int index;
    private final Method parent;

    public Parameter(int index, Method parent) {
        this.index = index;
        this.parent = parent;
    }

    @Override
    public String getName() {
        return "arg" + index; // do this because param names don't really matter at the bytecode level.
    }

    @Override
    public Method getParent() {
        return parent;
    }

    @Override
    public String getParentName() {
        return parent.getName();
    }

    @Override
    public String getTypeSetId() {
        return getName();
    }
}
