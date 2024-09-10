
package com.cydeo.dto.currency_api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyResponse {

    @JsonProperty("date")
    private String date;
    @JsonProperty("usd")
    private CurrencyRates currencyRates;

}