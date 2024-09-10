package com.cydeo.controller;

import com.cydeo.entity.Company;
import com.cydeo.service.CountryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//@Controller
@RestController
@RequestMapping("/country")
public class CountryController {
    private final CountryService countryService;
    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping("/list")
    public String listCountries(Model model) {
        List<String> countries = countryService.getCountries();
        model.addAttribute("countries", countries);
        return "company/company-create";
    }

    @GetMapping("/create")
    public String createCompanyForm(Model model) {
        List<String> countries = countryService.getCountries();
        model.addAttribute("newCompany", new Company());
        model.addAttribute("countries", countries);
        return "company/company-create";
    }
}