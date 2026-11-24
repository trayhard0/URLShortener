package io.github.trayhard0.urlshortener.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TokenHasherTest {
    @Test
    void shouldGenerate64CharacterHexadecimal() {
        TokenHasher tokenHasher = new TokenHasher();
        String hash = tokenHasher.hash("test");
        assertTrue(hash.matches("^[0-9a-f]{64}$"));
    }

    @Test
    void shouldGenerateSameHashGivenSameToken() {
        TokenHasher tokenHasher = new TokenHasher();
        String hash1 = tokenHasher.hash("test");
        String hash2 = tokenHasher.hash("test");
        assertEquals(hash1,hash2);
    }

    @Test
    void shouldGenerateDifferentHashesFromDifferentInputs() {
        TokenHasher tokenHasher = new TokenHasher();
        String hash1 = tokenHasher.hash("test1");
        String hash2 = tokenHasher.hash("test2");
        assertNotEquals(hash1,hash2);
    }
}
