package io.github.trayhard0.urlshortener.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShortCodeGeneratorTest {
    @Test
    void shouldGenerateCodeOfLengthSeven() {
        ShortCodeGenerator shortCodeGenerator = new ShortCodeGenerator();
        String code = shortCodeGenerator.generate();
        assertEquals(7, code.length());
    }

    @Test
    void shouldOnlyContainBase62Characters() {
        ShortCodeGenerator shortCodeGenerator = new ShortCodeGenerator();
        String code = shortCodeGenerator.generate();
        boolean matches = code.matches("^[a-zA-Z0-9]+$");
        assertTrue(matches);
    }

    @Test
    void shouldGenerateDifferentCodesConsecutively() {
        ShortCodeGenerator shortCodeGenerator = new ShortCodeGenerator();
        String code1 = shortCodeGenerator.generate();
        String code2 = shortCodeGenerator.generate();
        assertNotEquals(code1,code2);
    }
}
