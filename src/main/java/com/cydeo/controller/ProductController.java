package com.cydeo.controller;

import com.cydeo.annotation.ExecutionTime;
import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Category;
import com.cydeo.enums.ProductUnit;
import com.cydeo.repository.CategoryRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.List;

//@Controller
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final CompanyService companyService;


    public ProductController(ProductService productService, CategoryRepository categoryRepository, CompanyService companyService) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
        this.companyService = companyService;
    }

    @ExecutionTime
    @GetMapping("/list")
    public String listProducts(Model model) {
        model.addAttribute("products", productService.listAllProducts());
        return "/product/product-list";
    }

    @ExecutionTime
    @GetMapping("/create")
    public String createProduct(Model model) {
        List<ProductUnit> productUnits = List.of(ProductUnit.values());
        Long companyId = companyService.getCompanyByLoggedInUser().getId();
        List<Category> categories = categoryRepository.findAllByCompanyId(companyId);
        model.addAttribute("newProduct", new ProductDto());
        model.addAttribute("categories", categories);
        model.addAttribute("productUnits", productUnits);
        return "/product/product-create";
    }

    @ExecutionTime
    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute("newProduct")  ProductDto product, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            List<ProductUnit> productUnits = List.of(ProductUnit.values());
            Long companyId = companyService.getCompanyByLoggedInUser().getId();
            List<Category> categories = categoryRepository.findAllByCompanyId(companyId);
            model.addAttribute("categories", categories);
            model.addAttribute("productUnits", productUnits);
            return "/product/product-create";
        }
        productService.save(product);
        return "redirect:/products/list";
    }

    @GetMapping("/update/{productId}")
    public String updateProduct(@PathVariable("productId") Long productId, Model model) {

        model.addAttribute("product", productService.findById(productId));

        List<ProductUnit> productUnits = List.of(ProductUnit.values());
        Long companyId = companyService.getCompanyByLoggedInUser().getId();

        List<Category> categories = categoryRepository.findAllByCompanyId(companyId);
        model.addAttribute("categories", categories);
        model.addAttribute("productUnits", productUnits);
        return "/product/product-update";
    }

    @PostMapping("/update/{productId}")
    public String updateProduct(@Valid @ModelAttribute("product") ProductDto product, BindingResult bindingResult, Model model, @PathVariable String productId) {

        if (bindingResult.hasErrors()) {
            List<ProductUnit> productUnits = List.of(ProductUnit.values());
            Long companyId = companyService.getCompanyByLoggedInUser().getId();
            List<Category> categories = categoryRepository.findAllByCompanyId(companyId);
            model.addAttribute("categories", categories);
            model.addAttribute("productUnits", productUnits);
            return "/product/product-update";
        }
        product.setId(Long.valueOf(productId));
        productService.update(product);
        return "redirect:/products/list";
    }


    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        productService.delete(id);
        return "redirect:/products/list";
    }


}
