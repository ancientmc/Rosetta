package com.ancientmc.rosetta.mapping.match.type;

import com.ancientmc.rosetta.jar.type.Type;

public sealed interface MatchType<J extends Type> permits MatchClass, MatchField, MatchMethod, MatchParameter {
    String getOldName();

    String getNewName();

    boolean matches(J type);
}
