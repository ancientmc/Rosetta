package com.ancientmc.rosetta;

import com.ancientmc.rosetta.util.Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON file for configuring Rosetta.
 * @author moist-mason
 */
public class Config {

    /** List of packages that determine which classes are excluded from parsing. */
    public final List<String> excluded;

    /** List of classes that are excluded from being given intermediate class names. */
    public final List<String> unobfuscated;

    /** The package namespace that intermediate classes are put into. */
    public final String namespace;

    /** The maximum character length for a method or field to be recognized as obfuscated. */
    public final int maxObfChars;

    public Config(File configFile) {
        JsonObject config = Util.getJson(configFile);
        excluded = getArray(config, "excluded");
        unobfuscated = getArray(config, "unobfuscated");
        namespace = config.get("namespace").getAsString();
        maxObfChars = config.get("max_obf_chars").getAsInt();
    }

    private List<String> getArray(JsonObject config, String name) {
        List<String> list = new ArrayList<>();
        JsonArray array = config.getAsJsonArray(name);
        array.forEach(e -> list.add(e.getAsString()));
        return list;
    }

    /**
     * @return {@code true} if the input data is an excluded element.
     */
    public boolean isExcluded(String data) {
        for (String e : excluded) {
            return e.contains(data);
        }

        return false;
    }


    /**
     * @return {@code true} if the input data is a premapped element.
     */
    public boolean isUnobfuscated(String data) {
        return unobfuscated.contains(data);
    }
}
