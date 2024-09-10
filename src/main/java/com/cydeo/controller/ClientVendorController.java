package com.cydeo.controller;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.service.ClientVendorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.List;

//@Controller
@RestController
@RequestMapping("/clientVendors")
public class ClientVendorController {

    private final ClientVendorService clientVendorService;

    public ClientVendorController(ClientVendorService clientVendorService) {
        this.clientVendorService = clientVendorService;
    }

    @GetMapping("/list")
    public String listAllClientVendors(Model model) {
        model.addAttribute("clientVendors", clientVendorService.findClientVendorsByCompanyIdAndClientVendorType());
        return "/clientVendor/clientVendor-list";
    }

    @GetMapping("/create")
    public String createClientVendor(Model model) {
        model.addAttribute("newClientVendor", new ClientVendorDto());
        model.addAttribute("clientVendorTypes", List.of(ClientVendorType.values()));
        return "/clientVendor/clientVendor-create";
    }

    @PostMapping("create")
    public String createClientVendor(@Valid @ModelAttribute("newClientVendor") ClientVendorDto clientVendor, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("clientVendorTypes", List.of(ClientVendorType.values()));
            return "/clientVendor/clientVendor-create";
        }
        clientVendorService.saveClientVendor(clientVendor);
        return "redirect:/clientVendors/list";
    }

    @GetMapping("/update/{clientVendorId}")
    public String updateClientVendor(@PathVariable("clientVendorId") Long clientVendorId, Model model) {

        model.addAttribute("clientVendor", clientVendorService.findById(clientVendorId));
        model.addAttribute("clientVendorTypes", List.of(ClientVendorType.values()));
        return "/clientVendor/clientVendor-update";
    }

    @PostMapping("/update/{clientVendorId}")
    public String saveUpdatedClientVendor(@Valid @PathVariable("clientVendorId") Long clientVendorId, @Valid @ModelAttribute("clientVendor") ClientVendorDto clientVendor, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("clientVendorTypes", List.of(ClientVendorType.values()));
            return "/clientVendor/clientVendor-update";
        }
        clientVendor.setId(clientVendorId);
        clientVendorService.updateClientVendor(clientVendor);
        return "redirect:/clientVendors/list";
    }

    @GetMapping("/delete/{clientVendorId}")
    public String deleteClientVendor(@PathVariable("clientVendorId") Long id) {

        clientVendorService.deleteClientVendor(id);

        return "redirect:/clientVendors/list";
    }


}
