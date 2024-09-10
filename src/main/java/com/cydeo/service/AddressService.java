package com.cydeo.service;

import com.cydeo.dto.AddressDto;
import com.cydeo.entity.Address;

public interface AddressService {
    Long saveAndRetrieveId(AddressDto address);
    Address findById(Long id);
}
