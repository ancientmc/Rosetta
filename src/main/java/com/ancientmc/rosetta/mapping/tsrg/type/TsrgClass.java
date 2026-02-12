package com.ancientmc.rosetta.mapping.tsrg.type;

import java.util.List;

public final class TsrgClass implements TsrgType {
    private final String obf;
    private final String mapped;
    private final String id;
    private List<TsrgField> fields;
    private List<TsrgMethod> methods;

    public TsrgClass(String obf, String mapped, String id) {
        this.obf = obf;
        this.mapped = mapped;
        this.id = id;
    }

    public void setChildren(List<TsrgField> fields, List<TsrgMethod> methods) {
        this.fields = fields;
        this.methods = methods;
    }

    public List<TsrgField> getFields() {
        return fields;
    }

    public List<TsrgMethod> getMethods() {
        return methods;
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
        return "";
    }

    @Override
    public String toString() {
        return getIndent() + String.join(" ", obf, mapped, id) + "\n";
    }
}
