package com.ancientmc.rosetta.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Util {
    public static JsonObject getJson(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            JsonElement element = JsonParser.parseReader(reader);
            return element.getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getNextTsrgClass(List<String> lines, String classLine) {
        for (int i = lines.indexOf(classLine) + 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!line.startsWith("\t")) { // class line = any line without prefixed indents
                return i;
            }
        }
        return lines.size(); // returned after everything's parsed.
    }

}