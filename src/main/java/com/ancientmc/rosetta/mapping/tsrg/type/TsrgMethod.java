package com.ancientmc.rosetta.mapping.tsrg.type;

public final class TsrgMethod implements TsrgType, TsrgChildType<TsrgClass> {
    private final String obf;
    private final String desc;
    private final String mapped;
    private final TsrgClass parent;
    private final String id;

    public TsrgMethod(String obf, String desc, String mapped, TsrgClass parent, String id) {
        this.obf = obf;
        this.desc = desc;
        this.mapped = mapped;
        this.parent = parent;
        this.id = id;
    }

    @Override
    public String getObf() {
        return obf;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String getMapped() {
        return mapped;
    }

    @Override
    public TsrgClass getParent() {
        return parent;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getIndent() {
        return "\t";
    }

    @Override
    public String toString() {
        return getIndent() + String.join(" ", obf, desc, mapped, id) + "\n";
    }
}
