package pl.dietadvisor.common.recipeScraper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dietadvisor.common.recipeScraper.model.dynamodb.RecipeScrapeLog;
import pl.dietadvisor.common.recipeScraper.repository.dynamodb.RecipeScrapeLogRepository;

import java.util.List;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class RecipeScrapeLogService {
    private final RecipeScrapeLogRepository repository;

    public List<RecipeScrapeLog> get() {
        return (List<RecipeScrapeLog>) repository.findAll();
    }

    public RecipeScrapeLog getById(String id) {
        return repository.findById(id).orElseThrow();
    }

    public List<RecipeScrapeLog> getByIds(List<String> ids) {
        return repository.findByIdIn(ids);
    }

    public List<RecipeScrapeLog> createAll(List<RecipeScrapeLog> recipeScrapeLogs) {
        recipeScrapeLogs.forEach(recipeScrapeLog -> {
            recipeScrapeLog.setId(null);
            recipeScrapeLog.setCreatedAt(now());
        });

        return (List<RecipeScrapeLog>) repository.saveAll(recipeScrapeLogs);
    }

    public List<RecipeScrapeLog> getByJobId(String jobId) {
        return repository.findByJobId(jobId);
    }
}
