package com.cydeo.client;

import com.cydeo.dto.CountryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "countryClient", url = "https://www.universal-tutorial.com/api")
public interface CountryClient {
    @GetMapping("/countries")
    List<CountryDto> getCountries(@RequestHeader("Authorization") String token);

}


