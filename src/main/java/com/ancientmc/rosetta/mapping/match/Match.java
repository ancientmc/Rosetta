package com.ancientmc.rosetta.mapping.match;

import com.ancientmc.rosetta.jar.type.Type;
import com.ancientmc.rosetta.mapping.match.type.*;

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


    /**
     * @return the Match type based on the given list and java type.
     */
    public <J extends Type, M extends MatchType<J>> M getType(List<M> matchTypes, J type) {
        return matchTypes.stream().filter(t -> t.matches(type)).findAny().orElseThrow();
    }

    /**
     * @return {@code true} if the Java type is found within the given list of match types.
     */
    public <J extends Type, M extends MatchType<J>> boolean isMatched(List<M> matchTypes, J type) {
        return matchTypes.stream().anyMatch(m -> m.matches(type));
    }
}
