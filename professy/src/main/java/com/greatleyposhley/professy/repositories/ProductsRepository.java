package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.Products;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductsRepository extends JpaRepository<Products, Long> {

    List<Products> findAllByOrgIdAndUserAccount_Id(Long orgId, Long userAccountId);

    Optional<Products> findByIdAndOrgIdAndUserAccount_Id(
            Long productId,
            Long orgId,
            Long userAccountId
    );

    boolean existsByOrgIdAndNameAndUserAccount_Id(
            Long orgId,
            String name,
            Long userAccountId
    );
}
