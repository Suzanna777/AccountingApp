package com.cydeo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CountryDto {
    @JsonProperty("country_name")
    private String countryName;

    @JsonProperty("country_short_name")
    private String countryShortName;

    @JsonProperty("country_phone_code")
    private int countryPhoneCode;
}
