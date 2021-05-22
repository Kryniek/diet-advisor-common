package pl.dietadvisor.common.api.controller.products;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dietadvisor.common.productScraper.model.dynamodb.ProductScrapeJob;
import pl.dietadvisor.common.productScraper.model.redis.ProductScrapeJobCancel;
import pl.dietadvisor.common.api.producer.ProductScrapeJobProducer;
import pl.dietadvisor.common.productScraper.service.ProductScrapeJobService;
import pl.dietadvisor.common.productScraper.service.redis.ProductScrapeJobCancelRedisService;

import javax.validation.constraints.NotBlank;
import java.util.List;

import static java.util.Objects.requireNonNull;

@RestController
@RequiredArgsConstructor
@RequestMapping("products/scrape-jobs")
public class ProductScrapeJobController {
    private final ProductScrapeJobService service;
    private final ProductScrapeJobCancelRedisService productScrapeJobCancelRedisService;
    private final ProductScrapeJobProducer productScrapeJobProducer;

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

        ProductScrapeJob createdProductScrapeJob = service.create(productScrapeJob);
        productScrapeJobProducer.send(createdProductScrapeJob);

        return new ResponseEntity<>(createdProductScrapeJob, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ProductScrapeJobCancel> cancel(@PathVariable @NotBlank String id) {
        return ResponseEntity.ok(productScrapeJobCancelRedisService.cancel(id));
    }
}
