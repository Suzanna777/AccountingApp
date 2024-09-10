

package com.cydeo.dto.currency_api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyRates {

    @JsonProperty("eur")
    public BigDecimal Euro;

    @JsonProperty("gbp")
    public BigDecimal BritishPound;

    @JsonProperty("cad")
    public BigDecimal CanadianDollar;

    @JsonProperty("jpy")
    public BigDecimal JapaneseYen;

    @JsonProperty("inr")
    public BigDecimal IndianRupee;

}
