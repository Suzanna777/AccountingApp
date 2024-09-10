package com.cydeo.service.impl;

import com.cydeo.client.CountryClient;
import com.cydeo.dto.CountryDto;
import com.cydeo.service.CountryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CountryServiceImpl implements CountryService {

    private static final Logger logger = LoggerFactory.getLogger(CountryServiceImpl.class);
    private final CountryClient countryClient;
    private final TokenFetcher tokenFetcher;

 //   private static final String AUTH_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7InVzZXJfZW1haWwiOiJhbWVyaWNhbmRyZWFtZG9vckBnbWFpbC5jb20iLCJhcGlfdG9rZW4iOiJCc1VPbmdLMzI2SzJnMk5UM3BJOFZUNXpZLXJIb1RqZVRFZlloZGxubXZNdVJxb2ZNcExRM3ZDclExV0Zwb2tFSm9vIn0sImV4cCI6MTcyMzE1NzE1NX0.U7aFwgXJBBN-f6-KiDdwnH4ptmWn0iYIRyr9zseCwLQ";

    public CountryServiceImpl(CountryClient countryClient, TokenFetcher tokenFetcher) {
        this.countryClient = countryClient;
        this.tokenFetcher = tokenFetcher;
    }

    @Override
    public List<String> getCountries() {
        String token = tokenFetcher.fetchToken();
        if (token == null || token.trim().isEmpty()) {
            logger.error("Failed to fetch token!!!. And Cannot proceed with fetching countries.");
            return Collections.emptyList();
        }
        try {
            List<CountryDto> countryDtos = countryClient.getCountries("Bearer " + token);
            if (countryDtos == null) {
                logger.error("If Failed to fetch country data. Or if  Response is null.");
                return Collections.emptyList();
            }
            List<String> countryNames = countryDtos.stream()
                    .map(CountryDto::getCountryName)
                    .sorted(String::compareToIgnoreCase)
                    .collect(Collectors.toList());
            if (countryNames.contains("United States")) {
                countryNames.remove("United States");
                countryNames.add(0, "United States");
            }
            logger.info("Fetched countries: {}", countryNames);
            return countryNames;
        } catch (Exception e) {
            logger.error("Error occurred when fetching countries", e);
            return Collections.emptyList();
        }
    }
}


