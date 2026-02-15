package com.ancientmc.rosetta.mapping.match;

import com.ancientmc.rosetta.mapping.match.type.MatchClass;
import com.ancientmc.rosetta.mapping.match.type.MatchField;
import com.ancientmc.rosetta.mapping.match.type.MatchMethod;
import com.ancientmc.rosetta.mapping.match.type.MatchParameter;

import java.util.List;

public class Match {
    private final List<MatchClass> classes;
    private final List<MatchField> fields;
    private final List<MatchMethod> methods;
    private final List<MatchParameter> params;

    public Match(MatchReader reader) {
        this.classes = reader.classes;
        this.fields = reader.fields;
        this.methods = reader.methods;
        this.params = reader.params;
    }

    public List<MatchClass> getClasses() {
        return classes;
    }

    public List<MatchField> getFields() {
        return fields;
    }

    public List<MatchMethod> getMethods() {
        return methods;
    }

    public List<MatchParameter> getParams() {
        return params;
    }

    public void successTest() {
        System.out.println("made successfully");
    }
}
