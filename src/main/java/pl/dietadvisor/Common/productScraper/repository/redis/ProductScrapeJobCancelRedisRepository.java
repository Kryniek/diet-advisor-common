package pl.dietadvisor.Common.productScraper.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.dietadvisor.Common.productScraper.model.redis.ProductScrapeJobCancel;

@Repository
public interface ProductScrapeJobCancelRedisRepository extends CrudRepository<ProductScrapeJobCancel, String> {
}
