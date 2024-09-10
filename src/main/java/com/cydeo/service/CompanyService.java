package com.cydeo.service;

import com.cydeo.dto.CompanyDto;
import com.cydeo.enums.CompanyStatus;

import java.util.List;

public interface CompanyService {
   CompanyDto getCompanyByLoggedInUser();
   CompanyDto save(CompanyDto dto);
   CompanyDto update(CompanyDto dto);

   List<CompanyDto> getCompaniesByStatus (CompanyStatus status);
   List<CompanyDto> getCompaniesSortedByStatusAndTitle();
   List<CompanyDto> getCompaniesExcluding(Long id);
   List<CompanyDto> getAdminCompanies();

   CompanyDto findById(Long id);
  
   void activateCompany(Long companyId);
   void deactivateCompany(Long companyId);

}
