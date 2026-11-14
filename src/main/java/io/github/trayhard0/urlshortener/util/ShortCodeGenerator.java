package io.github.trayhard0.urlshortener.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ShortCodeGenerator {
    private static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    private static final int CODE_LENGTH = 7;
    private final SecureRandom secureRandom = new SecureRandom();

    public String generate() {
        StringBuilder shortCode = new StringBuilder(CODE_LENGTH);
        for(int x = 0; x < CODE_LENGTH; x++) {
            shortCode.append(getNextChar());
        }
        return shortCode.toString();
    }

    private char getNextChar() {
        return ALPHABET[secureRandom.nextInt(ALPHABET.length)];
    }
}
