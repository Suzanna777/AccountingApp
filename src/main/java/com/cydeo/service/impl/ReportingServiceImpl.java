package com.cydeo.service.impl;

import com.cydeo.entity.InvoiceProduct;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.ReportingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportingServiceImpl implements ReportingService {

    private final InvoiceProductRepository invoiceProductRepository;
    private final CompanyService companyService;

    public ReportingServiceImpl(InvoiceProductRepository invoiceProductRepository, CompanyService companyService) {
        this.invoiceProductRepository = invoiceProductRepository;
        this.companyService = companyService;
    }

    @Override
    public Map<String, BigDecimal> getMonthlyProfitLoss() {
        Long companyId = companyService.getCompanyByLoggedInUser().getId();

        List<InvoiceProduct> approvedInvoices = invoiceProductRepository.findAllApprovedInvoices(companyId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

        return approvedInvoices.stream()
                .collect(Collectors.groupingBy(
                        invoice -> YearMonth.from(invoice.getInvoice().getDate()).format(formatter),
                        Collectors.reducing(BigDecimal.ZERO, InvoiceProduct::getProfitLoss, BigDecimal::add)
                ));
    }

    @Override
    public List<InvoiceProduct> getChangesOfStock() {
        Long companyId = companyService.getCompanyByLoggedInUser().getId();
        return invoiceProductRepository.findAllProductsOfApprovedInvoices(companyId);

    }
}



