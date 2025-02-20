package com.ancientmc.rosetta.jar.type;

import java.util.ArrayList;
import java.util.List;

public class Method extends InnerType {
    public String superParentName;
    public boolean inherited;
    public List<Parameter> params = new ArrayList<>();

    public Method(String name, String parentName, String desc, String superParentName, boolean inherited) {
        super(name, parentName, desc);
        this.superParentName = superParentName;
        this.inherited = inherited;
    }

    public Method setParameters(int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                params.add(new Parameter(i, this));
            }
        }
        return this;
    }
}
