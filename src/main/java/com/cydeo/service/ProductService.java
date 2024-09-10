package com.cydeo.service;

import com.cydeo.dto.ProductDto;

import java.util.List;

public interface ProductService {

    List<ProductDto> listAllProducts();

    ProductDto findById(Long id);

    List<ProductDto> listAvailableProducts();

    void delete(Long id);

    void save(ProductDto product);

    ProductDto update(ProductDto product);

}
