package com.cydeo.controller;

import com.cydeo.dto.CategoryDto;
import com.cydeo.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


// @Controller
@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/list")
    public String listCategories(Model model){

        model.addAttribute("categories", categoryService.listAllCategories());

        return "category/category-list";
    }

    @GetMapping("/create")
    public String createCategory(Model model){

        model.addAttribute("newCategory", new CategoryDto());
        return "category/category-create";
    }

    @PostMapping("create")
    public String createCategory(@Valid @ModelAttribute("newCategory")CategoryDto categoryDto, BindingResult bindingResult, Model model){

        if (bindingResult.hasErrors()){
            return "category/category-create";
        }

        categoryService.save(categoryDto);
        return "redirect:/categories/list";
    }

    @GetMapping("/update/{id}")
    public String updateCategory(@PathVariable Long id, Model model) {
        CategoryDto categoryDto = categoryService.findById(id);

        model.addAttribute("category", categoryDto);

        return "category/category-update";
    }

    @PostMapping("/update/{id}")
    public String updateCategory(@Valid @ModelAttribute("category") CategoryDto categoryDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {

            return "category/category-update";
        }

        categoryService.update(categoryDto);

        return "redirect:/categories/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id) {
        categoryService.delete(id);
        return "redirect:/categories/list";
    }
}
