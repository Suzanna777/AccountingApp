package com.cydeo.validator;

import com.cydeo.annotation.UniqueValue;
import com.cydeo.entity.Category;
import com.cydeo.entity.Company;
import com.cydeo.repository.CategoryRepository;
import com.cydeo.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class UniqueValueValidator implements ConstraintValidator<UniqueValue, String > {

    private Class<?> entity;
    private String field;

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public void initialize(UniqueValue constraintAnnotation) {
        this.entity = constraintAnnotation.entity();
        this.field = constraintAnnotation.field();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {

        boolean isUnique = true;

        if (value==null){
            return true;
        }

        if (entity.equals(Company.class)) {
            if (companyRepository.existsByTitle(value)) {
                Long id = companyRepository.findByTitle(value).getId();
                isUnique = !companyRepository.existsByTitleIgnoreCaseAndIdNot(value, id);
            }
        } else if (entity.equals(Category.class)) {
            return !categoryRepository.existsByDescription(value);
        } else {
            return false;
        }

        return isUnique;
    }

}
