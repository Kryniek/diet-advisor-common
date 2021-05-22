package pl.dietadvisor.common.recipeScraper.repository.dynamodb;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.dietadvisor.common.recipeScraper.model.dynamodb.RecipeScrapeJob;

@Repository
@EnableScan
public interface RecipeScrapeJobRepository extends CrudRepository<RecipeScrapeJob, String> {
}
