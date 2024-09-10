package com.cydeo.service.impl;

import com.cydeo.dto.CategoryDto;
import com.cydeo.entity.Category;
import com.cydeo.exception.CategoryNotFoundException;
import com.cydeo.repository.CategoryRepository;
import com.cydeo.repository.ProductRepository;
import com.cydeo.service.CategoryService;
import com.cydeo.service.CompanyService;
import com.cydeo.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final MapperUtil mapperUtil;

    private final CompanyService companyService;


    private final ProductRepository productRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, MapperUtil mapperUtil, CompanyService companyService, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.mapperUtil = mapperUtil;
        this.companyService = companyService;
        this.productRepository = productRepository;
    }

    @Override
    public CategoryDto findById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException("Category with id " + id + " not found"));
        CategoryDto categoryDto = mapperUtil.convert(category, new CategoryDto());
        categoryDto.setHasProduct(!productRepository.retrieveAllAvailableProducts(categoryDto.getId()).isEmpty());
        return categoryDto;
    }


    @Override
    public List<CategoryDto> listAllCategories() {
        Long companyId = companyService.getCompanyByLoggedInUser().getId();

        List<Category> categories = categoryRepository.findAllByCompanyIdAndIsDeleted(companyId, false);

        List<Category> sortedCategoriesInAsc = categories.stream()
                .sorted(Comparator.comparing(Category::getDescription))
                .toList();

        return sortedCategoriesInAsc.stream()
                .map(category -> {
                    CategoryDto categoryDto = mapperUtil.convert(category, new CategoryDto());
                    categoryDto.setHasProduct(!productRepository.retrieveAllAvailableProducts(categoryDto.getId()).isEmpty());
                    return categoryDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void save(CategoryDto dto) {
        dto.setCompany(companyService.getCompanyByLoggedInUser());
        Category category = mapperUtil.convert(dto, new Category());

        categoryRepository.save(category);

    }

    @Override
    public void update(CategoryDto categoryDto) {

        Category categoryInDB = categoryRepository.findById(categoryDto.getId()).orElseThrow(() -> new CategoryNotFoundException("Category with id " + categoryDto.getId()+ " not found"));

        categoryInDB.setDescription(categoryDto.getDescription());

       Category savedCategory = categoryRepository.save(categoryInDB);


    }




    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findByIdAndIsDeleted(id,false);
        if(checkIfCategoryCanBeDeleted(category)) {
            category.setIsDeleted(true);
            category.setDescription(category.getDescription() + "-" + category.getId());
            categoryRepository.save(category);
        }
    } 



    private boolean checkIfCategoryCanBeDeleted(Category category){
        CategoryDto categoryDto = findById(category.getId());
        categoryDto.setHasProduct(!productRepository.retrieveAllAvailableProducts(categoryDto.getId()).isEmpty());
        if( categoryDto.isHasProduct()){
            return false;
        }else{
            return true;
        }
    }
}
