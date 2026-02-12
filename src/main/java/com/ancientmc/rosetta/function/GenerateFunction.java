package com.ancientmc.rosetta.function;

import com.ancientmc.rosetta.Config;
import com.ancientmc.rosetta.jar.IdSet;
import com.ancientmc.rosetta.jar.Jar;
import com.ancientmc.rosetta.jar.type.ClassType;
import com.ancientmc.rosetta.jar.type.Field;
import com.ancientmc.rosetta.jar.type.Method;
import com.ancientmc.rosetta.jar.type.Parameter;
import com.ancientmc.rosetta.mapping.tsrg.Tsrg;
import com.ancientmc.rosetta.mapping.tsrg.type.*;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
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
    public void callIdWriter() throws IOException {
        super.writeIds(idCsv, classIds, fieldIds, methodIds, paramIds);
    }

    @Override
    public Tsrg buildTsrg() {
        List<Tsrg.Line<? extends TsrgType>> lines = new LinkedList<>();
        List<TsrgClass> tsrgClasses = new LinkedList<>();

        for (ClassType cls : jar.getClasses()) {
            buildClass(lines, cls, tsrgClasses);
        }

        return new Tsrg(tsrgFile, lines, tsrgClasses);
    }

    public void buildClass(List<Tsrg.Line<? extends TsrgType>> lines, ClassType cls, List<TsrgClass> tsrgClasses) {
        List<TsrgField> childTsrgFields = new LinkedList<>();
        List<TsrgMethod> childTsrgMethods = new LinkedList<>();

        TsrgClass tsrgCls = getTsrgClass(cls);
        lines.add(new Tsrg.Line<>(tsrgCls));

        for (Field field : cls.getFields()) {
            TsrgField tsrgFld = getTsrgField(field, tsrgCls);
            childTsrgFields.add(tsrgFld);
            lines.add(new Tsrg.Line<>(tsrgFld));
        }

        for (Method method : cls.getMethods()) {
            Method superMethod = jar.getSuperMethod(method);
            TsrgMethod tsrgMtd = getTsrgMethod(method, superMethod, tsrgCls);
            childTsrgMethods.add(tsrgMtd);
            lines.add(new Tsrg.Line<>(tsrgMtd));

            if (method.hasParams()) {
                for (Parameter param : method.getParams()) {
                    TsrgParameter tsrgParam = getTsrgParameter(param, method, superMethod, tsrgMtd);
                    lines.add(new Tsrg.Line<>(tsrgParam));
                }
            }
        }

        tsrgCls.setChildren(childTsrgFields, childTsrgMethods);
        tsrgClasses.add(tsrgCls);
    }

    public TsrgClass getTsrgClass(ClassType cls) {
        String id = classIds.get(cls);
        String mapped = config.isPremapped(cls.getName()) ? cls.getName() : config.namespace + "c_" + id;
        return new TsrgClass(cls.getName(), mapped, id);
    }

    public TsrgField getTsrgField(Field field, TsrgClass tsrgCls) {
        String id = fieldIds.get(field);
        String mapped = field.getName().length() <= config.maxObfChars ? "f_" + id : field.getName();
        return new TsrgField(field.getName(), mapped, tsrgCls, id);
    }

    public TsrgMethod getTsrgMethod(Method method, Method superMethod, TsrgClass tsrgCls) {
        String mid = method.isInherited() ? methodIds.get(superMethod) : methodIds.get(method);
        String mapped = getMappedMethod(method, mid);
        return new TsrgMethod(method.getName(), method.getDesc(), mapped, tsrgCls, mid);
    }

    public TsrgParameter getTsrgParameter(Parameter param, Method method, Method superMethod, TsrgMethod tsrgMtd) {
        String pid = param.getParent().isInherited()
                ? paramIds.get(superMethod.getParam(param.getIndex()))
                : paramIds.get(method.getParam(param.getIndex()));
        String name = "p_" + pid;
        return new TsrgParameter(param.getIndex(), name, tsrgMtd, pid);
    }

    public String getMappedMethod(Method method, String mid) {
        if (method.getInheritanceStatus().equals(Method.InheritanceStatus.CLASSPATH)
                || method.getName().endsWith("init>")) {
            return method.getName(); // don't add intermediary names to constructors or JDK/dependency-inherited methods
        }

        return method.getName().length() <= config.maxObfChars ? "m_" + mid : method.getName();
    }
}
