package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.Address;
import com.cydeo.entity.Company;
import com.cydeo.entity.User;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.exception.CompanyNotFoundException;
import com.cydeo.repository.AddressRepository;
import com.cydeo.repository.CompanyRepository;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.SecurityService;
import com.cydeo.util.MapperUtil;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EqualsAndHashCode
@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final MapperUtil mapperUtil;
    private final SecurityService securityService;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository, MapperUtil mapperUtil, SecurityService securityService, UserRepository userRepository, AddressRepository addressRepository) {
        this.companyRepository = companyRepository;
        this.mapperUtil = mapperUtil;
        this.securityService = securityService;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public CompanyDto getCompanyByLoggedInUser() {
        return securityService.getLoggedInUser().getCompany();
    }

    @Override
    public CompanyDto save(CompanyDto dto) {

        if (companyRepository.existsByTitle(dto.getTitle())) {
            throw new IllegalArgumentException("Title must be unique.");
        }
        Address address = mapperUtil.convert(dto.getAddress(), new Address());
        addressRepository.save(address);

        Company company = mapperUtil.convert(dto, new Company());
        company.setAddress(address);
        company.setCompanyStatus(CompanyStatus.PASSIVE);
        Company savedCompany = companyRepository.save(company);

        return mapperUtil.convert(savedCompany,dto);
    }

    @Override
    public CompanyDto update(CompanyDto dto) {
        Company company = companyRepository.findById(dto.getId())
                .orElseThrow(() -> new CompanyNotFoundException("Company not found with ID: " + dto.getId()));

        if (companyRepository.existsByTitleIgnoreCaseAndIdNot(dto.getTitle(), dto.getId())) {
            throw new IllegalArgumentException("Title must be unique.");
        }

        Address address = mapperUtil.convert(dto.getAddress(), new Address());
        addressRepository.save(address);

        Company updatedCompany = mapperUtil.convert(dto, company);
        updatedCompany.setAddress(address);
        updatedCompany.setId(company.getId());
        updatedCompany.setCompanyStatus(company.getCompanyStatus());
        companyRepository.save(updatedCompany);

        return mapperUtil.convert(updatedCompany,dto);
    }

    @Override
    public void activateCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found with ID: " + companyId));
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        companyRepository.save(company);
        List<User> users = userRepository.findAll();
        for (User user : users) {
            user.setIsDeleted(false);
        }
        userRepository.saveAll(users);
    }

    @Override
    public void deactivateCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found with ID: " + companyId));
        company.setCompanyStatus(CompanyStatus.PASSIVE);
        companyRepository.save(company);
        List<User> users = userRepository.findAll();
        for (User user : users) {
            user.setIsDeleted(true);
        }
        userRepository.saveAll(users);
    }

    @Override
    public List<CompanyDto> getCompaniesByStatus(CompanyStatus status) {

        return companyRepository.findAll()
                .stream()
                .filter(company ->
                        company.getCompanyStatus().equals(status))
                .map(company ->
                        mapperUtil.convert(company, new CompanyDto()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CompanyDto> getCompaniesSortedByStatusAndTitle() {

        return Stream.concat(
                        getCompaniesByStatus(CompanyStatus.ACTIVE)
                                .stream()
                                .sorted(Comparator.comparing(CompanyDto::getTitle))
                        ,
                        getCompaniesByStatus(CompanyStatus.PASSIVE)
                                .stream()
                                .sorted(Comparator.comparing(CompanyDto::getTitle)))
                .collect(Collectors.toList());
    }

    @Override
    public List<CompanyDto> getCompaniesExcluding(Long id) {
        return getCompaniesSortedByStatusAndTitle()
                .stream()
                .filter(companyDto -> !companyDto.getId().equals(id))
                .collect(Collectors.toList());
    }

    @Override
    public List<CompanyDto> getAdminCompanies() {
        UserDto currentUser = securityService.getLoggedInUser();
        List<Company> companyList = companyRepository.findAll();

        if(currentUser.getRole().getDescription().equalsIgnoreCase("Root User")){
            return getCompaniesExcluding(currentUser.getCompany().getId());

        } else  {
            return companyList.stream()
                    .filter(company -> company.getTitle().equalsIgnoreCase(currentUser.getCompany().getTitle()))
                    .map(company -> mapperUtil.convert(company, new CompanyDto()))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public CompanyDto findById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found with ID: " + id));
        return mapperUtil.convert(companyRepository.findById(id), new CompanyDto());
    }

}

