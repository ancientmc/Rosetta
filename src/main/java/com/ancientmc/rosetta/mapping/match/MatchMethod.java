package com.ancientmc.rosetta.mapping.match;

import com.ancientmc.rosetta.jar.type.Parameter;
import com.ancientmc.rosetta.util.Util;

import java.util.ArrayList;
import java.util.List;

public record MatchMethod(String oldParent, String newParent, String oldName, String newName, String oldDesc, String newDesc, List<String> classBlock, String methodLine) {

    public List<MatchParameter> getParams() {
        List<MatchParameter> params = new ArrayList<>();
        List<String> methodBlock = classBlock.subList(classBlock.indexOf(methodLine), Util.getNextMatchMethod(classBlock, methodLine)); // get the lines of the method and its params

        if (!methodBlock.isEmpty()) { // empty -> just the method line -> no params
            for (String pLine : methodBlock) {
                if (pLine.startsWith("\t\tma")) {
                    String[] split = pLine.split("\t");
                    int oldIndex = Integer.parseInt(split[3]);
                    int newIndex = Integer.parseInt(split[4]);
                    params.add(new MatchParameter(this, oldIndex, newIndex));
                }
            }
        }

        return params;
    }

    public MatchParameter getParameter(int index) {
        return getParams().stream().filter(p -> p.newIndex() == index).findAny().orElse(null);
    }
}
