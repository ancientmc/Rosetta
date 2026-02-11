package com.ancientmc.rosetta.jar.type;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * <p>
 * A type set represents a collection of a given Java data type. It hinges on an internal map, where each key is an identifier string for a type object, and
 * value is the object itself. This class is primarily to ensure cleaner code when referencing type objects.
 * </p>
 * @author moist-mason
 * @param <T> the type for this set.
 */
public class TypeSet<T extends Type> implements Iterable<T> {
    private final Map<String, T> map = new HashMap<>();

    public void add(T type) {
        map.put(type.getTypeSetId(), type);
    }

    public void addAll(List<T> list) {
        list.forEach(this::add);
    }

    private List<T> list() {
        return map.values().stream().toList();
    }

    @Override
    public Iterator<T> iterator() {
        return list().iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Iterable.super.forEach(action);
    }

    /**
     * Filters the type set based on the given predicate.
     * @param predicate boolean predicate.
     * @return The filtered set.
     */
    public TypeSet<T> filtered(Predicate<T> predicate) {
        List<T> filteredList = list().stream().filter(predicate).toList();
        TypeSet<T> newSet = new TypeSet<>();
        newSet.addAll(filteredList);
        return newSet;
    }

    public T get(String id) {
        return map.get(id);
    }

    public int indexOf(T type) {
        return list().indexOf(type);
    }

    public int size() {
        return list().size();
    }

    public boolean isEmpty() {
        return list().isEmpty();
    }
}
