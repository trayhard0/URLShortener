package io.github.trayhard0.urlshortener.controller;

import io.github.trayhard0.urlshortener.model.Link;
import io.github.trayhard0.urlshortener.service.LinkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LinkController.class)
public class LinkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LinkService linkService;

    @Test
    void shouldCreateLinkSuccessfully() throws Exception {
        Link fakeLink = new Link("abc1234", "https://example.com", "fakehash", Instant.now());
        LinkService.CreatedLink fakeResult = new LinkService.CreatedLink(fakeLink, "raw-token-123");
        when(linkService.createLink("https://example.com")).thenReturn(fakeResult);

        mockMvc.perform(post("/api/links")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"url\":\"https://example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("abc1234"))
                .andExpect(jsonPath("$.shortUrl").value("http://localhost:8080/abc1234"))
                .andExpect(jsonPath("$.manageToken").value("raw-token-123"));
    }

    @Test
    void shouldReturnBadRequestWhenUrlIsBlank() throws Exception {
        mockMvc.perform(post("/api/links")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"url\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRedirectToOriginalUrlWhenCodeExists() throws Exception {
        Link fakeLink = new Link("abc1234", "https://example.com", "fakehash", Instant.now());
        when(linkService.getLinkByCode(fakeLink.getCode())).thenReturn(Optional.of(fakeLink));
        mockMvc.perform(get("/abc1234"))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, "https://example.com"));
    }

    @Test
    void shouldReturnNotFoundWhenCodeDoesNotExist() throws Exception {
        when(linkService.getLinkByCode("unknown")).thenReturn(Optional.empty());
        mockMvc.perform(get("/unknown"))
                .andExpect(status().isNotFound());
    }
}
