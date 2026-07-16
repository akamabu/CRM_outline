package com.vietcrm.product.api;

import com.vietcrm.product.application.ProductApplicationService;
import com.vietcrm.shared.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductApplicationService products;

    public ProductController(ProductApplicationService products) {
        this.products = products;
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> list(@RequestParam(required = false) String tenantId, @RequestParam(required = false) String keyword) {
        return ApiResponse.of(products.list(tenantId, keyword).stream().map(ProductResponse::from).toList());
    }

    @PostMapping
    public ApiResponse<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        return ApiResponse.of(ProductResponse.from(products.create(request)));
    }
}
