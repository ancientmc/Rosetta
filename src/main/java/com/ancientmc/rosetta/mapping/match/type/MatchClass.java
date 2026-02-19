package com.ancientmc.rosetta.mapping.match.type;

import com.ancientmc.rosetta.jar.type.ClassType;

import java.util.List;

public final class MatchClass implements MatchType<ClassType> {
    private final String oldName;
    private final String newName;

    private List<MatchField> fields;
    private List<MatchMethod> methods;

    public MatchClass(String oldName, String newName) {
        this.oldName = oldName;
        this.newName = newName;
    }

    public void setChildren(List<MatchField> fields, List<MatchMethod> methods) {
        this.fields = fields.stream().filter(f -> f.getOldParentName().equals(oldName)).toList();
        this.methods = methods.stream().filter(m -> m.getOldParentName().equals(oldName)).toList();
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
    public boolean matches(ClassType type) {
        return type.getName().equals(newName);
    }

    public List<MatchField> getFields() {
        return fields;
    }

    public List<MatchMethod> getMethods() {
        return methods;
    }
}
