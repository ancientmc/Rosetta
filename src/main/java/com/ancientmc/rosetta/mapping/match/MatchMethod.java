package com.ancientmc.rosetta.mapping.match;

import com.ancientmc.rosetta.util.Util;

import java.util.ArrayList;
import java.util.List;

public class MatchMethod {
    public String oldParent;
    public String newParent;
    public String oldName;
    public String newName;
    public String oldDesc;
    public String newDesc;
    public List<MatchParameter> params = new ArrayList<>();

    public MatchMethod(String oldParent, String newParent, String oldName, String newName, String oldDesc, String newDesc) {
        this.oldParent = oldParent;
        this.newParent = newParent;
        this.oldName = oldName;
        this.newName = newName;
        this.oldDesc = oldDesc;
        this.newDesc = newDesc;
    }

    public MatchMethod setParams(List<String> classBlock, String methodLine) {
        List<String> methodBlock = classBlock.subList(classBlock.indexOf(methodLine), Util.getNextMatchMethod(classBlock, methodLine)); // get the lines of the method and its params

        if (!methodBlock.isEmpty()) { // empty -> just the method line -> no params
            for (String pLine : methodBlock) {
                if (pLine.startsWith("\t\tma")) {
                    String[] split = pLine.split("\t");
                    int oldIndex = Integer.parseInt(split[3]);
                    int newIndex = Integer.parseInt(split[4]);
                    this.params.add(new MatchParameter(this, oldIndex, newIndex));
                }
            }
        }
        return this;
    }

    public MatchParameter getParameter(int index) {
        return params.stream().filter(p -> p.newIndex == index).findAny().orElse(null);
    }
}
