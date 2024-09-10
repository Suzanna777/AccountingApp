package com.cydeo.controller;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.UserDto;
import com.cydeo.service.CompanyService;
import com.cydeo.service.RoleService;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


//@Controller
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final CompanyService companyService;


    public UserController(UserService userService, RoleService roleService, CompanyService companyService) {
        this.userService = userService;
        this.roleService = roleService;
        this.companyService = companyService;
    }

    @GetMapping("/list")
    public String listAllUsers(Model model){

        model.addAttribute("users", userService.listAllUsers());

        return "user/user-list";
    }

    @GetMapping("/create")
    public String createUser(Model model){

        model.addAttribute("newUser", new UserDto());
        model.addAttribute("userRoles", roleService.listAdminRoles());
        model.addAttribute("companies", companyService.getAdminCompanies());

        return "user/user-create";
    }

    @PostMapping("/create")
    public String saveUser(@Valid @ModelAttribute("newUser") UserDto user, BindingResult bindingResult, Model model){

        if(bindingResult.hasErrors()){
            model.addAttribute("userRoles", roleService.listAdminRoles());
            model.addAttribute("companies", companyService.getAdminCompanies());
            return "user/user-create";
        }
        try {
            userService.save(user);
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("username", "error.user", e.getMessage());
            model.addAttribute("userRoles", roleService.listAdminRoles());
            model.addAttribute("companies", companyService.getAdminCompanies());
            return "user/user-create";
        }
        return "redirect:/users/list";
    }

    @GetMapping ("/update/{id}")
    public String editUser(@PathVariable("id") Long id, Model model){
        UserDto userDto = userService.findById(id);
        CompanyDto companyDto = companyService.findById(userDto.getCompany().getId());
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("userRoles", roleService.listAdminRoles());
        model.addAttribute("companies", companyDto);
        return "user/user-update";
    }

    @PostMapping("/update/{id}")
    public String updateUser( @Valid @ModelAttribute("user") UserDto user,
                             BindingResult bindingResult, Model model,@PathVariable("id") Long id) {
        UserDto userDto = userService.findById(id);
        CompanyDto companyDto = companyService.findById(userDto.getCompany().getId());

        if (bindingResult.hasErrors()) {
            model.addAttribute("userRoles", roleService.listAdminRoles());
            model.addAttribute("companies", companyDto);
            return "user/user-update";
        }


        userService.updateUser(user);

        return "redirect:/users/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {

        userService.deleteUser(id);

        return "redirect:/users/list";




    }

}

