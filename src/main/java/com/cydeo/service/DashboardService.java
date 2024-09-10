package com.cydeo.service;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.currency_api.CurrencyRates;
import com.cydeo.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface DashboardService {
    List<InvoiceDto> getLastThreeApprovedInvoices(InvoiceStatus invoiceStatus);

    CurrencyRates getExchangeRates();

    Map<String, BigDecimal> getSummaryNumbers();

}
