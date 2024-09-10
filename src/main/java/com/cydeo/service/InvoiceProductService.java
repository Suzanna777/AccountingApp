package com.cydeo.service;

import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.InvoiceProduct;

import java.math.BigDecimal;
import java.util.List;

public interface InvoiceProductService {

    InvoiceProductDto findInvoiceProductById (Long id);

    List<InvoiceProductDto> listAllByInvoiceId(Long invoiceId);

    InvoiceProduct save(InvoiceProductDto invoiceProductDto, Long invoiceId);

    void delete(Long invoiceProductId);

    List<InvoiceProductDto> listRemainingApprovedPurchaseInvoiceProducts(Long productId);

    BigDecimal calculateInvoiceProductTotal(InvoiceProductDto invoiceProductDto);
    boolean isProductInTheInvoiceProductList(Long productId, Long invoiceId);

    Object checkStock(InvoiceProductDto invoiceProductDto);
}
