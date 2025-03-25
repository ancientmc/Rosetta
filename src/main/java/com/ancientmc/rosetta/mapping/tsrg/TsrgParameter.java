package com.ancientmc.rosetta.mapping.tsrg;

import java.io.File;

public record TsrgParameter(int index, TsrgMethod parent, String name) {
    public String getId() {
        return name.split("_")[1];
    }
}
