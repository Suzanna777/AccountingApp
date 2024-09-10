package com.cydeo.service;

import com.cydeo.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto findById(Long id);

    List<CategoryDto> listAllCategories();

    void save(CategoryDto categoryDto);

    void update(CategoryDto categoryDto);

    void delete(Long id);
}
