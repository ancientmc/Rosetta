package com.ancientmc.rosetta.mapping.match;

public class MatchField {
    public String oldParent;
    public String newParent;
    public String oldName;
    public String newName;

    public MatchField(String oldParent, String newParent, String oldName, String newName) {
        this.oldParent = oldParent;
        this.newParent = newParent;
        this.oldName = oldName;
        this.newName = newName;
    }
}
