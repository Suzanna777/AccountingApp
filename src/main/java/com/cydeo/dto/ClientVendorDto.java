package com.cydeo.dto;

import com.cydeo.enums.ClientVendorType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@Getter
@Setter
@NoArgsConstructor
public class ClientVendorDto {
    private Long id;

    @NotBlank(message = "Name is a required field.")
    @Size(max = 50, min = 2, message = "Name must be between 2 and 50 characters.")
    private String clientVendorName;

    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^(?:\\+1)?\\s?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$", message = "Phone Number is required field and may be in any valid phone number format.")
    private String phone;

    @NotBlank(message = "Website is a required field.")
    @Pattern(regexp = "^(https?://)?([a-zA-Z0-9.-]+)\\.([a-zA-Z]{2,})(:[0-9]{1,5})?(/.*)?$", message = "Website should have a valid format.")
    private String website;

    @NotNull(message = "Please select type.")
    private ClientVendorType clientVendorType;

    @Valid
    private AddressDto address;

    @Valid
    private CompanyDto company;

    private boolean hasInvoice;
}
