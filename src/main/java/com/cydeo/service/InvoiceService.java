package com.cydeo.service;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.Invoice;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;

import java.util.List;

public interface InvoiceService {

    List<InvoiceDto> listAllInvoicesByType(InvoiceType invoiceType);

    InvoiceDto findById(Long id);

    Invoice save(InvoiceDto invoiceDto, InvoiceType invoiceType);

    String generateNextInvoiceNo(InvoiceType invoiceType);

   InvoiceDto createNewInvoice(InvoiceType invoiceType);

    void update(InvoiceDto invoiceDto, InvoiceType invoiceType);

    void delete(Long id);

    void approve(Long id);

    boolean hasInvoicesByClientVendorId(Long clientVendorId);

    List<InvoiceDto> showLastThreeApprovedInvoices(InvoiceStatus invoiceStatus);
    void checkIfInvoiceCanBeApproved(InvoiceProductDto invoiceProductDto);
}
