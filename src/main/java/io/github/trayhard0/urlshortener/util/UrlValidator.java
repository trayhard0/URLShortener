package io.github.trayhard0.urlshortener.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

@Component
public class UrlValidator {
    private static final int MAX_LENGTH = 2048;
    private final String ownHost;
    private static final Pattern TRAILING_DOTS = Pattern.compile("\\.+$");


    public UrlValidator(@Value("${app.base-url}") String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("Invalid app.base-url: must not be null or blank");
        }
        URI uri = URI.create(baseUrl);
        String host = normalizeHost(uri.getHost());
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("Invalid app.base-url: must contain a valid scheme and host: " + baseUrl);
        }
        this.ownHost = host;
    }

    private String normalizeHost(String host) {
        if (host == null) return null;
        return TRAILING_DOTS.matcher(host).replaceAll("");
    }

    public boolean isValid(String url) {
        if (isNull(url) || isOverMaxLength(url))
            return false;

        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            return false;
        }

        String scheme = uri.getScheme();
        if (!isValidScheme(scheme))
            return false;

        String host = normalizeHost(uri.getHost());
        if (!isValidHost(host))
            return false;

        if (isPrivateOrLoopback(host))
            return false;

        return true;
    }

    private boolean isNull(String url) {
        return url == null;
    }

    private boolean isOverMaxLength(String url) {
        return url != null && url.length() > MAX_LENGTH;
    }

    private boolean isValidScheme(String scheme) {
        return "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
    }

    private boolean isValidHost(String host) {
        return host != null
                && !host.isBlank()
                && !host.equalsIgnoreCase(this.ownHost)
                && !host.equalsIgnoreCase("localhost");
    }

    private boolean isPrivateOrLoopback(String host) {
        try {
            InetAddress[] addresses = InetAddress.getAllByName(host);
            for (InetAddress address : addresses) {
                if (address.isLoopbackAddress()
                        || address.isAnyLocalAddress()
                        || address.isSiteLocalAddress()
                        || address.isLinkLocalAddress()) {
                    return true;
                }
            }
            return false;
        } catch (UnknownHostException e) {
            return false;
        }
    }

}
