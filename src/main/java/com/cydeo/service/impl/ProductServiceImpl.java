package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.Product;
import com.cydeo.exception.ProductNotFoundException;
import com.cydeo.repository.ProductRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.ProductService;
import com.cydeo.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final MapperUtil mapperUtil;
    private final CompanyService companyService;



    public ProductServiceImpl(ProductRepository productRepository, MapperUtil mapperUtil, CompanyService companyService) {
        this.productRepository = productRepository;
        this.mapperUtil = mapperUtil;
        this.companyService = companyService;

    }

    @Override
    public List<ProductDto> listAllProducts() {
        Long companyId = companyService.getCompanyByLoggedInUser().getId();
        List<Product> products = productRepository.findByCompanyIdAndIsDeletedFalseOrderByCategoryAscNameAsc(companyId);
        return products.stream()
                .map(product -> mapperUtil.convert(product, new ProductDto()))
                .collect(Collectors.toList());
    }


    @Override
    public ProductDto findById(Long id) {
        Product foundProduct = productRepository.findByIdAndIsDeleted(id,false);

        if(foundProduct == null){
            throw new ProductNotFoundException("Product not found...");
        }

        return mapperUtil.convert(foundProduct, new ProductDto());
    }

    @Override
    public List<ProductDto> listAvailableProducts() {
        Long companyId = companyService.getCompanyByLoggedInUser().getId();
        List<Product> products = productRepository.retrieveAllAvailableProductsForCompany(companyId);
        return products.stream()
                .map(product ->
                        mapperUtil.convert(product, new ProductDto()))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        Product product = productRepository.findByIdAndIsDeleted(id,false);
        if (product == null) {
            throw new ProductNotFoundException("Product cannot be found or has already been deleted! Id: " + id);
        }
        if(checkIfProductCanBeDeleted(product)) {
            product.setIsDeleted(true);
            product.setName(product.getName() + "-" + product.getId());
            productRepository.save(product);
        }
    }


    @Override
    public void save(ProductDto product) {
        CompanyDto loggedUserCompany = companyService.getCompanyByLoggedInUser();

        Product newProduct= mapperUtil.convert(product, new Product());
        newProduct.setIsDeleted(false);
        newProduct.setCompany((mapperUtil.convert(loggedUserCompany, new Company())));
        productRepository.save(newProduct);
    }

    @Override
    public ProductDto update(ProductDto product) {

        CompanyDto loggedUserCompany = companyService.getCompanyByLoggedInUser();
        Product foundProduct = productRepository.findByIdAndIsDeleted(product.getId(), false);

        if (foundProduct == null) {
            throw new ProductNotFoundException("Product cannot be found or has already been deleted! Id: " + product.getId());
        }

        ProductDto convertedProductDto = mapperUtil.convert(foundProduct, new ProductDto());
        convertedProductDto.setName(product.getName());
        convertedProductDto.setId(product.getId());
        convertedProductDto.setCategory(product.getCategory());
        convertedProductDto.setProductUnit(product.getProductUnit());
        convertedProductDto.setLowLimitAlert(product.getLowLimitAlert());

        Product convertedProduct = mapperUtil.convert(convertedProductDto, new Product());
        convertedProduct.setCompany((mapperUtil.convert(loggedUserCompany, new Company())));

        productRepository.save(convertedProduct);
        return findById(convertedProduct.getId());
    }

    private boolean checkIfProductCanBeDeleted(Product product){
        ProductDto productDto = findById(product.getId());
        if( productDto.isHasInvoiceProduct() || productDto.getQuantityInStock() > 1 ){
            return false;
        }else {
            return true;
        }
    }




}
