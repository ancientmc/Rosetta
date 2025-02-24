package com.ancientmc.rosetta.test;

import net.minecraftforge.mappingverifier.IVerifier;
import net.minecraftforge.mappingverifier.MappingVerifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Verifier {
    public static void verify(String function, String jarPath, String tsrgPath) throws IOException {
        List<String> lines = new ArrayList<>();
        File jar = new File(jarPath);
        File tsrg = new File(tsrgPath);

        MappingVerifier verifier = new MappingVerifier();
        verifier.loadJar(jar);
        verifier.loadMap(tsrg);
        verifier.addTask("OverrideNames");

        if (verifier.verify()) {
            System.out.println("Verification successful");
            lines.add("Verification successful");
        } else {
            for (IVerifier task : verifier.getTasks()) {
                List<String> errors = task.getErrors();
                errors.forEach(e -> {
                    System.out.println("ERROR: " + e + " in task " + task.getName());
                    lines.add("ERROR: " + e + " in task " + task.getName());
                });
            }
        }

        File log = new File(Paths.LOG);
        writeLog(function, lines, log);

    }

    public static void writeLog(String function, List<String> lines, File log) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(log))) {
            writer.write("DATE:\t" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
            writer.write("FUNCTION:\t" + capitalize(function) + "\n");
            writer.write("OUT:\n");

            for (String line : lines) {
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class ParamVerifier implements IVerifier {

        @Override
        public List<String> getErrors() {
            return List.of();
        }

        @Override
        public boolean process() {
            return false;
        }
    }

    public static String capitalize(String in) {
        return in.substring(0, 1).toUpperCase(Locale.ROOT) + in.substring(1);
    }
}
