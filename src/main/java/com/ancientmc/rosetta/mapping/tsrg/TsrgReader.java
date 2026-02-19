package com.ancientmc.rosetta.mapping.tsrg;

import com.ancientmc.rosetta.mapping.tsrg.type.*;
import net.neoforged.srgutils.IMappingFile;
import net.neoforged.srgutils.IMappingFile.IClass;
import net.neoforged.srgutils.IMappingFile.IField;
import net.neoforged.srgutils.IMappingFile.IMethod;
import net.neoforged.srgutils.IMappingFile.IParameter;
import net.neoforged.srgutils.INamedMappingFile;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Uses SRGUtils to create a TSRG object from a loaded file.
 */
public class TsrgReader {
    private final File file;

    public TsrgReader(File file) {
        this.file = file;
    }

    public Tsrg read() throws IOException {
        INamedMappingFile names = INamedMappingFile.load(file);
        IMappingFile cnfMap = names.getMap("obf", "cnf");
        IMappingFile idMap = names.getMap("obf", "id");

        List<TsrgType> lines = new LinkedList<>();
        List<TsrgClass> tsrgClasses = new LinkedList<>();

        for (IClass cnfClass : cnfMap.getClasses()) {
            buildClass(cnfClass, idMap, lines, tsrgClasses);
        }

        return new Tsrg(file, lines, tsrgClasses);
    }

    public void buildClass(IClass cnfClass, IMappingFile idMap, List<TsrgType> lines, List<TsrgClass> tsrgClasses) {
        List<TsrgField> childFields = new LinkedList<>();
        List<TsrgMethod> childMethods = new LinkedList<>();

        IClass idClass = idMap.getClass(cnfClass.getOriginal());
        String id = idClass.getMapped();
        TsrgClass cls = new TsrgClass(cnfClass.getOriginal(), cnfClass.getMapped(), id);
        lines.add(cls);

        for (IField cnfField : cnfClass.getFields()) {
            TsrgField field = getField(idClass, cnfField, cls);
            lines.add(field);
            childFields.add(field);
        }

        for (IMethod cnfMethod : cnfClass.getMethods()) {
            TsrgMethod method = getMethod(idClass, cnfMethod, cls);
            lines.add(method);
            childMethods.add(method);

            if (!cnfMethod.getParameters().isEmpty()) {
                List<TsrgParameter> params = new LinkedList<>();

                for (IParameter cnfParam : cnfMethod.getParameters()) {
                    TsrgParameter param = getParam(idClass, cnfParam, cnfMethod, method);
                    lines.add(param);
                    params.add(param);
                }

                method.setParams(params);
            }
        }

        cls.setChildren(childFields, childMethods);
        tsrgClasses.add(cls);
    }

    public TsrgField getField(IClass idClass, IField cnfField, TsrgClass parent) {
        IField idField = idClass.getField(cnfField.getOriginal());
        if (idField == null) return null;

        String id = idField.getMapped();
        return new TsrgField(cnfField.getOriginal(), cnfField.getMapped(), parent, id);
    }

    public TsrgMethod getMethod(IClass idClass, IMethod cnfMethod, TsrgClass parent) {
        IMethod idMethod = idClass.getMethod(cnfMethod.getOriginal(), cnfMethod.getDescriptor());
        if (idMethod == null) return null;

        String id = idMethod.getMapped();
        return new TsrgMethod(cnfMethod.getOriginal(), cnfMethod.getDescriptor(), cnfMethod.getMapped(), parent, id);
    }

    public TsrgParameter getParam(IClass idClass, IParameter cnfParam, IMethod cnfMethod, TsrgMethod parent) {
        IMethod idMethod = idClass.getMethod(cnfMethod.getOriginal(), cnfMethod.getDescriptor());
        if (idMethod == null) return null;

        IParameter idParam = idMethod.getParameter(cnfParam.getIndex());
        if (idParam == null) return null;

        String id = idParam.getMapped();
        return new TsrgParameter(idParam.getIndex(), cnfParam.getMapped(), parent, id);
    }
}
