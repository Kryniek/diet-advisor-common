package pl.dietadvisor.common.product.repository.dynamodb;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.dietadvisor.common.product.model.dynamodb.ProductScrapeJob;

@Repository
@EnableScan
public interface ProductScrapeJobRepository extends CrudRepository<ProductScrapeJob, String> {
}
