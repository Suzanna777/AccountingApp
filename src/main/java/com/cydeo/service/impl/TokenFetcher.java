package com.cydeo.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TokenFetcher {

    private static final Logger logger = LoggerFactory.getLogger(TokenFetcher.class);
    @Value("${api.token.url}")
    private String tokenUrl;

    @Value("${api.token.header}")
    private String apiToken;

    @Value("${api.user.email}")
    private String userEmail;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;
    private String cachedToken;
    private long tokenExpiryTime;
    public TokenFetcher(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String fetchToken() {
        if (cachedToken != null && System.currentTimeMillis() < tokenExpiryTime) {
            logger.info("Returning cached token");
            return cachedToken;
        }
        logger.info("Starting token fetch process");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("api-token", apiToken);
        headers.set("user-email", userEmail);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            String authToken = jsonResponse.path("auth_token").asText(null);

            if (authToken != null && !authToken.trim().isEmpty()) {
                cachedToken = authToken;
                tokenExpiryTime = System.currentTimeMillis() + 3600000;
                logger.info("Token fetched successfully");
                return authToken;
            } else {
                logger.error("Token is null or empty");
                return null;
            }
        } catch (Exception e) {
            logger.error("Error occurred while fetching token", e);
            return null;
        }
    }
}