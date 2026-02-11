package com.ancientmc.rosetta.mapping.tsrg.type;

public final class TsrgMethod implements TsrgType {
    private final String obf;
    private final String desc;
    private final String mapped;
    private final String id;

    public TsrgMethod(String obf, String desc, String mapped, String id) {
        this.obf = obf;
        this.desc = desc;
        this.mapped = mapped;
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
    public String getId() {
        return id;
    }

    @Override
    public String getIndent() {
        return "\t";
    }

    @Override
    public String toLine() {
        return getIndent() + " " + obf + " " + desc + " " + mapped + " " + id;
    }
}
