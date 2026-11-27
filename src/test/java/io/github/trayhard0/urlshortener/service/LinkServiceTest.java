package io.github.trayhard0.urlshortener.service;

import io.github.trayhard0.urlshortener.exception.BadRequestException;
import io.github.trayhard0.urlshortener.model.Link;
import io.github.trayhard0.urlshortener.repository.LinkRepository;
import io.github.trayhard0.urlshortener.util.ShortCodeGenerator;
import io.github.trayhard0.urlshortener.util.TokenGenerator;
import io.github.trayhard0.urlshortener.util.TokenHasher;
import io.github.trayhard0.urlshortener.util.UrlValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LinkServiceTest {

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private ShortCodeGenerator shortCodeGenerator;

    @Mock
    private TokenGenerator tokenGenerator;

    @Mock
    private TokenHasher tokenHasher;

    @Mock
    private UrlValidator urlValidator;

    @InjectMocks
    private LinkService linkService;

    @Test
    public void shouldCreateLinkSuccessfully() {
        Link expectedLink = new Link("abc1234", "https://example.com", "hashed-token-hex", Instant.now());
        when(shortCodeGenerator.generate()).thenReturn("abc1234");
        when(tokenGenerator.generate()).thenReturn("raw-token-abc");
        when(tokenHasher.hash("raw-token-abc")).thenReturn("hashed-token-hex");
        when(linkRepository.saveAndFlush(any(Link.class))).thenReturn(expectedLink);
        when(urlValidator.isValid("https://example.com")).thenReturn(true);
        LinkService.CreatedLink result = linkService.createLink("https://example.com");
        assertEquals("raw-token-abc", result.rawToken());
        assertEquals("abc1234", result.link().getCode());
        assertEquals("https://example.com", result.link().getOriginalUrl());
    }

    @Test
    public void shouldRetryWhenCodeCollides() {
        Link expectedLink = new Link("SUCCESS", "https://example.com", "hashed-token-hex", Instant.now());
        when(shortCodeGenerator.generate()).thenReturn("COLLIDE","SUCCESS");
        when(tokenGenerator.generate()).thenReturn("raw-token-abc");
        when(tokenHasher.hash("raw-token-abc")).thenReturn("hashed-token-hex");
        when(linkRepository.saveAndFlush(any(Link.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate code"))
                .thenReturn(expectedLink);
        when(urlValidator.isValid("https://example.com")).thenReturn(true);
        LinkService.CreatedLink result = linkService.createLink("https://example.com");

        assertEquals("raw-token-abc", result.rawToken());
        assertEquals("SUCCESS", result.link().getCode());
        assertEquals("https://example.com", result.link().getOriginalUrl());

        verify(shortCodeGenerator, times(2)).generate();
        verify(linkRepository, times(2)).saveAndFlush(any(Link.class));
    }

    @Test
    public void shouldThrowExceptionWhenRetriesExhausted() {
        when(shortCodeGenerator.generate()).thenReturn("COLLIDE1","COLLIDE2","COLLIDE3");
        when(tokenGenerator.generate()).thenReturn("raw-token-abc");
        when(tokenHasher.hash("raw-token-abc")).thenReturn("hashed-token-hex");
        when(linkRepository.saveAndFlush(any(Link.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate code"));

        when(urlValidator.isValid("https://example.com")).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> {
            linkService.createLink("https://example.com");
        });

        verify(shortCodeGenerator, times(3)).generate();
        verify(linkRepository, times(3)).saveAndFlush(any(Link.class));
    }

    @Test
    public void shouldThrowBadRequestExceptionWhenUrlNotValid() {
        when(urlValidator.isValid("not-a-url")).thenReturn(false);
        assertThrows(BadRequestException.class, () -> {
            linkService.createLink("not-a-url");
        });
        verifyNoInteractions(shortCodeGenerator, tokenGenerator, tokenHasher, linkRepository);
    }

    @Test
    public void shouldReturnLinkWhenCodeExists() {
        Link expectedLink = new Link("abc1234", "https://example.com", "hashed-token-hex", Instant.now());
        when(linkRepository.findByCode(expectedLink.getCode())).thenReturn(Optional.of(expectedLink));
        Optional<Link> result = linkService.getLinkByCode("abc1234");
        assertTrue(result.isPresent());
        assertEquals("abc1234", result.get().getCode());
    }

    @Test
    public void shouldReturnEmptyWhenCodeDoesNotExist() {
        when(linkRepository.findByCode("unknown")).thenReturn(Optional.empty());
        Optional<Link> result = linkService.getLinkByCode("unknown");
        assertTrue(result.isEmpty());
    }
}
