package com.ancientmc.rosetta.mapping.match.type;

import com.ancientmc.rosetta.jar.type.Method;

import java.util.List;

public final class MatchMethod implements MatchType<Method>, MatchChildType<MatchClass> {
    private final String oldName;
    private final String newName;
    private final String oldDesc;
    private final String newDesc;
    private final MatchClass parent;
    private List<MatchParameter> params;

    public MatchMethod(String oldName, String newName, String oldDesc, String newDesc, MatchClass parent) {
        this.oldName = oldName;
        this.newName = newName;
        this.oldDesc = oldDesc;
        this.newDesc = newDesc;
        this.parent = parent;
    }

    public void setParams(List<MatchParameter> params) {
        this.params = params.stream().filter(p -> p.getOldParentName().equals(oldName) && p.getNewParentName().equals(newName)
                && p.getParent().getOldDesc().equals(oldDesc) && p.getParent().getNewDesc().equals(newDesc)
                && p.getParent().getOldParentName().equals(parent.getOldName()) && p.getParent().getNewParentName().equals(parent.getOldName())).toList();
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

    public List<MatchParameter> getParams() {
        return params;
    }

    public MatchParameter getParam(int index) {
        return params.stream().filter(p -> p.getNewIndex() == index).findAny().orElse(null);
    }

    @Override
    public boolean matches(Method type) {
        return type.getName().equals(newName)
                && type.getDesc().equals(newDesc)
                && type.getParentName().equals(getNewParentName());
    }
}
