package pl.dietadvisor.common.recipeScraper.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dietadvisor.common.recipeScraper.model.redis.RecipeScrapeJobCancel;
import pl.dietadvisor.common.recipeScraper.repository.redis.RecipeScrapeJobCancelRedisRepository;

@Service
@RequiredArgsConstructor
public class RecipeScrapeJobCancelRedisService {
    private final RecipeScrapeJobCancelRedisRepository repository;

    public RecipeScrapeJobCancel cancel(String id) {
        return repository.save(RecipeScrapeJobCancel.builder()
                .id(id)
                .build());
    }

    public boolean isCancelled(String id) {
        return repository.existsById(id);
    }
}
