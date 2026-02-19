package com.ancientmc.rosetta.mapping.tsrg.type;

public sealed interface TsrgType permits TsrgClass, TsrgField, TsrgMethod, TsrgParameter {
    String getObf();

    String getMapped();

    String getId();

    String getIndent();
}
