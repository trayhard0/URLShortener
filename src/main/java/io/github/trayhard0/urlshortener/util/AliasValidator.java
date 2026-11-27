package io.github.trayhard0.urlshortener.util;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class AliasValidator {
    private static final int MINIMUM_LENGTH = 3;
    private static final int MAXIMUM_LENGTH = 30;
    private static final Pattern VALID_CHARS = Pattern.compile("^[a-zA-Z0-9_-]+$");
    private static final Set<String> RESERVED_WORDS = Set.of("api", "health", "admin", "static", "actuator");

    public boolean isValid(String alias) {
        if (alias == null)
            return false;
        if (alias.length() < MINIMUM_LENGTH || alias.length() > MAXIMUM_LENGTH)
            return false;
        if (RESERVED_WORDS.contains(alias.toLowerCase(Locale.ROOT)))
            return false;
        return VALID_CHARS.matcher(alias).matches();
    }
}
