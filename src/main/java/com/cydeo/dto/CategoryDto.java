package com.cydeo.dto;

import com.cydeo.annotation.UniqueValue;
import com.cydeo.entity.Category;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CategoryDto {

    private Long id;

    @UniqueValue(entity = Category.class, field = "category", message = "Description must be unique.")
    @NotBlank(message = "Description is a required field.")
    @Size(max = 100, min = 2, message = "Description must be between 2 and 100 characters.")
    private String description;
    private CompanyDto company;
    private boolean hasProduct;

}
