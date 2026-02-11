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
    private final List<Parameter> params = new ArrayList<>();

    public Method(String name, ClassType parent, String superParentName, String desc, InheritanceStatus inheritanceStatus, int argCount) {
        this.name = name;
        this.parent = parent;
        this.superParentName = superParentName;
        this.desc = desc;
        this.inheritanceStatus = inheritanceStatus;
        this.argCount = argCount;
        this.setParams();
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
        return String.join(".", parent.getTypeSetId(), name, desc);
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
        return argCount > 0;
    }

    public void setParams() {
        if (argCount > 0) {
            for (int i = 0; i < argCount; i++) {
                Parameter param = new Parameter(i, this);
                params.add(param);
            }
        }
    }

    public List<Parameter> getParams() {
        return params;
    }

    public Parameter getParam(int index) {
        return getParams().get(index);
    }

    /** Represents the method's inheritance source, or if it's even inherited at all. */
    public enum InheritanceStatus {
        NONE, // no inheritance
        CLASSPATH, // JDK or Minecraft dependency (LWJGL, Paulscode, etc.)
        JAR // another Minecraft class
    }
}
