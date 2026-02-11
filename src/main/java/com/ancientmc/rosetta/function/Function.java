package com.ancientmc.rosetta.function;

import com.ancientmc.rosetta.mapping.tsrg.Tsrg;

import java.io.IOException;

public abstract class Function {
    protected void exec() throws IOException {
        Tsrg tsrg = buildTsrg();
        tsrg.write();
    }

    public abstract Tsrg buildTsrg();
}
