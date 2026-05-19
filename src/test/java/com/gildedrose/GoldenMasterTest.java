package com.gildedrose;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GoldenMasterTest {
    private static final String GOLDEN_MASTER_RESOURCE = "/golden_master_expected.txt";
    private static final String FIXTURE_DAYS = "30";

    @DisplayName("TexttestFixture 출력은 Golden Master와 동일해야 한다")
    @Test
    void texttestFixtureOutputMatchesGoldenMaster() throws Exception {
        String expected = readGoldenMaster();
        String actual = captureTexttestFixtureOutput(FIXTURE_DAYS);

        assertEquals(normalizeLineEndings(expected), normalizeLineEndings(actual));
    }

    private String readGoldenMaster() throws Exception {
        try (InputStream inputStream = getClass().getResourceAsStream(GOLDEN_MASTER_RESOURCE)) {
            if (inputStream == null) {
                throw new IllegalStateException("Golden Master resource not found: " + GOLDEN_MASTER_RESOURCE);
            }
            byte[] bytes = inputStream.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

    private String captureTexttestFixtureOutput(String days) throws Exception {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (PrintStream capturedOut = new PrintStream(outputStream, true, StandardCharsets.UTF_8.name())) {
            System.setOut(capturedOut);
            TexttestFixture.main(new String[] { days });
        } finally {
            System.setOut(originalOut);
        }

        return outputStream.toString(StandardCharsets.UTF_8.name());
    }

    private String normalizeLineEndings(String value) {
        return value.replace("\r\n", "\n");
    }
}
