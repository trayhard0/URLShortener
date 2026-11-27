package io.github.trayhard0.urlshortener.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AliasValidatorTest {
    private AliasValidator validator;

    @BeforeEach
    void setup() {
        validator = new AliasValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "abc",
            "my-link",
            "my_link_123",
            "my-admin",
            "123456789012345678901234567890"
    })
    void shouldAcceptValidAliases(String validAlias) {
        assertTrue(validator.isValid(validAlias));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "a",
            "ab",
            "1234567890123456789012345678901"
    })
    void shouldRejectInvalidLengths(String badLengthAlias) {
        assertFalse(validator.isValid(badLengthAlias));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "my link",
            "   ",
            "my.link",
            "my/link",
            "my@link",
            "link!"
    })
    void shouldRejectInvalidCharacters(String badCharAlias) {
        assertFalse(validator.isValid(badCharAlias));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "api",
            "health",
            "admin",
            "static",
            "actuator",
            "ADMIN",
            "Api"
    })
    void shouldRejectReservedWords(String reservedAlias) {
        assertFalse(validator.isValid(reservedAlias));
    }

    @Test
    void shouldRejectNullAlias() {
        assertFalse(validator.isValid(null));
    }
}
