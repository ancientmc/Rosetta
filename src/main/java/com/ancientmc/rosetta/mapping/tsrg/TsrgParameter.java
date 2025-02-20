package com.ancientmc.rosetta.mapping.tsrg;

public class TsrgParameter {
    public int index;
    public TsrgMethod parent;
    public String name;
    public String id;

    public TsrgParameter(int index, TsrgMethod parent, String name) {
        this.index = index;
        this.parent = parent;
        this.name = name;
    }

    public TsrgParameter setId(String name) {
        this.id = name.split("_")[1];
        return this;
    }

    public TsrgMethod getParent(Tsrg tsrg) {
        return tsrg.methods.stream().filter(m -> m.params.contains(this)).findAny().orElse(null);
    }
}
