package com.cydeo.controller;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.enums.InvoiceType;
import com.cydeo.exception.InsufficientStockException;
import com.cydeo.service.ClientVendorService;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;


//@Controller
@RestController
@RequestMapping("/salesInvoices")
public class SalesInvoiceController {

    private final InvoiceService invoiceService;
    private final ClientVendorService clientVendorService;
    private final ProductService productService;
    private final InvoiceProductService invoiceProductService;

    public SalesInvoiceController(InvoiceService invoiceService, ClientVendorService clientVendorService, ProductService productService, InvoiceProductService invoiceProductService) {
        this.invoiceService = invoiceService;
        this.clientVendorService = clientVendorService;
        this.productService = productService;
        this.invoiceProductService = invoiceProductService;
    }


    @GetMapping("/list")
    public String listAllSalesInvoices(Model model) {

        model.addAttribute("invoices", invoiceService.listAllInvoicesByType(InvoiceType.SALES));

        return "invoice/sales-invoice-list";
    }

    @GetMapping("/create")
    public String getSalesInvoiceCreatePage(Model model) {

        model.addAttribute("newSalesInvoice", invoiceService.createNewInvoice(InvoiceType.SALES));
        model.addAttribute("clients", clientVendorService.findClientVendorsByCompanyId(ClientVendorType.CLIENT));
        return "invoice/sales-invoice-create";
    }

    @PostMapping("/create")
    public String createSalesInvoice(@ModelAttribute("invoiceDto") InvoiceDto invoiceDto, Model model) {

        Long id = invoiceService.save(invoiceDto, InvoiceType.SALES).getId();

        return "redirect:/salesInvoices/update/" + id;
    }

    @GetMapping("/update/{invoiceId}")
    public String updateSalesInvoice(@PathVariable("invoiceId") Long id, Model model) {

        model.addAttribute("invoice", invoiceService.findById(id));
        model.addAttribute("newInvoiceProduct", new InvoiceProductDto());
        model.addAttribute("clients", clientVendorService.findClientVendorsByCompanyId(ClientVendorType.CLIENT));
        model.addAttribute("products", productService.listAvailableProducts());
        model.addAttribute("invoiceProducts", invoiceProductService.listAllByInvoiceId(id));

        return "invoice/sales-invoice-update";
    }

    @PostMapping("/update/{invoiceId}")
    public String saveUpdatedSalesInvoice(@PathVariable("invoiceId") Long id,
                                          @ModelAttribute("invoice") InvoiceDto invoice) {

        invoice.setId(id);
        invoiceService.update(invoice, InvoiceType.SALES);

        return "redirect:/salesInvoices/list";
    }

    @PostMapping("/addInvoiceProduct/{invoiceId}")
    public String addInvoiceProductToInvoice(@PathVariable("invoiceId") Long id,
                                             @Valid @ModelAttribute("newInvoiceProduct") InvoiceProductDto invoiceProductDto,
                                             BindingResult bindingResult,
                                             Model model) {


        if (bindingResult.hasErrors()) {
            displayInfo(model,id);
            return "invoice/sales-invoice-update";
        }

        try{
            invoiceProductService.save(invoiceProductDto, id);
        }catch (Exception e){
            model.addAttribute("error", e.getMessage());
            displayInfo(model,id);
            return "invoice/sales-invoice-update";
        }

        return "redirect:/salesInvoices/update/" + id;
    }

    @GetMapping("/delete/{invoiceId}")
    public String deleteInvoice(@PathVariable("invoiceId") Long id) {

        invoiceService.delete(id);

        return "redirect:/salesInvoices/list";
    }

    @GetMapping("/print/{invoiceId}")
    public String printInvoice(@PathVariable("invoiceId") Long id, Model model) {

        InvoiceDto invoiceDto = invoiceService.findById(id);

        model.addAttribute("company", invoiceDto.getCompany());
        model.addAttribute("invoice", invoiceDto);
        model.addAttribute("invoiceProducts", invoiceProductService.listAllByInvoiceId(id));


        return "invoice/invoice_print";
    }

    @GetMapping("/removeInvoiceProduct/{invoiceId}/{invoiceProductId}")
    public String removeInvoiceProduct(@PathVariable("invoiceId") Long invoiceId,
                                       @PathVariable("invoiceProductId") Long invoiceProductId) {

        invoiceProductService.delete(invoiceProductId);

        return "redirect:/salesInvoices/update/" + invoiceId;
    }

    @GetMapping("/approve/{id}")
    public String approveSalesInvoice(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {

        try {
            invoiceService.approve(id);
        } catch (InsufficientStockException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/salesInvoices/list";
        }

        return "redirect:/salesInvoices/list";
    }

    private void displayInfo(Model model,Long id){
        model.addAttribute("invoice", invoiceService.findById(id));
        model.addAttribute("clients", clientVendorService.findClientVendorsByCompanyId(ClientVendorType.CLIENT));
        model.addAttribute("products", productService.listAvailableProducts());
        model.addAttribute("invoiceProducts", invoiceProductService.listAllByInvoiceId(id));

    }
}

