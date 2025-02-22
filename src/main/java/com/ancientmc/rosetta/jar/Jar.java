package com.ancientmc.rosetta.jar;

import com.ancientmc.rosetta.Config;
import com.ancientmc.rosetta.jar.type.ClassType;
import com.ancientmc.rosetta.jar.type.Field;
import com.ancientmc.rosetta.jar.type.Method;
import com.ancientmc.rosetta.jar.type.Parameter;
import com.ancientmc.rosetta.util.Util;
import com.google.gson.JsonObject;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Jar {
    public List<ClassType> classes = new ArrayList<>();
    public List<Field> fields = new ArrayList<>();
    public List<Method> methods = new ArrayList<>();
    public List<Parameter> params = new ArrayList<>();

    private Jar(File file, File inheritance, Config config) {
        try (ZipFile zip = new ZipFile(file)) {
            for (ZipEntry entry : Collections.list(zip.entries())) {

                if(!entry.getName().endsWith(".class") && !entry.getName().contains(config.excludedPackages.stream().findAny().orElseThrow())) {
                    continue;
                }
                ClassReader reader = new ClassReader(zip.getInputStream(entry));
                ClassNode classNode = new ClassNode();
                reader.accept(classNode, 0);

                this.classes.add(new ClassType(classNode.name, classNode.superName));

                for (FieldNode fieldNode : classNode.fields) {
                    fields.add(new Field(fieldNode.name, classNode.name, fieldNode.desc));
                }

                for (MethodNode methodNode : classNode.methods) {
                    int count = Type.getArgumentTypes(methodNode.desc).length;
                    String superParent = getSuperParent(inheritance, classNode.name, methodNode.name, methodNode.desc);
                    boolean inherited = superParent != null;

                    Method method = new Method(methodNode.name, classNode.name, methodNode.desc, superParent, inherited).setParameters(count);
                    methods.add(method);

                    if (!method.params.isEmpty()) {
                        params.addAll(method.params);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Jar load(File file, File inheritance, Config config) {
        return new Jar(file, inheritance, config);
    }

    public static String getSuperParent(File file, String className, String methodName, String methodDesc) {
        JsonObject json = Util.getJson(file);
        JsonObject methods = json.getAsJsonObject(className).getAsJsonObject("methods");
        if (methods != null) {
            JsonObject method = methods.getAsJsonObject(methodName + " " + methodDesc);
            if (method.get("override") != null) {

                // Treat the following as having no parents:
                // 1. All constructors (<clinit>, <init>)
                // 2. Any method whose override is from a JDK class
                if (method.get("override").getAsString().contains("java") || methodName.endsWith("init>")) {
                    return null;
                }
                return method.get("override").getAsString();
            }
        }
        return null;
    }

    public ClassType getClass(String name) {
        return classes.stream().filter(cls -> cls.name.equals(name)).findAny().orElse(null);
    }
}