package com.cydeo.service.impl;

import com.cydeo.dto.AddressDto;
import com.cydeo.entity.Address;
import com.cydeo.repository.AddressRepository;
import com.cydeo.service.AddressService;
import com.cydeo.util.MapperUtil;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImpl implements AddressService {

    private final MapperUtil mapperUtil;
    private final AddressRepository addressRepository;

    public AddressServiceImpl(MapperUtil mapperUtil, AddressRepository addressRepository) {
        this.mapperUtil = mapperUtil;
        this.addressRepository = addressRepository;
    }

    @Override
    public Long saveAndRetrieveId(AddressDto address) {
        Address newAddress = mapperUtil.convert(address, new Address());
        return addressRepository.save(newAddress).getId();
    }

    @Override
    public Address findById(Long id) {
        return addressRepository.findById(id).orElseThrow();
    }
}
