package pl.dietadvisor.Common.productScraper.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dietadvisor.Common.productScraper.model.ProductScrapeJob;
import pl.dietadvisor.Common.productScraper.model.redis.ProductScrapeJobCancel;
import pl.dietadvisor.Common.productScraper.service.ProductScrapeJobService;
import pl.dietadvisor.Common.productScraper.service.redis.ProductScrapeJobCancelRedisService;

import javax.validation.constraints.NotBlank;
import java.util.List;

import static java.util.Objects.requireNonNull;

@RestController
@RequiredArgsConstructor
@RequestMapping("products/scrape-jobs")
public class ProductScrapeJobController {
    private final ProductScrapeJobService service;
    private final ProductScrapeJobCancelRedisService productScrapeJobCancelRedisService;

    @GetMapping
    public ResponseEntity<List<ProductScrapeJob>> get() {
        return ResponseEntity.ok(service.get());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductScrapeJob> getById(@PathVariable @NotBlank String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<ProductScrapeJob> create(@RequestBody @NonNull ProductScrapeJob productScrapeJob) {
        requireNonNull(productScrapeJob.getSource(), "Source must be set.");

        return new ResponseEntity<>(service.create(productScrapeJob), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ProductScrapeJobCancel> cancel(@PathVariable @NotBlank String id) {
        return ResponseEntity.ok(productScrapeJobCancelRedisService.cancel(id));
    }
}
