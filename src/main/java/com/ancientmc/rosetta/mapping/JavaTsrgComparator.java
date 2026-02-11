package com.ancientmc.rosetta.mapping;

import com.ancientmc.rosetta.jar.type.Type;
import com.ancientmc.rosetta.mapping.tsrg.type.TsrgType;

public interface JavaTsrgComparator<J extends Type, T extends TsrgType> {
    boolean compare(J java, T tsrg);
}
