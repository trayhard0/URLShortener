package io.github.trayhard0.urlshortener.service;


import io.github.trayhard0.urlshortener.model.Link;
import io.github.trayhard0.urlshortener.repository.LinkRepository;
import io.github.trayhard0.urlshortener.util.ShortCodeGenerator;
import io.github.trayhard0.urlshortener.util.TokenGenerator;
import io.github.trayhard0.urlshortener.util.TokenHasher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class LinkService {
    private  static final int MAX_ATTEMPTS = 3;

    private final LinkRepository linkRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final TokenGenerator tokenGenerator;
    private final TokenHasher tokenHasher;

    private static final Logger log = LoggerFactory.getLogger(LinkService.class);

    public LinkService(LinkRepository linkRepository, ShortCodeGenerator shortCodeGenerator, TokenGenerator tokenGenerator, TokenHasher tokenHasher) {
        this.linkRepository = linkRepository;
        this.shortCodeGenerator = shortCodeGenerator;
        this.tokenGenerator = tokenGenerator;
        this.tokenHasher = tokenHasher;
    }

    public record CreatedLink(Link link, String rawToken) {}

    public CreatedLink createLink(String originalUrl) {
        String rawToken = tokenGenerator.generate();
        String tokenHash = tokenHasher.hash(rawToken);

        for(int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            String code = shortCodeGenerator.generate();

            Instant createdAt = Instant.now();

            Link link = new Link(code, originalUrl, tokenHash, createdAt);

            try {
                Link savedLink = linkRepository.saveAndFlush(link);
                return new CreatedLink(savedLink, rawToken);
            } catch (DataIntegrityViolationException e) {
                log.warn("Short code collision on attempt {} for code {}",attempt,code);
            }
        }
        throw new IllegalStateException("Failed to generate a unique short code after " + MAX_ATTEMPTS + " attempts");
    }

    public Optional<Link> getLinkByCode(String code) {
            return linkRepository.findByCode(code);
    }
}
