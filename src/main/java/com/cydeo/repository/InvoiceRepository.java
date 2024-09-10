package com.cydeo.repository;

import com.cydeo.entity.Invoice;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findAllByOrderByInvoiceNoDesc();

    Optional<Invoice> findInvoiceById(Long id);

    @Query("select i from Invoice i where i.invoiceType=?1 and i.company.id=?2 and i.isDeleted=false order by i.invoiceNo desc")
    List<Invoice> listAllByInvoiceTypeAndCompanyId(InvoiceType invoiceType, Long companyId);

    @Query("SELECT max(i.invoiceNo) FROM Invoice i WHERE i.invoiceType= ?2 AND i.company.id = ?1")
    String findLatestInvoiceNumber(Long companyId, InvoiceType invoiceType);

    List<Invoice> findInvoicesByClientVendorId(Long clientVendorId);

    List<Invoice> findTop3ByCompanyIdAndInvoiceStatusOrderByDateDesc(Long companyId, InvoiceStatus invoiceStatus);
}