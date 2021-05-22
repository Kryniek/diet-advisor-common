package pl.dietadvisor.common.recipeScraper.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.dietadvisor.common.recipeScraper.model.redis.RecipeScrapeJobCancel;

@Repository
public interface RecipeScrapeJobCancelRedisRepository extends CrudRepository<RecipeScrapeJobCancel, String> {
}
