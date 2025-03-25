package com.ancientmc.rosetta.mapping.tsrg;

import com.ancientmc.rosetta.util.Util;
import net.minecraftforge.srgutils.IMappingFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record TsrgMethod(String obf, String mapped, String parent, String desc, File file, Collection<? extends IMappingFile.IParameter> iParams) {
    public String getId() {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            String classLine = lines.stream().filter(l -> l.startsWith(parent + " ")).findAny().orElse(null);

            List<String> classBlock = lines.subList(lines.indexOf(classLine), Util.getNextTsrgClass(lines, classLine));
            String line = classBlock.stream().filter(l -> l.contains(desc + " " + mapped)).findAny().orElse(null);

            if (line != null) {
                return line.split(" ")[3];
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public List<TsrgParameter> getParams() {
        List<TsrgParameter> params = new ArrayList<>();
        iParams.forEach(param -> params.add(new TsrgParameter(param.getIndex(), this, param.getMapped())));
        return params;
    }

    public TsrgParameter getParameter(int index) {
        if (!getParams().isEmpty()) {
            for (TsrgParameter param : getParams()) {
                if (param.index() == index) {
                    return param;
                }
            }
        }

        return null;
    }
}
