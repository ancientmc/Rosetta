package com.ancientmc.rosetta.mapping.match.type;

import java.util.List;

public class MatchClass implements MatchType {
    private final String oldName;
    private final String newName;

    private List<MatchField> fields;
    private List<MatchMethod> methods;

    public MatchClass(String oldName, String newName) {
        this.oldName = oldName;
        this.newName = newName;
    }

    public void setChildren(List<MatchField> fields, List<MatchMethod> methods) {
        this.fields = fields;
        this.methods = methods;
    }

    public static MatchClass dummy() {
        return new MatchClass("", "");
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
    public String toString() {
        return "CLASS: oldName=" + oldName + " newName=" + newName;
    }

    public List<MatchField> getFields() {
        return fields;
    }

    public List<MatchMethod> getMethods() {
        return methods;
    }
}
