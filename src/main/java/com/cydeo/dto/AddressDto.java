package com.cydeo.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

     private Long id;

     @NotBlank(message = "Address Line 1 is a required field.")
     @Size(max = 100, min = 2, message = "Address Line 1 must be between 2 and 100 characters.")
     private String addressLine1;

     @Size(max = 100, message = "Address Line 2 should have maximum 100 characters.")
     private String addressLine2;

     @NotBlank(message = "City is a required field.")
     @Size(max = 50, min = 2, message = "City must be between 2 and 50 characters.")
     private String city;

     @NotBlank(message = "State is a required field.")
     @Size(max = 50, min = 2, message = "State must be between 2 and 50 characters.")
     private String state;

     @NotBlank(message = "Country is a required field.")
     @Size(max = 50, min = 2, message = "Country must be between 2 and 50 characters.")
     private String country;

     @NotBlank(message = "Zip code is a required field.")
     @Pattern(regexp = "^\\d{5}-\\d{4}$", message = "Zip code must have a valid format. Ex. 00000-0000")
     private String zipCode;
}
