package com.cydeo.service;

import com.cydeo.entity.InvoiceProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ReportingService {

    Map<String, BigDecimal> getMonthlyProfitLoss();

    List<InvoiceProduct> getChangesOfStock();

}
