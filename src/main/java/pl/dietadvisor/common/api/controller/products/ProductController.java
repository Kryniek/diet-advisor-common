package pl.dietadvisor.common.api.controller.products;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dietadvisor.common.product.model.dynamodb.Product;
import pl.dietadvisor.common.product.service.ProductService;

import javax.validation.constraints.NotBlank;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpStatus.CREATED;

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

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody @NonNull Product product) {
        requireNonNull(product.getName(), "Name must be set.");
        requireNonNull(product.getKcal(), "Kcal must be set.");
        requireNonNull(product.getProteins(), "Proteins must be set.");
        requireNonNull(product.getCarbohydrates(), "Carbohydrates must be set.");
        requireNonNull(product.getFats(), "Fats must be set.");

        return new ResponseEntity<>(service.create(product), CREATED);
    }
}
