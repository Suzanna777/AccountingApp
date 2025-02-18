package com.cydeo.converter;


import com.cydeo.dto.InvoiceDto;
import com.cydeo.service.InvoiceService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class InvoiceDTOConverter implements Converter<String, InvoiceDto> {

   public final InvoiceService invoiceService;


    public InvoiceDTOConverter(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    // @Override
    // public InvoiceDto convert(Long source) {
    //     return invoiceService.findById(source);
    // }


    @Override
    public InvoiceDto convert(String source) {
        if (source==null || source.isEmpty()) {
            return null;
        }
        return invoiceService.findById(Long.parseLong(source));
    }
}
