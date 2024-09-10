package com.cydeo.repository;

import com.cydeo.entity.ClientVendor;
import com.cydeo.enums.ClientVendorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClientVendorRepository extends JpaRepository<ClientVendor,Long> {

    @Query("SELECT v FROM ClientVendor v WHERE v.company.id = ?1 AND v.clientVendorType = ?2 ORDER BY v.clientVendorName ASC")
    List<ClientVendor> findClientVendorsByCompanyId(Long companyId, ClientVendorType clientVendorType);

    @Query("SELECT v FROM ClientVendor v WHERE v.company.id = ?1 ORDER BY v.clientVendorType, v.clientVendorName")
    List<ClientVendor> findClientVendorsByCompanyIdAndClientVendorType(Long companyId);

}
