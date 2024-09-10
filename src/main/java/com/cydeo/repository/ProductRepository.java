package com.cydeo.repository;

import com.cydeo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCompanyIdAndIsDeletedFalseOrderByCategoryAscNameAsc(Long companyId);

    Product findByIdAndIsDeleted(Long id, Boolean deleted);

    List<Product> findByIsDeletedFalse();

    @Query("select p from Product p where p.category.id = ?1")
    List<Product> retrieveAllAvailableProducts(Long categoryId);

    @Query("select p from Product p where p.company.id = ?1")
    List<Product> retrieveAllAvailableProductsForCompany(Long companyId);

}