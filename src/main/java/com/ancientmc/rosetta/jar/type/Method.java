package com.ancientmc.rosetta.jar.type;

import com.ancientmc.rosetta.jar.Jar;

import java.util.ArrayList;
import java.util.List;

public record Method(String name, String parentName, String desc, String superParentName, boolean inherited, int count) {
    public List<Parameter> getParams() {
        List<Parameter> params = new ArrayList<>();

        if (count > 0) {
            for (int i = 0; i < count; i++) {
                params.add(new Parameter(i, this));
            }
        }

        return params;
    }

    public Method getSuperMethod(Jar jar) {
        ClassType superParent = jar.getClass(superParentName);

        if (superParent != null) {
            return superParent.getMethod(jar, name, desc);
        }

        return null;
    }
}
