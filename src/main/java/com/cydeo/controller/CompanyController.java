package com.cydeo.controller;

import com.cydeo.dto.CompanyDto;
import com.cydeo.service.CompanyService;
import com.cydeo.service.impl.CountryFetcher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

//@Controller
@RestController
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService companyService;

    private final CountryFetcher countryFetcher;
    public CompanyController(CompanyService companyService, CountryFetcher countryFetcher) {
        this.companyService = companyService;
        this.countryFetcher = countryFetcher;
    }

    @GetMapping ("/list")
    public String listAll(Model model) {

        //exclude root company "Cydeo"
        List<CompanyDto> companies = companyService.getCompaniesExcluding(1L);

        model.addAttribute("companies", companies);

        return "company/company-list";
    }

    @GetMapping("/create")
    public String createCompany(Model model){

        model.addAttribute("newCompany", new CompanyDto());
        List<String> countries = countryFetcher.fetchCountries();
        model.addAttribute("countries", countries);
        return "company/company-create";
    }

    @PostMapping("/create")
    public String createCompany(@Valid @ModelAttribute("newCompany") CompanyDto companyDto, BindingResult bindingResult){

        if (bindingResult.hasErrors()){
            return "company/company-create";
        }

        try {
            companyService.save(companyDto);
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("title", "error.company", e.getMessage());
            return "company/company-create";
        }

        return "redirect:/companies/list";
    }

    @GetMapping("/update/{id}")
    public String updateCompany(@PathVariable Long id, Model model) {
        CompanyDto companyDto = companyService.findById(id);

        model.addAttribute("company", companyDto);

        List<String> countries = countryFetcher.fetchCountries(); // Fetch countries
        model.addAttribute("countries", countries);
        return "company/company-update";
    }

    @PostMapping("/update/{id}")
    public String updateCompany(@Valid @ModelAttribute("company") CompanyDto companyDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {

            return "company/company-update";
        }

        try {
            companyService.update(companyDto);
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("title", "error.company", e.getMessage());
            return "company/company-update";
        }

        return "redirect:/companies/list";
    }

    @GetMapping ("/activate/{id}")
    public String activateCompany(@PathVariable Long id){

        companyService.activateCompany(id);

        return "redirect:/companies/list";
    }

    @GetMapping("/deactivate/{id}")
    public String deactivateCompany(@PathVariable Long id){

        companyService.deactivateCompany(id);

        return "redirect:/companies/list";
    }

}
