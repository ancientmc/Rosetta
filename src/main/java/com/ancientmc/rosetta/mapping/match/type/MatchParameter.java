package com.ancientmc.rosetta.mapping.match.type;

import com.ancientmc.rosetta.jar.type.Parameter;

public final class MatchParameter implements MatchType<Parameter>, MatchChildType<MatchMethod> {
    private final int oldIndex;
    private final int newIndex;
    private final MatchMethod parent;

    public MatchParameter(int oldIndex, int newIndex, MatchMethod parent) {
        this.oldIndex = oldIndex;
        this.newIndex = newIndex;
        this.parent = parent;
    }

    public int getOldIndex() {
        return oldIndex;
    }

    public int getNewIndex() {
        return newIndex;
    }

    @Override
    public String getOldName() { // use getOldIndex() not this
        return Integer.toString(oldIndex);
    }

    @Override
    public String getNewName() { // use getNewIndex() not this
        return Integer.toString(newIndex);
    }

    @Override
    public boolean matches(Parameter type) {
        return true; // unused
    }

    @Override
    public MatchMethod getParent() {
        return parent;
    }

    @Override
    public String getOldParentName() {
        return parent.getOldName();
    }

    @Override
    public String getNewParentName() {
        return parent.getNewName();
    }
}
