package com.ancientmc.rosetta.mapping.match;

public class MatchParameter {
    public MatchMethod parent;
    public int oldIndex;
    public int newIndex;

    public MatchParameter(MatchMethod parent, int oldIndex, int newIndex) {
        this.parent = parent;
        this.oldIndex = oldIndex;
        this.newIndex = newIndex;
    }
}
