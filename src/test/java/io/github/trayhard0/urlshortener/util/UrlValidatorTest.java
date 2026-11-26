package io.github.trayhard0.urlshortener.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class UrlValidatorTest {

    private UrlValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UrlValidator("http://localhost:8080");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "http://example.com",
            "https://example.com",
            "https://example.com/path/to/resource",
            "https://example.com?query=param&another=123",
            "https://subdomain.domain.org:8443/test#section",
            "HTTPS://EXAMPLE.COM/UPPERCASE"
    })
    void shouldAcceptValidUrls(String validUrl) {
        assertTrue(validator.isValid(validUrl));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "javascript:alert(1)",
            "file:///etc/passwd",
            "ftp://ftp.example.com/file.txt",
            "data:text/plain;base64,SGVsbG8sIFdvcmxkIQ==",
            "mailto:admin@example.com",
            "ssh://git@github.com"
    })
    void shouldRejectInvalidSchemes(String badSchemeUrl) {
        assertFalse(validator.isValid(badSchemeUrl));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "http://localhost",
            "http://localhost:3000/dashboard",
            "http://127.0.0.1:5432",
            "http://127.0.0.2",
            "http://10.0.0.1/admin",
            "http://172.16.0.1/internal",
            "http://192.168.1.1/router",
            "http://169.254.169.254/latest/meta-data/",
            "http://0.0.0.0",
            "http://[::1]",
            "http://[::1]:8080/admin"
    })
    void shouldRejectLocalhostAndPrivateIps(String ssrfUrl) {
        assertFalse(validator.isValid(ssrfUrl));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://example.com/loopCode",
            "http://example.com:8080/api/links",
            "HTTPS://EXAMPLE.COM/loop",
            "https://example.com./loop",
            "https://example.com../loop"
    })
    void shouldRejectOwnHostRedirectLoop(String loopUrl) {
        UrlValidator customValidator = new UrlValidator("https://example.com");
        assertFalse(customValidator.isValid(loopUrl));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "   ",
            "not-a-url",
            "http://",
            "https://",
            "://missing-scheme.com",
            "http:///no-host",
            "https://example.com/path with spaces",
            "http://./"
    })
    void shouldRejectMalformedUrls(String malformedUrl) {
        assertFalse(validator.isValid(malformedUrl));
    }

    @Test
    void shouldRejectNullUrl() {
        assertFalse(validator.isValid(null));
    }

    @Test
    void shouldRejectUrlExceedingMaxLength() {
        String base = "https://example.com/";
        String longUrl = base + "a".repeat(2049 - base.length());
        assertTrue(longUrl.length() > 2048);
        assertFalse(validator.isValid(longUrl));
    }

    @Test
    void shouldAcceptUrlAtMaxLength() {
        String base = "https://example.com/";
        String maxUrl = base + "a".repeat(2048 - base.length());
        assertEquals(2048, maxUrl.length());
        assertTrue(validator.isValid(maxUrl));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "localhost:8080",
            "http://",
            "http://."
    })
    void shouldThrowForInvalidBaseUrl(String invalidBaseUrl) {
        assertThrows(IllegalArgumentException.class, () -> new UrlValidator(invalidBaseUrl));
    }
}
