package com.cydeo.dto;

import com.cydeo.enums.ProductUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long id;

    @NotBlank(message = "{name.notBlank}")
    @Size(min = 2, max = 100, message = "{name.size}")
    private String name;

    private Integer quantityInStock;

    @NotNull(message = "{lowLimitAlert.notnull}")
    @Min(value = 1, message = "{lowLimitAlert.min}")
    private Integer lowLimitAlert;

    @NotNull(message = "{productUnit.notNull}")
    private ProductUnit productUnit;

    @NotNull(message = "{category.notNull}")
    private CategoryDto category;
    private boolean hasInvoiceProduct;

}
