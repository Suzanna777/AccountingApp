package com.cydeo.service.impl;

import com.cydeo.client.CountryClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


@Service
public class CountryFetcher {
    private static final Logger logger = LoggerFactory.getLogger(CountryFetcher.class);
    @Value("${api.countries.url}")
    private String countriesUrl;
    private final TokenFetcher tokenFetcher;
    private final RestTemplate restTemplate;
    public CountryFetcher(CountryClient countryClient, TokenFetcher tokenFetcher, RestTemplate restTemplate) {
        this.tokenFetcher = tokenFetcher;
        this.restTemplate = restTemplate;
    }

    public List<String> fetchCountries() {
        List<String> countryNames = new ArrayList<>();
        try {
            String authToken = tokenFetcher.fetchToken();
            if (authToken == null || authToken.isEmpty()) {
                logger.error("Failed to fetch token.");
                return countryNames;
            }

            HttpHeaders authHeaders = new HttpHeaders();
            authHeaders.set("Authorization", "Bearer " + authToken);
            authHeaders.set("Accept", "application/json");
            HttpEntity<String> authEntity = new HttpEntity<>(authHeaders);
            ResponseEntity<String> countriesResponse = restTemplate.exchange(countriesUrl, HttpMethod.GET, authEntity, String.class);
            JSONArray countriesArray = new JSONArray(countriesResponse.getBody());
            for (int i = 0; i < countriesArray.length(); i++) {
                JSONObject countryObject = countriesArray.getJSONObject(i);
                String countryName = countryObject.optString("country_name", "Unknown Country");
                if (!countryName.equals("Unknown Country")) {
                    countryNames.add(countryName);
                }
            }

        } catch (Exception e) {
            logger.error("Error occurred while fetching countries", e);
        }

        return countryNames;
    }
    }



