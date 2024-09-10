package com.cydeo.service;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.enums.ClientVendorType;

import java.util.List;

public interface ClientVendorService {


    ClientVendorDto findById(Long id);

    List<ClientVendorDto> findClientVendorsByCompanyId(ClientVendorType clientVendorType);

    List<ClientVendorDto> findClientVendorsByCompanyIdAndClientVendorType();

    ClientVendorDto saveClientVendor(ClientVendorDto clientVendor);

    ClientVendorDto updateClientVendor(ClientVendorDto clientVendor);

    void deleteClientVendor(Long id);

}
