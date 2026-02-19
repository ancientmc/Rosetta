package com.ancientmc.rosetta.mapping.match.type;

import com.ancientmc.rosetta.jar.type.Type;

public sealed interface MatchChildType<J extends Type, T extends MatchType<J>> permits MatchField, MatchMethod, MatchParameter {
    T getParent();

    String getOldParentName();

    String getNewParentName();
}
