package com.ancientmc.rosetta.function;

import com.ancientmc.rosetta.Config;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class Function {

    /**
     * Main execution method for this function.
     */
    public abstract void exec();

    /**
     * Adds a given entry to the TSRG line list. Called in the Generate Task since all entries are new.
     * @param lines The list of lines being added to.
     * @param type The entry type. Options are "class", "field", "method", and "param".
     * @param obf The obfuscated entry. In the case of methods, the obfuscated method name and desc are input together with a space separating them.
     * @param mapped The mapped name for this entry.
     * @param id The five-digit ID for this entry.
     */
    public static void addLine(List<String> lines, String type, String obf, String mapped, String id) {
        addLine(lines, type, obf, mapped, id, true);
    }

    /**
     * Adds a given entry to the TSRG line list.
     * @param lines The list of lines being added to.
     * @param type The entry type. Options are "class", "field", "method", and "param".
     * @param obf The obfuscated entry. In the case of methods, the obfuscated method name and desc are input together with a space separating them.
     * @param mapped The mapped name for this entry.
     * @param id The five-digit ID for this entry.
     * @param newEntry true if the entry is new to the updated version, false if it isn't.
     */
    public static void addLine(List<String> lines, String type, String obf, String mapped, String id, boolean newEntry) {
        String indent = getIndent(type);
        String entryStatus = newEntry ? "NEW" : "OLD";
        lines.add(indent + String.join(" ", obf, mapped, id));
        toPrint(type, obf, mapped, id, entryStatus);
    }

    /**
     * Prints out the given entry to the console.
     * @param type The entry type. Options are "class", "field", "method", and "param".
     * @param obf The obfuscated entry. In the case of methods, the obfuscated method name and desc are input together with a space separating them.
     * @param mapped The mapped name for this entry.
     * @param id The five-digit ID for this entry.
     */
    public static void toPrint(String type, String obf, String mapped, String id, String entryStatus) {
        System.out.println(String.join(" ", type.toUpperCase(Locale.ROOT) + ":", obf, "->", mapped, "\tID:", id, "\tENTRY_STATUS:", entryStatus));
    }

    /**
     * Gets the indent prefixing the entry line in the TSRG.
     * @param type The entry type. Options are "class", "field", "method", and "param".
     * @return The indent. returns no indent if the entry is a class, one indent if the entry is a field or method, and two indents if the entry is a parameter.
     */
    public static String getIndent(String type) {
        String indent = "";
        switch (type) {
            case "class" -> {
                indent = "";
            }
            case "field", "method" -> {
                indent = "\t";
            }
            case "param" -> {
                indent = "\t\t";
            }
        }
        return indent;
    }
}
