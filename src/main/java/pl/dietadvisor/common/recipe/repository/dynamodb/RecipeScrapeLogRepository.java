package pl.dietadvisor.common.recipe.repository.dynamodb;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.dietadvisor.common.recipe.model.dynamodb.RecipeScrapeLog;

import java.util.List;

@Repository
@EnableScan
public interface RecipeScrapeLogRepository extends CrudRepository<RecipeScrapeLog, String> {
    List<RecipeScrapeLog> findByJobId(String jobId);

    List<RecipeScrapeLog> findByIdIn(List<String> ids);
}
