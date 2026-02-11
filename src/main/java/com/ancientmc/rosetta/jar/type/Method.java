package com.ancientmc.rosetta.jar.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Method implements Type, ChildType<ClassType> {
    private final String name;
    private final ClassType parent;
    private final String superParentName;
    private final String desc;
    private final InheritanceStatus inheritanceStatus;
    private final int argCount;

    public Method(String name, ClassType parent, String superParentName, String desc, InheritanceStatus inheritanceStatus, int argCount) {
        this.name = name;
        this.parent = parent;
        this.superParentName = superParentName;
        this.desc = desc;
        this.inheritanceStatus = inheritanceStatus;
        this.argCount = argCount;
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
        return name + " " + desc;
    }

    public String getSuperParentName() {
        return superParentName;
    }

    public String getDesc() {
        return desc;
    }

    public InheritanceStatus getInheritanceStatus() {
        return inheritanceStatus;
    }

    public boolean isInherited() {
        return !inheritanceStatus.equals(InheritanceStatus.NONE);
    }

    public boolean hasParams() {
        return !getParams().isEmpty();
    }

    public List<Parameter> getParams() {
        List<Parameter> params = new ArrayList<>();

        if (argCount == 0) { // no params
            return Collections.emptyList();
        }

        for (int i = 0; i < argCount; i++) {
            Parameter param = new Parameter(i, this);
            params.add(param);
        }

        return params;
    }

    /** Represents the method's inheritance source, or if it's even inherited at all. */
    public enum InheritanceStatus {
        NONE, // no inheritance
        CLASSPATH, // JDK or Minecraft dependency (LWJGL, Paulscode, etc.)
        JAR // another Minecraft class
    }
}
