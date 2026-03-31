package com.ancientmc.rosetta.function;

import com.ancientmc.rosetta.Config;
import com.ancientmc.rosetta.jar.Jar;
import com.ancientmc.rosetta.jar.type.*;
import com.ancientmc.rosetta.mapping.IdSet;
import com.ancientmc.rosetta.mapping.match.type.MatchType;
import com.ancientmc.rosetta.mapping.tsrg.Tsrg;
import com.ancientmc.rosetta.mapping.tsrg.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static Logger LOGGER = LoggerFactory.getLogger(GenerateFunction.class);

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
        this.methodIds = IdSet.createFresh(jar.getMethods().filtered(m -> !m.isInheritedFromJar()));
        this.paramIds = IdSet.createFresh(jar.getParams().filtered(p -> !p.getParent().isInheritedFromJar()));
    }

    @Override
    protected void callIdWriter() throws IOException {
        super.writeIds(idCsv, classIds.counter, fieldIds.counter, methodIds.counter, paramIds.counter);
    }

    @Override
    protected Tsrg buildTsrg() {
        List<TsrgType> lines = new LinkedList<>();
        List<TsrgClass> tsrgClasses = new LinkedList<>();

        for (ClassType cls : jar.getClasses()) {
            buildClass(lines, cls, tsrgClasses);
        }

        return new Tsrg(tsrgFile, lines, tsrgClasses);
    }

    private void buildClass(List<TsrgType> lines, ClassType cls, List<TsrgClass> tsrgClasses) {
        List<TsrgField> childTsrgFields = new LinkedList<>();
        List<TsrgMethod> childTsrgMethods = new LinkedList<>();

        TsrgClass tsrgCls = getTsrgClass(cls);
        lines.add(tsrgCls);

        for (Field field : cls.getFields()) {
            TsrgField tsrgFld = getTsrgField(field, tsrgCls);
            childTsrgFields.add(tsrgFld);
            lines.add(tsrgFld);
        }

        for (Method method : cls.getMethods()) {
            Method superMethod = jar.getSuperMethod(method);
            TsrgMethod tsrgMtd = getTsrgMethod(method, superMethod, tsrgCls);
            childTsrgMethods.add(tsrgMtd);
            lines.add(tsrgMtd);

            if (method.hasParams()) {
                List<TsrgParameter> tsrgParams = new LinkedList<>();

                for (Parameter param : method.getParams()) {
                    TsrgParameter tsrgParam = getTsrgParameter(param, method, superMethod, tsrgMtd);
                    lines.add(tsrgParam);
                    tsrgParams.add(tsrgParam);
                }

                tsrgMtd.setParams(tsrgParams);
            }
        }

        tsrgCls.setChildren(childTsrgFields, childTsrgMethods);
        tsrgClasses.add(tsrgCls);
    }

    private <T extends TsrgType> T logAndGetTsrg(T type, String typeName) {
        LOGGER.info("TSRG {} -> {}", typeName, type);
        return type;
    }

    private TsrgClass getTsrgClass(ClassType cls) {
        String id = classIds.get(cls);
        String mapped = config.isUnobfuscated(cls.getName()) ? cls.getName() : config.namespace + "c_" + id;
        return logAndGetTsrg(new TsrgClass(cls.getName(), mapped, id), "class");
    }

    private TsrgField getTsrgField(Field field, TsrgClass tsrgCls) {
        String id = fieldIds.get(field);
        LOGGER.info("New field. ID -> {}", id);
        String mapped = field.getName().length() <= config.maxObfChars ? "f_" + id : field.getName();
        return logAndGetTsrg(new TsrgField(field.getName(), mapped, tsrgCls, id), "field");
    }

    private TsrgMethod getTsrgMethod(Method method, Method superMethod, TsrgClass tsrgCls) {
        String id = method.isInheritedFromJar() ? methodIds.get(superMethod) : methodIds.get(method);
        String mapped = getMappedMethod(method, id);
        return logAndGetTsrg(new TsrgMethod(method.getName(), method.getDesc(), mapped, tsrgCls, id), "method");
    }

    private TsrgParameter getTsrgParameter(Parameter param, Method method, Method superMethod, TsrgMethod tsrgMtd) {
        String id = param.getParent().isInheritedFromJar()
                ? paramIds.get(superMethod.getParam(param.getIndex()))
                : paramIds.get(method.getParam(param.getIndex()));
        String name = "p_" + id;
        return logAndGetTsrg(new TsrgParameter(param.getIndex(), name, tsrgMtd, id), "param");
    }

    private String getMappedMethod(Method method, String mid) {
        if (method.getInheritanceStatus().isClasspath() // don't add intermediary names to constructors, the main method, or JDK/dependency-inherited methods
                || method.getName().endsWith("init>")
                || (method.getName().equals("main") && method.getDesc().equals("([Ljava/lang/String;)V"))) {
            return method.getName();
        }

        return method.getName().length() <= config.maxObfChars ? "m_" + mid : method.getName();
    }
}
