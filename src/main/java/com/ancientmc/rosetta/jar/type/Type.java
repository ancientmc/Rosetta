package com.ancientmc.rosetta.jar.type;

/**
 * Representation of a Java data type. All types have a name, the name of the parent, and an identifier. For classes, the parent name
 * belongs to the super class. For methods and fields, the parent is the holder class. For parameters, it's the method name.
 * @author moist-mason
 */
public sealed interface Type permits ClassType, Field, Method, Parameter {
    String getName();

    String getParentName();

    /** @return Unique identifier used for retrieving this type */
    String getTypeSetId();
}
