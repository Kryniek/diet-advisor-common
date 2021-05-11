package pl.dietadvisor.Common.productScraper.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dietadvisor.Common.productScraper.model.Product;
import pl.dietadvisor.Common.productScraper.service.ProductService;

import javax.validation.constraints.NotBlank;
import java.util.List;

import static java.util.Objects.requireNonNull;

@RestController
@RequiredArgsConstructor
@RequestMapping("products")
public class ProductController {
    private final ProductService service;

    @GetMapping
    public ResponseEntity<List<Product>> get() {
        return ResponseEntity.ok(service.get());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable @NotBlank String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping
    public ResponseEntity<Product> update(@RequestBody @NonNull Product product) {
        requireNonNull(product.getId(), "Id must be set.");

        return ResponseEntity.ok(service.update(product));
    }
}
