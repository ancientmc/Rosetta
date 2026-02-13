package com.ancientmc.rosetta.jar.type;

/**
 * Representation of a Java data type with a parent. Class types as defined by Rosetta have parent names, but do not have
 * parent *objects*, because Rosetta does not read JAR dependencies when parsing a JAR file.
 * @author moist-mason
 * @param <T> the type of the parent of this child.
 */
public sealed interface ChildType<T extends Type> permits Field, Method, Parameter {
    T getParent();
}
