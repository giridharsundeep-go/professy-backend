package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.Products;
import com.greatleyposhley.professy.entities.UserAccount;
import com.greatleyposhley.professy.repositories.ProductsRepository;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductsService {

    private final ProductsRepository productsRepository;
    private final UserAccountRepository userAccountRepository;

    public ProductsService(
            ProductsRepository productsRepository,
            UserAccountRepository userAccountRepository
    ) {
        this.productsRepository = productsRepository;
        this.userAccountRepository = userAccountRepository;
    }

    private UserAccount getUserAccount(Long orgId, String email) {

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Authenticated user email is required"
            );
        }

        return userAccountRepository
                .findByOrgIdAndEmail(orgId, email.trim().toLowerCase())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Authenticated user not found"
                        )
                );
    }

    private String getRequiredName(Map<String, Object> payload) {

        if (payload == null) {
            throw new IllegalArgumentException(
                    "Product data is required"
            );
        }

        Object value = payload.get("name");

        String name = value == null
                ? ""
                : value.toString().trim();

        if (name.isEmpty()) {
            throw new IllegalArgumentException(
                    "Name is required"
            );
        }

        return name;
    }

    private String getDescription(Map<String, Object> payload) {

        if (payload == null ||
                payload.get("description") == null) {
            return null;
        }

        String description =
                payload.get("description")
                        .toString()
                        .trim();

        return description.isEmpty()
                ? null
                : description;
    }

    @Transactional
    public Map<String, Object> createProduct(Long orgId, Map<String, Object> payload,
                                             String userEmail) {

        UserAccount userAccount =
                getUserAccount(orgId, userEmail);

        String name =
                getRequiredName(payload);

        String description =
                getDescription(payload);

        if (productsRepository.existsByOrgIdAndNameAndUserAccount_Id(orgId,
                name,
                userAccount.getId()
        )) {
            throw new IllegalArgumentException(
                    "A product with this name already exists"
            );
        }

        Products product =
                new Products();

        product.setOrgId(orgId);
        product.setUserAccount(userAccount);
        product.setName(name);
        product.setDescription(description);

        Products saved =
                productsRepository.save(product);

        return toProductResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllProducts(Long orgId, String userEmail) {

        UserAccount userAccount =
                getUserAccount(orgId, userEmail);

        List<Products> products =
                productsRepository
                        .findAllByOrgIdAndUserAccount_Id(orgId, 
                                userAccount.getId()
                        );

        List<Map<String, Object>> result =
                new ArrayList<>();

        for (Products product : products) {
            result.add(toProductResponse(product));
        }

        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getProductById(Long orgId, Long productId,
                                              String userEmail) {

        validateId(productId);

        UserAccount userAccount =
                getUserAccount(orgId, userEmail);

        Products product =
                productsRepository
                        .findByIdAndOrgIdAndUserAccount_Id(
                                productId,
                                orgId,
                                userAccount.getId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Product not found"
                                )
                        );

        return toProductResponse(product);
    }

    @Transactional
    public Map<String, Object> updateProduct(Long orgId, Long productId,
                                             Map<String, Object> payload,
                                             String userEmail) {

        validateId(productId);

        UserAccount userAccount =
                getUserAccount(orgId, userEmail);

        String name =
                getRequiredName(payload);

        String description =
                getDescription(payload);

        Products product =
                productsRepository
                        .findByIdAndOrgIdAndUserAccount_Id(
                                productId,
                                orgId,
                                userAccount.getId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Product not found"
                                )
                        );

        boolean nameChanged =
                !name.equalsIgnoreCase(
                        product.getName()
                );

        if (nameChanged &&
                productsRepository.existsByOrgIdAndNameAndUserAccount_Id(orgId,
                        name,
                        userAccount.getId()
                )) {

            throw new IllegalArgumentException(
                    "A product with this name already exists"
            );
        }

        product.setName(name);
        product.setDescription(description);

        Products updated =
                productsRepository.save(product);

        return toProductResponse(updated);
    }

    @Transactional
    public Map<String, Object> deleteProduct(Long orgId, Long productId,
                                             String userEmail) {

        validateId(productId);

        UserAccount userAccount =
                getUserAccount(orgId, userEmail);

        Products product =
                productsRepository
                        .findByIdAndOrgIdAndUserAccount_Id(
                                productId,
                                orgId,
                                userAccount.getId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Product not found"
                                )
                        );

        productsRepository.delete(product);

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put("id", productId);
        result.put(
                "message",
                "Product deleted successfully"
        );

        return result;
    }

    private void validateId(Long productId) {

        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid product id"
            );
        }
    }

    private Map<String, Object> toProductResponse(
            Products product
    ) {

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put("id", product.getId());

        result.put(
                "user_id",
                product.getUserAccount() != null
                        ? product.getUserAccount().getId()
                        : null
        );

        result.put(
                "name",
                product.getName()
        );

        result.put(
                "description",
                product.getDescription()
        );

        return result;
    }
}