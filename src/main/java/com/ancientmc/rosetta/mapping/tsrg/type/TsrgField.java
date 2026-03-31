package com.ancientmc.rosetta.mapping.tsrg.type;

public final class TsrgField implements TsrgType, TsrgChildType<TsrgClass> {
    private final String obf;
    private final String mapped;
    private final TsrgClass parent;
    private final String id;

    public TsrgField(String obf, String mapped, TsrgClass parent, String id) {
        this.obf = obf;
        this.mapped = mapped;
        this.parent = parent;
        this.id = id;
    }

    @Override
    public String getObf() {
        return obf;
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
        return getIndent() + String.join(" ", obf, mapped, id);
    }
}
