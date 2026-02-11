package com.ancientmc.rosetta.function;

import com.ancientmc.rosetta.Config;
import com.ancientmc.rosetta.jar.IdSet;
import com.ancientmc.rosetta.jar.Jar;
import com.ancientmc.rosetta.jar.type.ClassType;
import com.ancientmc.rosetta.jar.type.Field;
import com.ancientmc.rosetta.jar.type.Method;
import com.ancientmc.rosetta.jar.type.Parameter;
import com.ancientmc.rosetta.mapping.tsrg.Tsrg;
import com.ancientmc.rosetta.mapping.tsrg.type.TsrgClass;
import com.ancientmc.rosetta.mapping.tsrg.type.TsrgField;
import com.ancientmc.rosetta.mapping.tsrg.type.TsrgMethod;
import com.ancientmc.rosetta.mapping.tsrg.type.TsrgType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates a TSRG with a fresh set of intermediary names and IDs. Used primarily if a JAR is the start of a chain, such as the first version of
 * Minecraft, or if the version being updated to is too different from the last to warrant a simple update.
 * @author moist-mason
 */
public class GenerateFunction extends Function {
    private final Jar jar;
    private final Config config;
    private final File tsrgFile;
    private final File idCsv;

    private final IdSet<ClassType> classIds;
    private final IdSet<Field> fieldIds;
    private final IdSet<Method> methodIds;
    private final IdSet<Parameter> paramIds;

    public GenerateFunction(Jar jar, Config config, File tsrgFile, File idCsv) {
        this.jar = jar;
        this.config = config;
        this.tsrgFile = tsrgFile;
        this.idCsv = idCsv;

        this.classIds = IdSet.createFresh(jar.getClasses());
        this.fieldIds = IdSet.createFresh(jar.getFields());
        this.methodIds = IdSet.createFresh(jar.getMethods().filtered(m -> !m.isInherited()));
        this.paramIds = IdSet.createFresh(jar.getParams().filtered(p -> !p.getParent().isInherited()));
    }

    @Override
    public Tsrg buildTsrg() {
        int lineIndex = -1;
        List<Tsrg.Line<? extends TsrgType>> lines = new ArrayList<>();

        for (ClassType cls : jar.getClasses()) {
            lineIndex++;
            addClass(lineIndex, lines, cls);
        }

        return new Tsrg(tsrgFile, lines);
    }

    public void addClass(int lineIndex, List<Tsrg.Line<? extends TsrgType>> lines, ClassType cls) {
        TsrgClass tsrgCls = getTsrgClass(cls);
        lines.add(lineIndex, new Tsrg.Line<>(tsrgCls));

        for (Field field : cls.getFields()) {
            lineIndex++;
            TsrgField tsrgFld = getTsrgField(field);
            lines.add(lineIndex, new Tsrg.Line<>(tsrgFld));
        }

        for (Method method : cls.getMethods()) {
            lineIndex++;
            TsrgMethod tsrgMtd = getTsrgMethod(method);
            lines.add(lineIndex, new Tsrg.Line<>(tsrgMtd));
        }
    }

    public TsrgClass getTsrgClass(ClassType cls) {
        String id = classIds.get(cls);
        String mapped = config.isPremapped(cls.getName()) ? cls.getName() : config.namespace + "c_" + id;
        return new TsrgClass(cls.getName(), mapped, id);
    }

    public TsrgField getTsrgField(Field field) {
        String id = fieldIds.get(field);
        String mapped = field.getName().length() <= config.minObfChars ? "f_" + id : field.getName();
        return new TsrgField(field.getName(), mapped, id);
    }

    public TsrgMethod getTsrgMethod(Method method) {
        Method superMethod = jar.getClasses().get(method.getSuperParentName())
                .getMethod(method.getName(), method.getDesc());
        String mid = method.isInherited() ? methodIds.get(superMethod) : methodIds.get(method);
        String mapped = getMappedMethod(method, mid);
        return new TsrgMethod(method.getName(), method.getDesc(), mapped, mid);
    }

    public String getMappedMethod(Method method, String mid) {
        if (method.getInheritanceStatus().equals(Method.InheritanceStatus.CLASSPATH)) {
            return method.getName(); // all descendants of libraries or the JDK should not be obfuscated.
        }

        return method.getName().length() <= config.minObfChars ? "m_" + mid : method.getName();
    }
}
