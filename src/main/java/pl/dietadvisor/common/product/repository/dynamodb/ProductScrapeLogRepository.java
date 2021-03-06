package pl.dietadvisor.common.product.repository.dynamodb;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.dietadvisor.common.product.model.dynamodb.ProductScrapeLog;

import java.util.List;

@Repository
@EnableScan
public interface ProductScrapeLogRepository extends CrudRepository<ProductScrapeLog, String> {
    List<ProductScrapeLog> findByJobId(String jobId);

    List<ProductScrapeLog> findByIdIn(List<String> ids);
}
