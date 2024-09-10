package com.cydeo.controller;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.enums.InvoiceType;
import com.cydeo.service.ClientVendorService;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


//@Controller
@RestController
@RequestMapping("/purchaseInvoices")
public class PurchasesInvoiceController {


    private final InvoiceService invoiceService;
    private final ClientVendorService clientVendorService;
    private final ProductService productService;
    private final InvoiceProductService invoiceProductService;

    public PurchasesInvoiceController(InvoiceService invoiceService, ClientVendorService clientVendorService, ProductService productService, InvoiceProductService invoiceProductService) {
        this.invoiceService = invoiceService;
        this.clientVendorService = clientVendorService;
        this.productService = productService;
        this.invoiceProductService = invoiceProductService;
    }

    @GetMapping("/list")
    public String listAllPurchaseInvoices(Model model){

        model.addAttribute("invoices", invoiceService.listAllInvoicesByType(InvoiceType.PURCHASE));

        return "invoice/purchase-invoice-list";
    }

    @GetMapping("/create")
    public String getPurchaseInvoiceCreatePage(Model model){

        model.addAttribute("newPurchaseInvoice", invoiceService.createNewInvoice(InvoiceType.PURCHASE));
        model.addAttribute("vendors", clientVendorService.findClientVendorsByCompanyId(ClientVendorType.VENDOR));

        return "invoice/purchase-invoice-create";
    }

    @PostMapping("/create")
    public String createPurchaseInvoice(@ModelAttribute("invoiceDto") InvoiceDto invoiceDto,Model model ) {

        Long id = invoiceService.save(invoiceDto,InvoiceType.PURCHASE).getId();

        return "redirect:/purchaseInvoices/update/" + id;
    }

    @GetMapping("/update/{invoiceId}")
    public String updatePurchaseInvoice(@PathVariable("invoiceId") Long id, Model model){

        model.addAttribute("invoice", invoiceService.findById(id));
        model.addAttribute("newInvoiceProduct", new InvoiceProductDto());
        model.addAttribute("vendors", clientVendorService.findClientVendorsByCompanyId(ClientVendorType.VENDOR));
        model.addAttribute("products",productService.listAvailableProducts());
        model.addAttribute("invoiceProducts", invoiceProductService.listAllByInvoiceId(id));

        return "invoice/purchase-invoice-update";
    }

    @PostMapping("/update/{invoiceId}")
    public String saveUpdatedPurchaseInvoice(@PathVariable("invoiceId") Long id,
                                             @ModelAttribute("invoice") InvoiceDto invoice){

        invoice.setId(id);
        invoiceService.update(invoice, InvoiceType.PURCHASE);

        return "redirect:/purchaseInvoices/list";
    }

    @PostMapping("/addInvoiceProduct/{invoiceId}")
    public String addInvoiceProductToInvoice(@PathVariable("invoiceId") Long id,
                                             @Valid @ModelAttribute("newInvoiceProduct")InvoiceProductDto invoiceProductDto,
                                             BindingResult bindingResult,
                                             Model model){


        if (bindingResult.hasErrors()) {
            displayInfo(model,id);
            return "invoice/purchase-invoice-update";
        }

        try{
            invoiceProductService.save(invoiceProductDto, id);
        }catch (Exception e){
            model.addAttribute("error", e.getMessage());
            displayInfo(model,id);
            return "invoice/purchase-invoice-update";
        }

        return "redirect:/purchaseInvoices/update/" + id;
    }

    @GetMapping("/delete/{invoiceId}")
    public String deleteInvoice(@PathVariable("invoiceId") Long id){

        invoiceService.delete(id);

        return "redirect:/purchaseInvoices/list";
    }

    @GetMapping("/print/{invoiceId}")
    public String printInvoice(@PathVariable("invoiceId") Long id, Model model){

        InvoiceDto printingInvoice = invoiceService.findById(id);

        model.addAttribute("company", printingInvoice.getCompany());
        model.addAttribute("invoice", printingInvoice);
        model.addAttribute("invoiceProducts", invoiceProductService.listAllByInvoiceId(id));


        return "invoice/invoice_print";
    }

    @GetMapping("/removeInvoiceProduct/{invoiceId}/{invoiceProuductId}")
    public String removeInvoiceProduct(@PathVariable("invoiceId") Long invoiceId,
                                       @PathVariable("invoiceProuductId") Long invoiceProuductId){

        invoiceProductService.delete(invoiceProuductId);

        return "redirect:/purchaseInvoices/update/" + invoiceId;
    }

    @GetMapping("/approve/{invoiceId}")
    public String approveInvoice(@PathVariable("invoiceId") Long id){

        invoiceService.approve(id);

        return "redirect:/purchaseInvoices/list";
    }

    private void displayInfo(Model model,Long id){
        model.addAttribute("invoice", invoiceService.findById(id));
        model.addAttribute("clients", clientVendorService.findClientVendorsByCompanyId(ClientVendorType.VENDOR));
        model.addAttribute("products", productService.listAvailableProducts());
        model.addAttribute("invoiceProducts", invoiceProductService.listAllByInvoiceId(id));

    }
}
