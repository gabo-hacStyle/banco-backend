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

    @GetMapping("/{productNumber}")
    public ResponseEntity<Product> getByAccountNumber(@PathVariable String productNumber) {
        Product product = productService.findByAccountNumber(productNumber);
        return ResponseEntity.ok(product);
    }


    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Product>> getByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(productService.findByClientId(clientId));
    }



    @PutMapping("/{accountNumber}/enable")
    public Product enable(@PathVariable String accountNumber) {
        return productService.activateProduct(accountNumber);
    }
    @PutMapping("/{accountNumber}/disable")
    public Product disable(@PathVariable String accountNumber) {

        return productService.inactivateProduct(accountNumber);

    }
    @PutMapping("/{accountNumber}/cancel")
    public Product cancel(@PathVariable String accountNumber) {
        return productService.cancelProduct(accountNumber);
    }
}
