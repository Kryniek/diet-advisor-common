package pl.dietadvisor.common.recipeScraper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dietadvisor.common.recipeScraper.model.dynamodb.RecipeScrapeJob;
import pl.dietadvisor.common.recipeScraper.repository.dynamodb.RecipeScrapeJobRepository;

import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;
import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static pl.dietadvisor.common.recipeScraper.enums.RecipeScrapeJobState.CREATED;

@Service
@RequiredArgsConstructor
public class RecipeScrapeJobService {
    private final RecipeScrapeJobRepository repository;

    public List<RecipeScrapeJob> get() {
        return (List<RecipeScrapeJob>) repository.findAll();
    }

    public RecipeScrapeJob getById(String id) {
        return repository.findById(id).orElseThrow();
    }

    public RecipeScrapeJob create(RecipeScrapeJob recipeScrapeJob) {
        return repository.save(RecipeScrapeJob.builder()
                .state(CREATED)
                .source(recipeScrapeJob.getSource())
                .createdAt(now())
                .build());
    }

    public RecipeScrapeJob update(RecipeScrapeJob recipeScrapeJob) {
        RecipeScrapeJob existingRecipeScrapeJob = getById(recipeScrapeJob.getId());
        if (nonNull(recipeScrapeJob.getState())) {
            existingRecipeScrapeJob.setState(recipeScrapeJob.getState());
        }
        if (isNotBlank(recipeScrapeJob.getErrorMessage())) {
            existingRecipeScrapeJob.setErrorMessage(recipeScrapeJob.getErrorMessage());
        }
        if (nonNull(recipeScrapeJob.getScrapedRecipesNumber())) {
            existingRecipeScrapeJob.setScrapedRecipesNumber(recipeScrapeJob.getScrapedRecipesNumber());
        }

        existingRecipeScrapeJob.setUpdatedAt(now());

        return repository.save(existingRecipeScrapeJob);
    }
}
