package com.ancientmc.rosetta.function;

import com.ancientmc.rosetta.jar.Ids;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class Function {

    /**
     * Main execution method for this function. It writes the TSRG data and then the ID csv.
     */
    public void exec(File tsrg, File ids) {
        try {
            List<String> lines = getLines();
            writeTsrg(tsrg, lines);
            writeIds(ids);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the lines to be written to the TSRG file.
     * @return The lines.
     * @throws IOException exception.
     */
    public abstract List<String> getLines() throws IOException;

    /**
     * Writes the lines to the given TSRG file.
     * @param tsrg The TSRG file.
     * @param lines The lines to be written.
     */
    public void writeTsrg(File tsrg, List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tsrg))) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes the ID counters to the ID csv.
     * @param ids the ID csv.
     */
    public void writeIds(File ids) {
        Map<String, Integer> counters = Ids.getIdCounters();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ids))) {
            writer.write("type, counter\n");

            for (Map.Entry<String, Integer> counter : counters.entrySet()) {
                writer.write(String.join(",", counter.getKey(), counter.getValue() + "\n"));
            }

            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
            case "class" -> indent = "";
            case "field", "method" -> indent = "\t";
            case "param" -> indent = "\t\t";
        }

        return indent;
    }
}
