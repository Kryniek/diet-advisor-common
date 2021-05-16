package pl.dietadvisor.common.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.dietadvisor.common.productScraper.model.dynamodb.ProductScrapeLog;
import pl.dietadvisor.common.productScraper.service.ProductScrapeLogService;

import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("products/scrape-logs")
public class ProductScrapeLogController {
    private final ProductScrapeLogService service;

    @GetMapping
    public ResponseEntity<List<ProductScrapeLog>> get() {
        return ResponseEntity.ok(service.get());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductScrapeLog> getById(@PathVariable @NotBlank String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<List<ProductScrapeLog>> getByJobId(@PathVariable @NotBlank String id) {
        return ResponseEntity.ok(service.getByJobId(id));
    }
}
