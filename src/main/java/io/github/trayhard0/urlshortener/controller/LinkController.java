package io.github.trayhard0.urlshortener.controller;


import io.github.trayhard0.urlshortener.dto.CreateLinkRequest;
import io.github.trayhard0.urlshortener.dto.CreateLinkResponse;
import io.github.trayhard0.urlshortener.model.Link;
import io.github.trayhard0.urlshortener.service.LinkService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class LinkController {
    private final LinkService linkservice;
    private final String baseUrl;

    public LinkController(LinkService linkservice, @Value("${app.base-url}") String baseUrl) {
        this.linkservice = linkservice;
        this.baseUrl = baseUrl;
    }

    @PostMapping("/api/links")
    public ResponseEntity<CreateLinkResponse> createLink(@Valid @RequestBody CreateLinkRequest request) {
        LinkService.CreatedLink createdLink  = linkservice.createLink(request.url());
        Link link = createdLink.link();
        String shortUrl = baseUrl + "/" + link.getCode();

        CreateLinkResponse response = new CreateLinkResponse(
                link.getCode(),
                shortUrl,
                link.getOriginalUrl(),
                link.getExpiresAt(),
                createdLink.rawToken());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        Optional<Link> link = linkservice.getLinkByCode(code);
        if(link.isEmpty())
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, link.get().getOriginalUrl()).build();
    }
}
