package com.ancientmc.rosetta.mapping;

import com.ancientmc.rosetta.jar.type.Type;
import com.ancientmc.rosetta.jar.type.TypeSet;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Every data type in the TSRG file has a unique five-digit ID. This class creates a set that stores those IDs.
 * @author moist-mason
 * @param <T> The type associated with this set.
 */
public class IdSet<T extends Type> {
    public final Map<T, String> map;
    public final int counter;

    public IdSet(Map<T, String> map, int counter) {
        this.map = map;
        this.counter = counter;
    }

    /**
     * Used for TSRG generation.
     * @param set A type set.
     * @return A fresh set of IDs for the input type set.
     * @param <T> The given data type.
     */
    public static <T extends Type> IdSet<T> createFresh(TypeSet<T> set) {
        Map<T, String> map = new LinkedHashMap<>();

        for (T type : set) {
            int i = set.indexOf(type);
            String id = getFormattedId(i + 1);
            map.put(type, id);
        }

        return new IdSet<>(map, map.size());
    }

    /**
     * Used for TSRG updating.
     * @param set A type set.
     * @param counter The original counter.
     * @return A set of IDs to be added to the updated TSRG.
     * @param <T> The given data type.
     */
    public static <T extends Type> IdSet<T> createNew(TypeSet<T> set, int counter) {
        Map<T, String> map = new LinkedHashMap<>();

        for (T type : set) {
            counter++;
            map.put(type, getFormattedId(counter));
        }

        return new IdSet<>(map, counter);
    }

    public String get(T type) {
        return map.get(type);
    }

    public static String getFormattedId(int id) {
        return new DecimalFormat("00000").format(id);
    }
}
