package com.ancientmc.rosetta.mapping.match.type;

public class MatchMethod implements MatchType, MatchChildType<MatchClass> {
    private final String oldName;
    private final String newName;
    private final String oldDesc;
    private final String newDesc;
    private final MatchClass parent;

    public MatchMethod(String oldName, String newName, String oldDesc, String newDesc, MatchClass parent) {
        this.oldName = oldName;
        this.newName = newName;
        this.oldDesc = oldDesc;
        this.newDesc = newDesc;
        this.parent = parent;
    }

    public static MatchMethod dummy() {
        return new MatchMethod("", "", "", "", MatchClass.dummy());
    }

    @Override
    public String getOldName() {
        return oldName;
    }


    @Override
    public String getNewName() {
        return newName;
    }

    public String getOldDesc() {
        return oldDesc;
    }

    public String getNewDesc() {
        return newDesc;
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
    public String toString() {
        return "METHOD: oldName=" + oldName + " oldDesc=" + oldDesc + " newName=" + newName + " newDesc=" + newDesc;
    }
}
