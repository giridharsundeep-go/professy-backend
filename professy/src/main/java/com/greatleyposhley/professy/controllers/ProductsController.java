package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.services.ProductsService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductsController {

    private final ProductsService productsService;

    public ProductsController(
            ProductsService productsService
    ) {
        this.productsService = productsService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>>
    getAllProducts(
            Authentication authentication
    ) {

        try {

            String userEmail =
                    getAuthenticatedEmail(
                            authentication
                    );

            List<Map<String, Object>> products =
                    productsService.getAllProducts(
                            userEmail
                    );

            return success(
                    products,
                    "Products fetched successfully",
                    HttpStatus.OK
            );

        } catch (IllegalArgumentException ex) {

            return error(
                    ex.getMessage(),
                    HttpStatus.BAD_REQUEST
            );

        } catch (Exception ex) {

            return error(
                    ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Map<String, Object>>
    getProductById(
            @PathVariable Long productId,
            Authentication authentication
    ) {

        try {

            String userEmail =
                    getAuthenticatedEmail(
                            authentication
                    );

            Map<String, Object> product =
                    productsService.getProductById(
                            productId,
                            userEmail
                    );

            return success(
                    product,
                    "Product fetched successfully",
                    HttpStatus.OK
            );

        } catch (IllegalArgumentException ex) {

            HttpStatus status =
                    "Product not found".equals(
                            ex.getMessage()
                    )
                            ? HttpStatus.NOT_FOUND
                            : HttpStatus.BAD_REQUEST;

            return error(
                    ex.getMessage(),
                    status
            );

        } catch (Exception ex) {

            return error(
                    ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>>
    createProduct(
            @RequestBody Map<String, Object> payload,
            Authentication authentication
    ) {

        try {

            String userEmail =
                    getAuthenticatedEmail(
                            authentication
                    );

            Map<String, Object> product =
                    productsService.createProduct(
                            payload,
                            userEmail
                    );

            return success(
                    product,
                    "Product created successfully",
                    HttpStatus.CREATED
            );

        } catch (IllegalArgumentException ex) {

            return error(
                    ex.getMessage(),
                    HttpStatus.BAD_REQUEST
            );

        } catch (Exception ex) {

            return error(
                    ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Map<String, Object>>
    updateProduct(
            @PathVariable Long productId,
            @RequestBody Map<String, Object> payload,
            Authentication authentication
    ) {

        try {

            String userEmail =
                    getAuthenticatedEmail(
                            authentication
                    );

            Map<String, Object> product =
                    productsService.updateProduct(
                            productId,
                            payload,
                            userEmail
                    );

            return success(
                    product,
                    "Product updated successfully",
                    HttpStatus.OK
            );

        } catch (IllegalArgumentException ex) {

            HttpStatus status =
                    "Product not found".equals(
                            ex.getMessage()
                    )
                            ? HttpStatus.NOT_FOUND
                            : HttpStatus.BAD_REQUEST;

            return error(
                    ex.getMessage(),
                    status
            );

        } catch (Exception ex) {

            return error(
                    ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Map<String, Object>>
    deleteProduct(
            @PathVariable Long productId,
            Authentication authentication
    ) {

        try {

            String userEmail =
                    getAuthenticatedEmail(
                            authentication
                    );

            Map<String, Object> result =
                    productsService.deleteProduct(
                            productId,
                            userEmail
                    );

            return success(
                    result,
                    "Product deleted successfully",
                    HttpStatus.OK
            );

        } catch (IllegalArgumentException ex) {

            HttpStatus status =
                    "Product not found".equals(
                            ex.getMessage()
                    )
                            ? HttpStatus.NOT_FOUND
                            : HttpStatus.BAD_REQUEST;

            return error(
                    ex.getMessage(),
                    status
            );

        } catch (Exception ex) {

            return error(
                    ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    private String getAuthenticatedEmail(
            Authentication authentication
    ) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new IllegalArgumentException(
                    "Authentication is required"
            );
        }

        String email =
                authentication.getName();

        if (email == null ||
                email.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Authenticated user email is required"
            );
        }

        return email;
    }

    private ResponseEntity<Map<String, Object>> success(
            Object data,
            String message,
            HttpStatus status
    ) {

        Map<String, Object> body =
                new HashMap<>();

        body.put("data", data);
        body.put("message", message);
        body.put("success", true);

        return ResponseEntity
                .status(status)
                .body(body);
    }

    private ResponseEntity<Map<String, Object>> error(
            String message,
            HttpStatus status
    ) {

        Map<String, Object> body =
                new HashMap<>();

        body.put("data", null);
        body.put(
                "message",
                message == null
                        ? "Request failed"
                        : message
        );
        body.put("success", false);

        return ResponseEntity
                .status(status)
                .body(body);
    }
}