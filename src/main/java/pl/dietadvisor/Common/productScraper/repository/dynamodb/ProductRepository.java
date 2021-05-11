package pl.dietadvisor.Common.productScraper.repository.dynamodb;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.dietadvisor.Common.productScraper.model.Product;

import java.util.List;

@Repository
@EnableScan
public interface ProductRepository extends CrudRepository<Product, String> {
    List<Product> findByNameIn(List<String> names);
}
