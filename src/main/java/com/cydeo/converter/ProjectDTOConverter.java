package com.cydeo.converter;

import com.cydeo.dto.ProductDto;
import com.cydeo.service.ProductService;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class ProjectDTOConverter implements Converter<String, ProductDto> {

    private final ProductService productService;

    public ProjectDTOConverter(@Lazy ProductService productService) {
        this.productService = productService;
    }


    @Override
    public ProductDto convert(String id) {
        if (id == null || id.equals("")) {
            return null;
        }
        return productService.findById(Long.parseLong(id));
    }


}
