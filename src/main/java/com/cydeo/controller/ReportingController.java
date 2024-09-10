package com.cydeo.controller;

import com.cydeo.service.ReportingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

//@Controller
@RestController
@RequestMapping("/reports")
public class ReportingController {

    private final ReportingService reportingService;

    public ReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @GetMapping("/profit-loss-report")
    public String getProfitLossReport(Model model) {
        Map<String, BigDecimal> monthlyProfitLossDataMap = reportingService.getMonthlyProfitLoss();
        model.addAttribute("monthlyProfitLossDataMap", monthlyProfitLossDataMap);
        return "report/profit-loss-report";
    }

    @GetMapping("/stock-report")
    public String getStockReport(Model model) {
        model.addAttribute(("invoiceProducts"), reportingService.getChangesOfStock());
        return "report/stock-report";
    }

}
