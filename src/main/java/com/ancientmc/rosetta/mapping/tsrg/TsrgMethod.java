package com.ancientmc.rosetta.mapping.tsrg;

import com.ancientmc.rosetta.util.Util;
import net.minecraftforge.srgutils.IMappingFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TsrgMethod {
    public String obf;
    public String mapped;
    public String parent;
    public String desc;
    public String id;
    public List<TsrgParameter> params = new ArrayList<>();

    public TsrgMethod(String obf, String mapped, String parent, String desc) {
        this.obf = obf;
        this.mapped = mapped;
        this.parent = parent;
        this.desc = desc;
    }

    public TsrgMethod setId(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            String classLine = lines.stream().filter(l -> l.startsWith(parent + " ")).findAny().orElse(null);
            List<String> classBlock = lines.subList(lines.indexOf(classLine), Util.getNextTsrgClass(lines, classLine));
            classBlock.stream().filter(l -> l.contains(mapped)).findAny()
                    .ifPresent(line -> this.id = line.split(" ")[3]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    public TsrgMethod setParameters(Collection<? extends IMappingFile.IParameter> params) {
        params.forEach(param -> this.params.add(new TsrgParameter(param.getIndex(), this, param.getMapped()).setId(param.getMapped())));
        return this;
    }

    public TsrgParameter getParameter(int index) {
        if (!params.isEmpty()) {
            for (TsrgParameter param : params) {
                if (param.index == index) {
                    return param;
                }
            }
        }
        return null;
    }
}
