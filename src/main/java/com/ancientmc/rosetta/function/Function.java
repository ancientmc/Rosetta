package com.ancientmc.rosetta.function;

import java.util.List;
import java.util.Locale;

public abstract class Function {

    /**
     * Main execution method for this function.
     */
    public abstract void exec();

    /**
     * Adds a given entry to the TSRG line list.
     * @param lines The list of lines being added to.
     * @param type The entry type. Options are "class", "field", "method", and "param".
     * @param obf The obfuscated entry. In the case of methods, the obfuscated method name and desc are input together with a space separating them.
     * @param mapped The mapped name for this entry.
     * @param id The five-digit ID for this entry.
     */
    public static void addLine(List<String> lines, String type, String obf, String mapped, String id) {
        String indent = getIndent(type);
        lines.add(indent + String.join(" ", obf, mapped, id));
        toPrint(type, obf, mapped, id);
    }

    /**
     * Prints out the given entry to the console.
     * @param type The entry type. Options are "class", "field", "method", and "param".
     * @param obf The obfuscated entry. In the case of methods, the obfuscated method name and desc are input together with a space separating them.
     * @param mapped The mapped name for this entry.
     * @param id The five-digit ID for this entry.
     */
    public static void toPrint(String type, String obf, String mapped, String id) {
        System.out.println(String.join(" ", type.toUpperCase(Locale.ROOT) + ":", obf, "->", mapped, "\tID:", id));
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
