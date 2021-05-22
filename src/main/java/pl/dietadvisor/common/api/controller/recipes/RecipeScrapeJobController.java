package pl.dietadvisor.common.api.controller.recipes;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dietadvisor.common.api.producer.RecipeScrapeJobProducer;
import pl.dietadvisor.common.recipe.model.dynamodb.RecipeScrapeJob;
import pl.dietadvisor.common.recipeScraper.model.redis.RecipeScrapeJobCancel;
import pl.dietadvisor.common.recipe.service.RecipeScrapeJobService;
import pl.dietadvisor.common.recipeScraper.service.redis.RecipeScrapeJobCancelRedisService;

import javax.validation.constraints.NotBlank;
import java.util.List;

import static java.util.Objects.requireNonNull;

@RestController
@RequiredArgsConstructor
@RequestMapping("recipes/scrape-jobs")
public class RecipeScrapeJobController {
    private final RecipeScrapeJobService service;
    private final RecipeScrapeJobCancelRedisService productScrapeJobCancelRedisService;
    private final RecipeScrapeJobProducer productScrapeJobProducer;

    @GetMapping
    public ResponseEntity<List<RecipeScrapeJob>> get() {
        return ResponseEntity.ok(service.get());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeScrapeJob> getById(@PathVariable @NotBlank String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<RecipeScrapeJob> create(@RequestBody @NonNull RecipeScrapeJob productScrapeJob) {
        requireNonNull(productScrapeJob.getSource(), "Source must be set.");

        RecipeScrapeJob createdProductScrapeJob = service.create(productScrapeJob);
        productScrapeJobProducer.send(createdProductScrapeJob);

        return new ResponseEntity<>(createdProductScrapeJob, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<RecipeScrapeJobCancel> cancel(@PathVariable @NotBlank String id) {
        return ResponseEntity.ok(productScrapeJobCancelRedisService.cancel(id));
    }
}
