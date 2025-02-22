package com.ancientmc.rosetta.test;

import com.ancientmc.rosetta.Rosetta;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class RosettaTest {
    @Test
    public void testGenerate() {
        Rosetta.main(Paths.GENERATE_ARGS);
    }

    @Test
    public void testUpdate() {
        Rosetta.main(Paths.UPDATE_ARGS);
    }

    @Test
    public void verifyGenerate() {
        try {
            Verifier.verify("generate", Paths.OLD_JAR, Paths.OLD_TSRG);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void verifyUpdate() {
        try {
            Verifier.verify("update", Paths.NEW_JAR, Paths.NEW_TSRG);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
