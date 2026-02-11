package com.ancientmc.rosetta.mapping.tsrg.type;

public final class TsrgField implements TsrgType {
    private final String obf;
    private final String mapped;
    private final String id;

    public TsrgField(String obf, String mapped, String id) {
        this.obf = obf;
        this.mapped = mapped;
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
    public String getId() {
        return id;
    }

    @Override
    public String getIndent() {
        return "\t";
    }

    @Override
    public String toLine() {
        return getIndent() + String.join(" ", obf, mapped, id) + "\n";
    }
}
