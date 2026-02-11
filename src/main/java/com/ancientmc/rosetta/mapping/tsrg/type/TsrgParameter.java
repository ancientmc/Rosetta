package com.ancientmc.rosetta.mapping.tsrg.type;

public final class TsrgParameter implements TsrgType {
    private final int index;
    private final String name;
    private final String id;

    public TsrgParameter(int index, String name, String id) {
        this.index = index;
        this.name = name;
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
    public String getId() {
        return id;
    }


    @Override
    public String getIndent() {
        return "\t\t";
    }


    // \t\t# o name id\n
    @Override
    public String toLine() {
        return getIndent() + String.join(" ", Integer.toString(index), getObf(), name) + "\n";
    }
}
