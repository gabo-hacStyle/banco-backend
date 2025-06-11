package gabs.infrastructure.controller;

import gabs.application.dto.ProductCreateDTO;
import gabs.application.ports.ProductUseCases;
import gabs.application.service.ProductService;
import gabs.domain.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductUseCases productService;

    public ProductController(ProductUseCases productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody ProductCreateDTO dto) {
        Product created = productService.createProduct(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable String id) {
        return productService.findByAccountNumber(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Product>> getByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(productService.findByClientId(clientId));
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<Product> getByAccountNumber(@PathVariable String accountNumber) {
        return productService.findByAccountNumber(accountNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
