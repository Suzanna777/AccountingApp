package com.cydeo.repository;

import com.cydeo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByCompanyId(Long companyId);

    List<Category> findAllByCompanyIdAndIsDeleted(Long companyId, Boolean isDeleted);

    Category findByIdAndIsDeleted(Long id, boolean b);

    boolean existsByDescription(String description);
}
