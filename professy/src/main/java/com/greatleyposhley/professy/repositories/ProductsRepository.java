package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.Products;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductsRepository extends JpaRepository<Products, Long> {

    List<Products> findAllByUserAccount_Id(Long userAccountId);

    Optional<Products> findByIdAndUserAccount_Id(
            Long productId,
            Long userAccountId
    );

    boolean existsByNameAndUserAccount_Id(
            String name,
            Long userAccountId
    );
}