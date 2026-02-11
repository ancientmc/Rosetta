package com.ancientmc.rosetta.jar;

import com.ancientmc.rosetta.Config;
import com.ancientmc.rosetta.jar.type.*;
import com.ancientmc.rosetta.jar.type.Method.InheritanceStatus;
import com.google.gson.JsonObject;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Helper class that parses through a JAR file and builds a JAR object.
 * @author moist-mason
 */
public class JarBuilder {
    private final File jarFile;
    private final JsonObject inheritance;
    private final Config config;

    public TypeSet<ClassType> classes = new TypeSet<>();
    public TypeSet<Field> fields = new TypeSet<>();
    public TypeSet<Method> methods = new TypeSet<>();
    public TypeSet<Parameter> params = new TypeSet<>();

    public JarBuilder(File jarFile, JsonObject inheritance, Config config) {
        this.jarFile = jarFile;
        this.inheritance = inheritance;
        this.config = config;
    }

    public Jar build() throws IOException {
        try (ZipFile zip = new ZipFile(jarFile)) {
            for (ZipEntry entry : Collections.list(zip.entries())) {
                if (entry.getName().contains(".class") && !config.isExcluded(entry.getName())) {
                    ClassReader reader = new ClassReader(zip.getInputStream(entry));
                    ClassNode classNode = new ClassNode();
                    reader.accept(classNode, 0);
                    addClass(classNode);
                }
            }
        }

        return new Jar(this);
    }

    /**
     * Sets up a ClassType object, including its child methods and/or fields, and adds that class to the global class set.
     * @param node ASM node representation of a Java class.
     */
    private void addClass(ClassNode node) {
        TypeSet<Field> childFields = new TypeSet<>();
        TypeSet<Method> childMethods = new TypeSet<>();
        ClassType cls = new ClassType(node.name, node.superName);

        for (FieldNode fNode : node.fields) {
            Field field = getField(cls, fNode);
            childFields.add(field);
        }

        for (MethodNode mNode : node.methods) {
            Method method = getMethod(cls, mNode);
            childMethods.add(method);
        }

        cls.setChildren(childFields, childMethods); // Add children for future retrieval.
        classes.add(cls);
    }


    /**
     * Creates a field object and adds it to the global field set.
     * @param parent the parent class.
     * @param node the ASM node representation of the field.
     * @return the field.
     */
    private Field getField(ClassType parent, FieldNode node) {
        Field field = new Field(node.name, parent, node.desc);
        fields.add(field);
        return field;
    }

    /**
     * Creates a method object and adds it to the global method set. Determines inheritance and sets up parameters if any are present.
     * @param parent the parent class.
     * @param node the ASM node representation of the method.
     * @return the method.
     */
    private Method getMethod(ClassType parent, MethodNode node) {
        int argCount = Type.getArgumentCount(node.desc);
        String superParent = getSuperParent(parent.getName(), node.name, node.desc);
        InheritanceStatus inheritanceStatus = getInheritanceStatus(superParent);

        Method method = new Method(node.name, parent, superParent, node.desc, inheritanceStatus, argCount);
        methods.add(method);

        if (method.hasParams()) {
            for (Parameter param : method.getParams()) {
                params.add(param);
            }
        }

        return method;
    }

    public String getSuperParent(String className, String methodName, String methodDesc) {
        JsonObject methods = inheritance.getAsJsonObject(className).getAsJsonObject("methods");

        if (methods != null) {
            JsonObject method = methods.getAsJsonObject(methodName + " " + methodDesc);

            if (method.get("override") != null) {

                // Treat constructors (<clinit>, <init>) as having no parents.
                if (methodName.endsWith("init>")) {
                    return null;
                }

                return method.get("override").getAsString();
            }
        }

        return null;
    }

    private InheritanceStatus getInheritanceStatus(String superParent) {
        if (superParent == null || superParent.equals("java/lang/Object")) {
            return InheritanceStatus.NONE;
        } else if (superParent.contains("java/") || superParent.contains("org/lwjgl/") || superParent.contains("com/paulscode/")) {
            return InheritanceStatus.CLASSPATH;
        } else {
            return InheritanceStatus.JAR;
        }
    }
}
