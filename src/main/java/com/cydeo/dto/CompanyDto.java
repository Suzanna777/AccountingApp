package com.cydeo.dto;

import com.cydeo.annotation.UniqueValue;
import com.cydeo.entity.Company;
import com.cydeo.enums.CompanyStatus;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {

    private Long id;

    @UniqueValue(message = "Title must be unique.", entity = Company.class, field = "title")
    @NotBlank(message = "Title is a required field.")
    @Size(max = 100, min = 2, message = "Title must be between 2 and 100 characters.")
    private String title;

    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^(?:\\+1)?\\s?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$", message = "Phone number must be in any valid format")
    private String phone;

    @NotBlank(message = "Website is a required field.")
    @Pattern(regexp = "^(https?://)?([a-zA-Z0-9.-]+)\\.([a-zA-Z]{2,})(:[0-9]{1,5})?(/.*)?$", message = "Website must be a valid format.")
    private String website;

    @Valid
    private AddressDto address;
    private CompanyStatus companyStatus;
}
