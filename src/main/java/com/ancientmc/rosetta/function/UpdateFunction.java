package com.ancientmc.rosetta.function;

import com.ancientmc.rosetta.Config;
import com.ancientmc.rosetta.jar.Jar;
import com.ancientmc.rosetta.jar.type.*;
import com.ancientmc.rosetta.mapping.Counters;
import com.ancientmc.rosetta.mapping.IdSet;
import com.ancientmc.rosetta.mapping.match.Match;
import com.ancientmc.rosetta.mapping.match.type.*;
import com.ancientmc.rosetta.mapping.tsrg.Tsrg;
import com.ancientmc.rosetta.mapping.tsrg.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class UpdateFunction extends Function {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateFunction.class);

    private final Jar jar;
    private final Config config;
    private final Match match;
    private final Tsrg oldTsrg;
    private final Counters oldCounters;
    private final File newTsrg;
    private final File newIdCsv;

    private final IdSet<ClassType> classIds;
    private final IdSet<Field> fieldIds;
    private final IdSet<Method> methodIds;
    private final IdSet<Parameter> paramIds;

    public UpdateFunction(Jar jar, Config config, Match match, Tsrg oldTsrg, Counters oldCounters, File newTsrg, File newIdCsv) {
        this.jar = jar;
        this.config = config;
        this.match = match;
        this.oldTsrg = oldTsrg;
        this.oldCounters = oldCounters;
        this.newTsrg = newTsrg;
        this.newIdCsv = newIdCsv;

        this.classIds = IdSet.createNew(jar.getClasses().filtered(c -> !match.isMatched(match.getClasses(), c)), getCounter("classes"));
        this.fieldIds = IdSet.createNew(jar.getFields().filtered(f -> !match.isMatched(match.getFields(), f)), getCounter("fields"));
        this.methodIds = IdSet.createNew(jar.getMethods().filtered(m -> !m.isInheritedFromJar() && !match.isMatched(match.getMethods(), m)), getCounter("methods"));
        this.paramIds = IdSet.createNew(jar.getParams().filtered(p -> isNewParam(p, match)), getCounter("params"));
    }

    @Override
    protected void callIdWriter() throws IOException {
        super.writeIds(newIdCsv, classIds.counter, fieldIds.counter, methodIds.counter, paramIds.counter);
    }

    @Override
    protected Tsrg buildTsrg() {
        List<TsrgType> lines = new LinkedList<>();
        List<TsrgClass> tsrgClasses = new LinkedList<>();

        for (ClassType cls : jar.getClasses()) {
            LOGGER.info("Class -> {}", cls.getName());
            buildClass(lines, cls, tsrgClasses);
        }

        return new Tsrg(newTsrg, lines, tsrgClasses);
    }

    private <T extends TsrgType> T logAndGetTsrg(T type, String typeName, String category) {
        LOGGER.info("{} TSRG {} -> {}", category, typeName, type);
        return type;
    }

    private <J extends Type, T extends MatchType<J>> T logAndGetMatch(T type, String typeName) {
        LOGGER.info("Match {} -> {}", typeName, type);
        return type;
    }

    private void buildClass(List<TsrgType> lines, ClassType cls, List<TsrgClass> tsrgClasses) {
        List<TsrgField> childTsrgFields = new LinkedList<>();
        List<TsrgMethod> childTsrgMethods = new LinkedList<>();

        TsrgClass tsrgClass = getTsrgCls(cls);
        lines.add(tsrgClass);

        for (Field field : cls.getFields()) {
            TsrgField tsrgField = getTsrgField(field, tsrgClass);
            childTsrgFields.add(tsrgField);
            lines.add(tsrgField);
        }

        for (Method method : cls.getMethods()) {
            if (method.isInheritedFromJar()) {
                Method superMethod = jar.getSuperMethod(method);
                TsrgMethod tsrgMethod = getTsrgMethod(superMethod, tsrgClass);
                childTsrgMethods.add(tsrgMethod);
                lines.add(tsrgMethod);
                if (superMethod != null) addParams(superMethod, lines, tsrgMethod);
            } else {
                TsrgMethod tsrgMethod = getTsrgMethod(method, tsrgClass);
                childTsrgMethods.add(tsrgMethod);
                lines.add(tsrgMethod);
                addParams(method, lines, tsrgMethod);
            }
        }

        tsrgClass.setChildren(childTsrgFields, childTsrgMethods);
        tsrgClasses.add(tsrgClass);
    }

    private TsrgClass getTsrgCls(ClassType cls) {
        TsrgClass newClass;

        if (match.isMatched(match.getClasses(), cls)) {
            MatchClass matchClass = logAndGetMatch(match.getType(match.getClasses(), cls), "class"); // get match class from java class info
            TsrgClass oldTsrgClass = logAndGetTsrg(oldTsrg.getClass(matchClass.getOldName()), "class", "Old"); // get tsrg class from match info
            newClass = new TsrgClass(cls.getName(), config.namespace + oldTsrgClass.getPackagelessMapped(), oldTsrgClass.getId());
        } else {
            String id = classIds.get(cls);
            LOGGER.info("New class. ID -> {}", id);
            String mapped = config.isUnobfuscated(cls.getName()) ? cls.getName() : config.namespace + "c_" + id;
            newClass = new TsrgClass(cls.getName(), mapped, id);
        }

        return logAndGetTsrg(newClass, "class", "New");
    }

    private TsrgField getTsrgField(Field field, TsrgClass tsrgClass) {
        TsrgField newField;

        if (match.isMatched(match.getFields(), field)) {
            MatchField matchField = match.getType(match.getFields(), field);
            TsrgField oldTsrgField = logAndGetTsrg(oldTsrg.getField(matchField.getOldName(), matchField.getOldParentName()), "field", "Old");
            newField = new TsrgField(field.getName(), oldTsrgField.getMapped(), tsrgClass, oldTsrgField.getId());
        } else {
            String id = fieldIds.get(field);
            LOGGER.info("New field. ID -> {}", id);
            String mapped = field.getName().length() <= config.maxObfChars ? "f_" + id : field.getName();
            newField = new TsrgField(field.getName(), mapped, tsrgClass, id);
        }

        return logAndGetTsrg(newField, "field", "New");
    }

    private TsrgMethod getTsrgMethod(Method method, TsrgClass tsrgClass) {
        TsrgMethod newMethod;

        if (match.isMatched(match.getMethods(), method)) {
            MatchMethod matchMethod = logAndGetMatch(match.getType(match.getMethods(), method), "method");
            TsrgMethod tsrgMethod = oldTsrg.getMethod(matchMethod.getOldName(), matchMethod.getOldDesc(), matchMethod.getOldParentName());
            newMethod = new TsrgMethod(method.getName(), method.getDesc(), tsrgMethod.getMapped(), tsrgClass, tsrgMethod.getId());
        } else {
            String mid = methodIds.get(method);
            LOGGER.info("New method. ID -> {}", mid);
            String mapped = getMappedMethod(method, mid);
            newMethod = new TsrgMethod(method.getName(), method.getDesc(), mapped, tsrgClass, mid);
        }

        return logAndGetTsrg(newMethod, "method", "New");
    }

    private String getMappedMethod(Method method, String mid) {
        if (method.getInheritanceStatus().isClasspath() // don't add intermediary names to constructors, the main method, or JDK/dependency-inherited methods
                || method.getName().endsWith("init>")
                || (method.getName().equals("main") && method.getDesc().equals("([Ljava/lang/String;)V"))) {
            return method.getName();
        }

        return method.getName().length() <= config.maxObfChars ? "m_" + mid : method.getName();
    }

    private void addParams(Method method, List<TsrgType> lines, TsrgMethod parent) {
        if (method.hasParams()) {
            if (match.isMatched(match.getMethods(), method)) { // matched method
                for (Parameter param : method.getParams()) {
                    TsrgParameter newTsrgParam;
                    MatchMethod matchMethod = match.getType(match.getMethods(), method);
                    TsrgMethod tsrgMethod = oldTsrg.getMethod(matchMethod.getOldName(), matchMethod.getOldDesc(), matchMethod.getOldParentName());
                    MatchParameter matchParam = matchMethod.getParams()
                            .stream().filter(p -> p.getNewIndex() == param.getIndex()).findAny().orElse(null);

                    if (matchParam != null) { // matched param in matched method
                        LOGGER.info("Matched param in matched method -> {}", matchParam);
                        TsrgParameter oldTsrgParam = tsrgMethod.getParameter(matchParam.getOldIndex());
                        newTsrgParam = new TsrgParameter(param.getIndex(), oldTsrgParam.getMapped(), parent, oldTsrgParam.getId());
                    } else { // new param in matched method
                        String id = paramIds.get(param);
                        LOGGER.info("New param in matched method. ID -> {}", id);
                        newTsrgParam = new TsrgParameter(param.getIndex(), "p_" + id, parent, id);
                    }

                    LOGGER.info("New TSRG param -> {}", newTsrgParam);
                    lines.add(newTsrgParam);
                }
            } else { // unmatched method, all new params
                for (Parameter param : method.getParams()) {
                    String id = paramIds.get(param);
                    LOGGER.info("New parameter in new method. ID -> {}", id);
                    TsrgParameter newTsrgParam = new TsrgParameter(param.getIndex(), "p_" + id, parent, id);
                    LOGGER.info("New TSRG param -> {}", newTsrgParam);
                    lines.add(newTsrgParam);
                }
            }
        }
    }
    
    private int getCounter(String type) {
        return oldCounters.getCounter(type);
    }

    // param id logic is complicated, so separate method needed
    private boolean isNewParam(Parameter param, Match match) {
        Method parent = param.getParent();

        if (!parent.isInheritedFromJar()) {
            if (!match.isMatched(match.getMethods(), parent)) { // 1) parent method is new to this version
                return true;
            } else {
                MatchMethod matchMethod = match.getType(match.getMethods(), parent);

                if (!matchMethod.getParams().isEmpty()) { // 2) both the parent method and the param is old
                    return matchMethod.getParams().stream().noneMatch(p -> p.getNewIndex() == param.getIndex());
                } else {
                    return true;
                }
            }
        }

        return false;
    }
}
