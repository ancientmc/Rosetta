package com.ancientmc.rosetta;

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

    public static String prettyListString(List<String> list) {
        return String.join("\n", list);
    }
}