package pl.dietadvisor.Common.productScraper.repository.dynamodb;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.dietadvisor.Common.productScraper.model.ProductScrapeJob;

@Repository
@EnableScan
public interface ProductScrapeJobRepository extends CrudRepository<ProductScrapeJob, String> {
}
