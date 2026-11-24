package io.github.trayhard0.urlshortener.util;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TokenGeneratorTest {
    @Test
    void shouldGenerate32RandomBytes() {
        TokenGenerator tokenGenerator = new TokenGenerator();
        String token = tokenGenerator.generate();
        byte[] decodedBytes = Base64.getUrlDecoder().decode(token);
        assertEquals(32, decodedBytes.length);
    }

    @Test
    void shouldGenerateTokenOfLength43() {
        TokenGenerator tokenGenerator = new TokenGenerator();
        String token = tokenGenerator.generate();
        assertEquals(43, token.length());
    }

    @Test
    void shouldGenerateDifferentTokensConsecutively() {
        TokenGenerator tokenGenerator = new TokenGenerator();
        String token1 = tokenGenerator.generate();
        String token2 = tokenGenerator.generate();
        assertNotEquals(token1, token2);
    }
}
