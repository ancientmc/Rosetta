package com.ancientmc.rosetta.mapping.match.type;

import com.ancientmc.rosetta.jar.type.Type;

public sealed interface MatchChildType<J extends Type, M extends MatchType<J>> permits MatchField, MatchMethod, MatchParameter {
    M getParent();

    String getOldParentName();

    String getNewParentName();
}
