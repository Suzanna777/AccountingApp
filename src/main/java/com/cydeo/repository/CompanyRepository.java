package com.cydeo.repository;

import com.cydeo.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company,Long> {
    boolean existsByTitle(String title);
    Company findByTitle(String title);
    boolean existsByTitleIgnoreCaseAndIdNot(String title, Long id);
}
