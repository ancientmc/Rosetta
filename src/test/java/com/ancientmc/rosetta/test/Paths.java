package com.ancientmc.rosetta.test;

/**
 * Declares the resource paths for our tests.
 */
public class Paths {

    /*
     * Generate TSRG
     */
    public static final String CONFIG = getResource("rosetta.cfg");
    public static final String OLD_JAR = getResource("old.jar");
    public static final String OLD_INHERITANCE = getResource("old_inheritance.json") ;
    public static final String OLD_TSRG = getResource("old.tsrg");
    public static final String OLD_IDS = getResource("old_ids.csv");

    /*
     * Update TSRG
     */
    public static final String MATCH = getResource("file.match");
    public static final String NEW_JAR = getResource("new.jar");
    public static final String NEW_INHERITANCE = getResource("new_inheritance.json");
    public static final String NEW_TSRG = getResource("new.tsrg");
    public static final String NEW_IDS = getResource("new_ids.csv");

    /**
     * Verify log file
     */
    public static final String LOG = getResource("verify.log");

    public static final String[] GENERATE_ARGS = new String[] {
            "generate", CONFIG, OLD_JAR, OLD_INHERITANCE, OLD_TSRG, OLD_IDS
    };

    public static final String[] UPDATE_ARGS = new String[] {
            "update", CONFIG, NEW_JAR, NEW_INHERITANCE, OLD_TSRG, OLD_IDS, MATCH, NEW_TSRG, NEW_IDS
    };

    // Hardcoded paths to avoid null exception funny business because some of the paths are outputs.
    public static String getResource(String path) {
        return "src/test/resources/" + path;
    }
}
