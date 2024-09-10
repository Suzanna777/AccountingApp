package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.enums.InvoiceType;
import com.cydeo.exception.InsufficientStockException;
import com.cydeo.exception.InvoiceNotFoundException;
import com.cydeo.exception.InvoiceProductNotFoundException;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceProductServiceImpl implements InvoiceProductService {

    private final InvoiceProductRepository invoiceProductRepository;
    private final MapperUtil mapperUtil;
    private final InvoiceService invoiceService;

    public InvoiceProductServiceImpl(InvoiceProductRepository invoiceProductRepository, MapperUtil mapperUtil, InvoiceService invoiceService) {
        this.invoiceProductRepository = invoiceProductRepository;
        this.mapperUtil = mapperUtil;
        this.invoiceService = invoiceService;
    }


    @Override
    public InvoiceProductDto findInvoiceProductById(Long id) {
        InvoiceProduct foundInvoiceProduct = invoiceProductRepository.findById(id)
                .orElseThrow(()->new InvoiceProductNotFoundException("Invoice Product cannot be found! Id: " + id));
        return mapperUtil.convert(foundInvoiceProduct, new InvoiceProductDto());
    }

    @Override
    public List<InvoiceProductDto> listAllByInvoiceId(Long invoiceId) {
        List<InvoiceProduct> invoiceProducts = invoiceProductRepository.retrieveAllByInvoice_IdAndIsDeletedFalse(invoiceId);
        return invoiceProducts.stream()
                .map(invoiceProduct ->{
                    InvoiceProductDto invoiceProductDto = mapperUtil.convert(invoiceProduct, new InvoiceProductDto());

                    invoiceProductDto.setTotal(calculateInvoiceProductTotal(invoiceProductDto));

                    return invoiceProductDto;
                })
                .collect(Collectors.toList());

    }


    @Override
    public InvoiceProduct save(InvoiceProductDto invoiceProductDto, Long invoiceId) {
        InvoiceDto invoiceDto = invoiceService.findById(invoiceId);
        if(invoiceDto== null){
            throw new InvoiceNotFoundException("Invoice  cannot be found with id: " + invoiceId);
        }
        invoiceProductDto.setInvoice(invoiceDto);

        if (invoiceProductDto.getId()==null){  // true if the invoiceProduct is being saved for the first time(false when saving inside approve method)
            invoiceProductDto.setProfitLoss(BigDecimal.ZERO);

            if (invoiceDto.getInvoiceType()== InvoiceType.SALES){
                checkStock(invoiceProductDto);
            }
            if(isProductInTheInvoiceProductList(invoiceProductDto.getProduct().getId(),invoiceId)) {
                throw new RuntimeException("Product already added to this invoice.");
            }
        }

        return invoiceProductRepository.save(
                mapperUtil.convert(invoiceProductDto,new InvoiceProduct()));
    }

    @Override
    public void delete(Long invoiceProductId) {
        InvoiceProduct invoiceProduct = invoiceProductRepository.findById(invoiceProductId)
                .orElseThrow(()->new InvoiceProductNotFoundException("Invoice Product to be deleted cannot be found! Id: " + invoiceProductId));

        invoiceProduct.setIsDeleted(true);

        invoiceProductRepository.save(invoiceProduct);
    }

    @Override
    public List<InvoiceProductDto> listRemainingApprovedPurchaseInvoiceProducts(Long productId) {
        List<InvoiceProduct> invoiceProducts = invoiceProductRepository.listRemainingApprovedPurchaseInvoiceProducts(productId);

        return invoiceProducts.stream()
                .map(invoiceProduct ->
                        mapperUtil.convert(invoiceProduct,new InvoiceProductDto()))
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal calculateInvoiceProductTotal(InvoiceProductDto invoiceProductDto) {
        return invoiceProductDto.getPrice()
                .multiply(BigDecimal.valueOf(invoiceProductDto.getQuantity()))
                .multiply(BigDecimal.valueOf(1 + (invoiceProductDto.getTax()/100.0)))
                .setScale(2);
    }
    @Override
    public boolean isProductInTheInvoiceProductList(Long productId, Long invoiceId) {
        return invoiceProductRepository.existsByInvoiceIdAndProductIdAndIsDeletedFalse(invoiceId, productId);

    }

    @Override
    public Object checkStock(InvoiceProductDto invoiceProductDto) {
        if (invoiceProductDto.getQuantity() > invoiceProductDto.getProduct().getQuantityInStock()){
            throw new InsufficientStockException("Not enough "+ invoiceProductDto.getProduct().getName() +" quantity to sell...");
        }
        return null;
    }
}
