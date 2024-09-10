package com.cydeo.client;

import com.cydeo.annotation.ExecutionTime;
import com.cydeo.dto.currency_api.CurrencyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(url = "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/usd.json", name = "CURRENCY-CLIENT")
public interface CurrencyClient {

    @GetMapping
    @ExecutionTime
    CurrencyResponse getAllCurrencies();

}