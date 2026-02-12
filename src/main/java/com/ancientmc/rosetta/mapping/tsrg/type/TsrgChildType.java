package com.ancientmc.rosetta.mapping.tsrg.type;

public sealed interface TsrgChildType<T extends TsrgType> permits TsrgField, TsrgMethod, TsrgParameter {
    T getParent();
}
