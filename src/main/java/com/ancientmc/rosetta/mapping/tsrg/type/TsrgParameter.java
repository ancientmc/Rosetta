package com.ancientmc.rosetta.mapping.tsrg.type;

public final class TsrgParameter implements TsrgType, TsrgChildType<TsrgMethod> {
    private final int index;
    private final String name;
    private final TsrgMethod parent;
    private final String id;

    public TsrgParameter(int index, String name, TsrgMethod parent, String id) {
        this.index = index;
        this.name = name;
        this.parent = parent;
        this.id = id;
    }

    @Override
    public String getObf() {
        return "o";
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String getMapped() {
        return name;
    }

    @Override
    public TsrgMethod getParent() {
        return parent;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getIndent() {
        return "\t\t";
    }

    // \t\t# o name id\n
    @Override
    public String toString() {
        return getIndent() + String.join(" ", Integer.toString(index), getObf(), name, id);
    }
}
