package pl.dietadvisor.common.api.controller.recipes;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.dietadvisor.common.recipeScraper.model.dynamodb.RecipeScrapeLog;
import pl.dietadvisor.common.recipeScraper.service.RecipeScrapeLogService;

import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("recipes/scrape-logs")
public class RecipeScrapeLogController {
    private final RecipeScrapeLogService service;

    @GetMapping
    public ResponseEntity<List<RecipeScrapeLog>> get() {
        return ResponseEntity.ok(service.get());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeScrapeLog> getById(@PathVariable @NotBlank String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<List<RecipeScrapeLog>> getByJobId(@PathVariable @NotBlank String id) {
        return ResponseEntity.ok(service.getByJobId(id));
    }
}
