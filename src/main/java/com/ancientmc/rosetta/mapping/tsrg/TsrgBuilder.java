package com.ancientmc.rosetta.mapping.tsrg;

import com.ancientmc.rosetta.mapping.tsrg.type.*;

import java.io.File;
import java.util.List;

public class TsrgBuilder {
    public File file;
    public List<Tsrg.Line<? extends TsrgType>> lines;
    public List<TsrgClass> classes;
    public List<TsrgField> fields;
    public List<TsrgMethod> methods;
    public List<TsrgParameter> params;

    public TsrgBuilder file(File file) {
        this.file = file;
        return this;
    }

    public TsrgBuilder lines(List<Tsrg.Line<? extends TsrgType>> lines) {
        this.lines = lines;
        return this;
    }

    public TsrgBuilder members(List<TsrgClass> classes, List<TsrgField> fields, List<TsrgMethod> methods, List<TsrgParameter> params) {
        this.classes = classes;
        this.fields = fields;
        this.methods = methods;
        this.params = params;
        return this;
    }

    public Tsrg build() {
        return new Tsrg(this);
    }
}
