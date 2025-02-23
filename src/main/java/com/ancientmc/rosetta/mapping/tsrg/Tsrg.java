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

    public TsrgClass getClass(MatchClass cls) {
        return this.classes.stream().filter(tc -> tc.obf.equals(cls.oldName)).findAny().orElse(null);
    }

    public TsrgField getField(MatchField field) {
        return this.fields.stream().filter(tf -> tf.parent.equals(field.oldParent) && tf.obf.equals(field.oldName)).findAny().orElse(null);
    }

    public TsrgMethod getMethod(MatchMethod method) {
        return this.methods.stream().filter(tm -> tm.parent.equals(method.oldParent) && tm.obf.equals(method.oldName) && tm.desc.equals(method.oldDesc)).findAny().orElse(null);
    }
}
