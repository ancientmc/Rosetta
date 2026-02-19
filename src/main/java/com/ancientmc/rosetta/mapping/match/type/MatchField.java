package com.ancientmc.rosetta.mapping.match.type;

import com.ancientmc.rosetta.jar.type.Field;

public final class MatchField implements MatchType<Field>, MatchChildType<MatchClass> {
    private final String oldName;
    private final String newName;
    private final MatchClass parent;

    public MatchField(String oldName, String newName, MatchClass parent) {
        this.oldName = oldName;
        this.newName = newName;
        this.parent = parent;
    }

    @Override
    public String getOldName() {
        return oldName;
    }


    @Override
    public String getNewName() {
        return newName;
    }

    @Override
    public MatchClass getParent() {
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

    @Override
    public boolean matches(Field type) {
        return type.getName().equals(newName)
                && type.getParentName().equals(getNewParentName());
    }
}
