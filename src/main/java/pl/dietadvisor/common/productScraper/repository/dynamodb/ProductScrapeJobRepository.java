package pl.dietadvisor.common.productScraper.repository.dynamodb;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.dietadvisor.common.productScraper.model.ProductScrapeJob;

@Repository
@EnableScan
public interface ProductScrapeJobRepository extends CrudRepository<ProductScrapeJob, String> {
}
