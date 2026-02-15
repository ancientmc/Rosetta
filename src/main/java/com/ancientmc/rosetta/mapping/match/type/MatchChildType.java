package com.ancientmc.rosetta.mapping.match.type;

public interface MatchChildType<T extends MatchType> {
    T getParent();

    String getOldParentName();

    String getNewParentName();
}
