package com.ancientmc.rosetta.mapping.tsrg;

import com.ancientmc.rosetta.mapping.match.MatchClass;
import com.ancientmc.rosetta.mapping.match.MatchField;
import com.ancientmc.rosetta.mapping.match.MatchMethod;
import net.minecraftforge.srgutils.IMappingFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Tsrg {
    private final IMappingFile mapping;
    private final File file;
    public List<TsrgClass> classes;
    public List<TsrgField> fields;
    public List<TsrgMethod> methods;
    public List<TsrgParameter> params;

    private Tsrg(IMappingFile mapping, File file) {
        this.mapping = mapping;
        this.file = file;
        this.classes = getClasses();
        this.fields = getFields();
        this.methods = getMethods();
        this.params = getParams();
    }

    public static Tsrg load(File file) {
        try {
            IMappingFile mapping = IMappingFile.load(file);
            return new Tsrg(mapping, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TsrgClass> getClasses() {
        List<TsrgClass> list = new ArrayList<>();
        this.mapping.getClasses().forEach(cls -> list.add(new TsrgClass(cls.getOriginal(), cls.getMapped()).setId(file)));
        return list;
    }

    public List<TsrgField> getFields() {
        List<TsrgField> list = new ArrayList<>();
        this.mapping.getClasses().forEach(cls -> {
            if (!cls.getFields().isEmpty()) {
                cls.getFields().forEach(fld -> list.add(new TsrgField(fld.getOriginal(), fld.getMapped(), cls.getOriginal()).setId(file)));
            }
        });
        return list;
    }

    public List<TsrgMethod> getMethods() {
        List<TsrgMethod> list = new ArrayList<>();
        this.mapping.getClasses().forEach(cls -> {
            if (!cls.getMethods().isEmpty()) {
                cls.getMethods().forEach(mtd -> list.add(new TsrgMethod(mtd.getOriginal(), mtd.getMapped(), cls.getOriginal(), mtd.getDescriptor())
                        .setId(file).setParameters(mtd.getParameters())));
            }
        });
        return list;
    }

    public List<TsrgParameter> getParams() {
        List<TsrgParameter> list = new ArrayList<>();
        this.methods.forEach(mtd -> {
            if (!mtd.params.isEmpty()) {
                list.addAll(mtd.params);
            }
        });

        return list;
    }

    public TsrgClass getIntermediateClass(MatchClass cls) {
        for (TsrgClass tsrgClass : this.getClasses()) {
            if (tsrgClass.obf.equals(cls.oldName)) {
                return tsrgClass;
            }
        }
        return null;
    }

    public TsrgField getIntermediateField(MatchField field) {
        for (TsrgField tsrgField : this.getFields()) {
            if (tsrgField.parent.equals(field.oldParent) && tsrgField.obf.equals(field.oldName)) {
                return tsrgField;
            }
        }
        return null;
    }

    public TsrgMethod getIntermediateMethod(MatchMethod method) {
        for (TsrgMethod tsrgMethod : this.methods) {
            if (tsrgMethod.parent.equals(method.oldParent) && tsrgMethod.obf.equals(method.oldName) && tsrgMethod.desc.equals(method.oldDesc)) {
                return tsrgMethod;
            }
        }
        return null;
    }
}
