package org.mad.catclient;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class CatController {
    private final WebClient webClient;
    private final InMemoryOAuth2AuthorizedClientService authorizedClientService;

    public CatController(WebClient webClient, InMemoryOAuth2AuthorizedClientService authorizedClientService) {
        this.webClient = webClient;
        this.authorizedClientService = authorizedClientService;
    }

    @GetMapping("/counter")
    public Object getCounter(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        logTokens(oAuth2AuthenticationToken);
        return Objects.requireNonNull(webClient
                .get()
                .uri("/counter")
                .retrieve()
                .toEntity(Object.class)
                .block())
                .getBody();
    }

    @GetMapping("/cats")
    public List<Cat> getItems(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        logTokens(oAuth2AuthenticationToken);
        return Objects.requireNonNull(webClient
                .get()
                .uri("/cats")
                .retrieve()
                .toEntityList(Cat.class)
                .block())
                .getBody();
    }

    @PostMapping("/cats")
    public ResponseEntity<Cat> createCat(
            OAuth2AuthenticationToken oAuth2AuthenticationToken,
            @RequestBody Cat cat
    ) {
        logTokens(oAuth2AuthenticationToken);
        var respondedCat = Objects.requireNonNull(webClient
                .post()
                .uri("/cats")
                .bodyValue(cat)
                .retrieve()
                .toEntity(Cat.class)
                .block())
                .getBody();
        return ResponseEntity.created(URI.create("?")).body(respondedCat);
    }

    @GetMapping("/sent-headers")
    public Map getSentHeaders(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        logTokens(oAuth2AuthenticationToken);
        return Objects.requireNonNull(webClient
                .get()
                .uri("/headers")
                .retrieve()
                .toEntity(Map.class)
                .block())
                .getBody();
    }

    private void logTokens(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        var authorizedClient = authorizedClientService.loadAuthorizedClient(
                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId(),
                oAuth2AuthenticationToken.getName()
        );

        System.out.println("Refresh Token: ");
        System.out.println(Objects.requireNonNull(authorizedClient.getRefreshToken()).getTokenValue());

        System.out.println("Access Token: ");
        System.out.println(authorizedClient.getAccessToken().getTokenValue());
    }
}
