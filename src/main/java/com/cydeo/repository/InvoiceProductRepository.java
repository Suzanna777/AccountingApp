package com.cydeo.repository;

import com.cydeo.entity.InvoiceProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceProductRepository extends JpaRepository<InvoiceProduct, Long> {

    Optional<InvoiceProduct> findById(Long id);

    @Query("select i from InvoiceProduct i where i.invoice.id=?1 and i.isDeleted=false")
    List<InvoiceProduct> retrieveAllByInvoice_IdAndIsDeletedFalse(Long invoiceId);


    @Query("select i from InvoiceProduct i " +
            "where i.invoice.invoiceStatus='APPROVED' " +
            "and i.invoice.invoiceType='PURCHASE' " +
            "and i.remainingQuantity<>0 " +
            "and i.product.id=?1 " +
            "and i.isDeleted=false " +
            "order by i.lastUpdateDateTime asc ")
    List<InvoiceProduct> listRemainingApprovedPurchaseInvoiceProducts(Long productId);

    boolean existsByInvoiceIdAndProductIdAndIsDeletedFalse(Long invoiceId, Long productId);

    @Query("SELECT ip FROM InvoiceProduct ip " +
            "JOIN ip.invoice i " +
            "WHERE i.invoiceStatus = 'APPROVED' AND i.company.id = :companyId " +
            "ORDER BY i.date DESC")
    List<InvoiceProduct> findAllApprovedInvoices(@Param("companyId") Long companyId);

    @Query("SELECT ip FROM InvoiceProduct ip WHERE ip.invoice.company.id=?1 AND ip.invoice.invoiceStatus = 'APPROVED'" +
            "ORDER BY ip.invoice.date DESC")
    List<InvoiceProduct> findAllProductsOfApprovedInvoices(Long companyId);

}
