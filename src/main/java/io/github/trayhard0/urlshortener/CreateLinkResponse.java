package io.github.trayhard0.urlshortener;

import java.time.Instant;

public record CreateLinkResponse(String code, String shortUrl, String originalUrl, Instant expiresAt, String manageToken) {
}
