package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Invoice;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.exception.InvoiceNotFoundException;
import com.cydeo.exception.InvoiceProductNotFoundException;
import com.cydeo.exception.ProductLowLimitAlertException;
import com.cydeo.exception.ProductNotFoundException;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.*;
import com.cydeo.util.MapperUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final MapperUtil mapperUtil;
    private final CompanyService companyService;
    private final InvoiceProductService invoiceProductService;
    private final ProductService productService;
    private final InvoiceProductRepository invoiceProductRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, MapperUtil mapperUtil, CompanyService companyService, UserService userService, @Lazy InvoiceProductService invoiceProductService, ProductService productService, InvoiceProductRepository invoiceProductRepository) {
        this.invoiceRepository = invoiceRepository;
        this.mapperUtil = mapperUtil;
        this.companyService = companyService;
        this.invoiceProductService = invoiceProductService;
        this.productService = productService;
        this.invoiceProductRepository = invoiceProductRepository;
    }


    @Override
    public List<InvoiceDto> listAllInvoicesByType(InvoiceType invoiceType) {
        Long companyId = companyService.getCompanyByLoggedInUser().getId();

        List<Invoice> invoiceList = invoiceRepository.listAllByInvoiceTypeAndCompanyId(invoiceType, companyId);

        return invoiceList.stream()
                .map(invoice -> {
                    InvoiceDto invoiceDto = mapperUtil.convert(invoice, new InvoiceDto());

                    BigDecimal totalPrice = calculatePriceByInvoiceId(invoiceDto.getId());

                    BigDecimal totalTax = calculateTotalTaxByInvoiceId(invoiceDto.getId());

                    invoiceDto.setPrice(totalPrice);
                    invoiceDto.setTax(totalTax.setScale(2));
                    invoiceDto.setTotal(totalPrice.add(totalTax).setScale(2));


                    return invoiceDto;
                })
                .collect(Collectors.toList());
    }


    @Override
    public InvoiceDto findById(Long id) {
        Invoice foundInvoice = invoiceRepository.findInvoiceById(id).orElseThrow(() -> new InvoiceNotFoundException("No Invoice Found!"));
        InvoiceDto invoiceDto = mapperUtil.convert(foundInvoice, new InvoiceDto());

        BigDecimal price = calculatePriceByInvoiceId(invoiceDto.getId());
        BigDecimal totalTax = calculateTotalTaxByInvoiceId(invoiceDto.getId());

        invoiceDto.setPrice(price);
        invoiceDto.setTax(totalTax);
        invoiceDto.setTotal(price.add(totalTax));

        return invoiceDto;
    }

    @Override
    public Invoice save(InvoiceDto invoiceDto, InvoiceType invoiceType) {
        invoiceDto.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        invoiceDto.setInvoiceType(invoiceType);
        invoiceDto.setCompany(companyService.getCompanyByLoggedInUser());
        Invoice invoice = mapperUtil.convert(invoiceDto, new Invoice());
        return invoiceRepository.save(invoice);
    }


    @Override
    public String generateNextInvoiceNo(InvoiceType invoiceType) {
        Long companyId = companyService.getCompanyByLoggedInUser().getId();
        String lastInvoiceNumber = invoiceRepository.findLatestInvoiceNumber(companyId, invoiceType);

        if (lastInvoiceNumber == null) {
            return invoiceType.getValue().substring(0, 1) + "-001";
        }
        String[] parts = lastInvoiceNumber.split("-");
        int number = Integer.parseInt(parts[1]);

        return String.format(parts[0] + "-%03d", number + 1);
    }

    @Override
    public InvoiceDto createNewInvoice(InvoiceType invoiceType) {
        InvoiceDto newInvoiceDto = new InvoiceDto();
        newInvoiceDto.setInvoiceNo(generateNextInvoiceNo(invoiceType));
        newInvoiceDto.setDate(LocalDate.now());
        return newInvoiceDto;
    }

    @Override
    public void update(InvoiceDto invoiceDto, InvoiceType invoiceType) {
        Invoice invoiceInDB = invoiceRepository.findInvoiceById(invoiceDto.getId()).orElseThrow(() -> new InvoiceNotFoundException("No Invoice Found with id -" + invoiceDto.getId()));

        invoiceDto.setInvoiceStatus(invoiceInDB.getInvoiceStatus());
        invoiceDto.setInvoiceType(invoiceType);
        invoiceDto.setCompany(companyService.getCompanyByLoggedInUser());

        Invoice invoiceToBeUpdated = mapperUtil.convert(invoiceDto, new Invoice());

        invoiceRepository.save(invoiceToBeUpdated);
    }

    @Override
    public void delete(Long id) {
        Invoice invoiceToBeDeleted = invoiceRepository.findById(id).orElseThrow(() -> new InvoiceNotFoundException("No Invoice Found with id -" + id));

        List<InvoiceProductDto> productsForInvoice = invoiceProductService.listAllByInvoiceId(id);
        productsForInvoice.stream().forEach(invoiceProductDto -> invoiceProductService.delete(invoiceProductDto.getId()));

        invoiceToBeDeleted.setIsDeleted(true);

        invoiceRepository.save(invoiceToBeDeleted);
    }

    @Override
    public void approve(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException("No Invoice Found with id -" + id));
        InvoiceType invoiceType = invoice.getInvoiceType();

        if (invoiceType == InvoiceType.SALES) {
            invoiceProductService.listAllByInvoiceId(id)
                    .stream()
                    .forEach(this::checkIfInvoiceCanBeApproved);
        }

        invoice.setInvoiceStatus(InvoiceStatus.APPROVED);

        updateRemainingStock(id, invoiceType);
        updateProfitLossAndRemainingQuantities(id, invoiceType);

        invoiceRepository.save(invoice);

        if (invoiceType == InvoiceType.SALES) {
            invoiceProductService.listAllByInvoiceId(id)
                    .stream()
                    .forEach(this::checkIfProductDroppedBelowLowLimit);
        }

    }

    @Override
    public List<InvoiceDto> showLastThreeApprovedInvoices(InvoiceStatus invoiceStatus) {
        Long companyId = companyService.getCompanyByLoggedInUser().getId();
        List<Invoice> invoiceList = invoiceRepository.findTop3ByCompanyIdAndInvoiceStatusOrderByDateDesc(companyId, invoiceStatus);
        return invoiceList.stream()
                .map(invoice -> {
                    InvoiceDto invoiceDto = mapperUtil.convert(invoice, new InvoiceDto());

                    BigDecimal totalPrice = calculatePriceByInvoiceId(invoiceDto.getId());

                    BigDecimal totalTax = calculateTotalTaxByInvoiceId(invoiceDto.getId());

                    invoiceDto.setPrice(totalPrice);
                    invoiceDto.setTax(totalTax.setScale(2));
                    invoiceDto.setTotal(totalPrice.add(totalTax).setScale(2));


                    return invoiceDto;
                })
                .collect(Collectors.toList());
    }


    @Override
    public boolean hasInvoicesByClientVendorId(Long clientVendorId) {
        return !invoiceRepository.findInvoicesByClientVendorId(clientVendorId).isEmpty();
    }

    @Override
    public void checkIfInvoiceCanBeApproved(InvoiceProductDto invoiceProductDto) {
        InvoiceProduct invoiceProductFromDB = invoiceProductRepository.findById(invoiceProductDto.getId())
                .orElseThrow(() -> new InvoiceProductNotFoundException("No invoice product found for the id -" + invoiceProductDto.getId()));

        String productName = invoiceProductDto.getProduct().getName();
        int stock = invoiceProductFromDB.getProduct().getQuantityInStock();
        int saleQuantity = invoiceProductDto.getQuantity();

        if (stock < saleQuantity) {
            throw new ProductNotFoundException("Stock of " + productName + " is not enough to approve this invoice. Please update the invoice.");
        }

    }

    private void checkIfProductDroppedBelowLowLimit(InvoiceProductDto invoiceProductDto) {
        InvoiceProduct invoiceProductFromDB = invoiceProductRepository.findById(invoiceProductDto.getId())
                .orElseThrow(() -> new InvoiceProductNotFoundException("No invoice product found for the id -" + invoiceProductDto.getId()));

        String productName = invoiceProductDto.getProduct().getName();
        int stock = invoiceProductFromDB.getProduct().getQuantityInStock();
        int saleQuantity = invoiceProductDto.getQuantity();
        int lowLimit = invoiceProductFromDB.getProduct().getLowLimitAlert();

        if ( stock-saleQuantity <= lowLimit ){
            throw new ProductLowLimitAlertException("Stock of " + productName + " is below low limit!");
        }
    }

    private void updateProfitLossAndRemainingQuantities(Long invoiceId, InvoiceType invoiceType) {

        invoiceProductService.listAllByInvoiceId(invoiceId)
                .stream()
                .forEach(invoiceProductDto -> {
                    invoiceProductDto.setRemainingQuantity(invoiceProductDto.getQuantity());

                    if (invoiceType == InvoiceType.PURCHASE) {
                        invoiceProductDto.setProfitLoss(BigDecimal.ZERO);
                    } else if (invoiceType == InvoiceType.SALES) {
                        invoiceProductDto.setProfitLoss(invoiceProductService.calculateInvoiceProductTotal(invoiceProductDto));
                        Long productId = invoiceProductDto.getProduct().getId();
                        invoiceProductService.listRemainingApprovedPurchaseInvoiceProducts(productId)
                                .stream()
                                .forEach(purchaseInvoiceProduct -> {
                                    int subs = purchaseInvoiceProduct.getRemainingQuantity() - invoiceProductDto.getRemainingQuantity();
                                    if (subs <= 0) {
                                        invoiceProductDto.setProfitLoss(
                                                invoiceProductDto.getProfitLoss()
                                                        .subtract(purchaseInvoiceProduct.getPrice()
                                                                .multiply(BigDecimal.valueOf(purchaseInvoiceProduct.getRemainingQuantity()))
                                                                .multiply(BigDecimal.valueOf(1 + purchaseInvoiceProduct.getTax() / 100.0))
                                                        )
                                        );
                                        purchaseInvoiceProduct.setRemainingQuantity(0);
                                        invoiceProductDto.setRemainingQuantity(-subs);
                                    } else {
                                        invoiceProductDto.setProfitLoss(
                                                invoiceProductDto.getProfitLoss()
                                                        .subtract(purchaseInvoiceProduct.getPrice()
                                                                .multiply(BigDecimal.valueOf(invoiceProductDto.getRemainingQuantity()))
                                                                .multiply(BigDecimal.valueOf(1 + purchaseInvoiceProduct.getTax() / 100.0))
                                                        )
                                        );
                                        purchaseInvoiceProduct.setRemainingQuantity(subs);
                                        invoiceProductDto.setRemainingQuantity(0);
                                    }
                                    invoiceProductService.save(purchaseInvoiceProduct, purchaseInvoiceProduct.getInvoice().getId());
                                });
                    }
                    invoiceProductService.save(invoiceProductDto, invoiceId);
                });
    }

    private void updateRemainingStock(Long invoiceId, InvoiceType invoiceType) {
        invoiceProductService.listAllByInvoiceId(invoiceId)
                .forEach(invoiceProductDto -> {
                    ProductDto productDto = invoiceProductDto.getProduct();
                    Integer currentStock = productDto.getQuantityInStock();
                    int updatedQuantity = productDto.getQuantityInStock();

                    if (invoiceType == InvoiceType.PURCHASE) {
                        updatedQuantity = currentStock + invoiceProductDto.getQuantity();
                    } else if (invoiceType == InvoiceType.SALES) {
                        updatedQuantity = currentStock - invoiceProductDto.getQuantity();
                    }

                    productDto.setQuantityInStock(updatedQuantity);
                    productService.save(productDto);
                });
    }


    public BigDecimal calculateTotalTaxByInvoiceId(Long id) {
        return invoiceProductService.listAllByInvoiceId(id)
                .stream()
                .map(p -> p.getPrice()
                        .multiply(BigDecimal.valueOf(p.getQuantity()))
                        .multiply(BigDecimal.valueOf(p.getTax() / 100.0)))
                .reduce(BigDecimal.ZERO, (acc, newTax) -> acc.add(newTax));
    }

    public BigDecimal calculatePriceByInvoiceId(Long id) {
        return invoiceProductService.listAllByInvoiceId(id)
                .stream()
                .map(p -> p.getPrice()
                        .multiply(BigDecimal.valueOf(p.getQuantity())))
                .reduce(BigDecimal.ZERO, (acc, newPrice) -> acc.add(newPrice));
    }

}
