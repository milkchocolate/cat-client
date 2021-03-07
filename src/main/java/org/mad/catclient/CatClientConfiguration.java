package org.mad.catclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesRegistrationAdapter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;

@Configuration
@EnableConfigurationProperties(OAuth2ClientProperties.class)
public class CatClientConfiguration {
    private final String catServerUrl;

    public CatClientConfiguration(
            @Value("${cat-server.url}") String catServerUrl
    ) {
        this.catServerUrl = catServerUrl;
    }

    @Bean
    WebClient webClient(ClientRegistrationRepository clientRegistrationRepository,
                        OAuth2AuthorizedClientRepository authorizedClientRepository) {
        var oauth2 = new ServletOAuth2AuthorizedClientExchangeFilterFunction(
                clientRegistrationRepository,
                authorizedClientRepository);
        oauth2.setDefaultOAuth2AuthorizedClient(true);
        return WebClient.builder().apply(oauth2.oauth2Configuration()).baseUrl(catServerUrl).build();
    }

    @Bean
    InMemoryClientRegistrationRepository clientRegistrationRepository(OAuth2ClientProperties properties) {
        var registrations = new ArrayList<>(
                OAuth2ClientPropertiesRegistrationAdapter.getClientRegistrations(properties).values());
        return new InMemoryClientRegistrationRepository(registrations);
    }

    @Bean
    InMemoryOAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }
}
