package io.github.trayhard0.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateLinkRequest(@NotBlank String url) {
}
