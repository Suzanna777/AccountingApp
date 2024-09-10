package com.cydeo.controller;


import com.cydeo.enums.InvoiceStatus;
import com.cydeo.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


//@Controller
@RestController
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {

        model.addAttribute("invoices", dashboardService.getLastThreeApprovedInvoices(InvoiceStatus.APPROVED));
        model.addAttribute("exchangeRates", dashboardService.getExchangeRates());
        model.addAttribute("summaryNumbers",dashboardService.getSummaryNumbers());

        return "dashboard";
    }

}
