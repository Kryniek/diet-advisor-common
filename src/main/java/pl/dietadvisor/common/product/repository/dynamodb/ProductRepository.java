package pl.dietadvisor.common.product.repository.dynamodb;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.dietadvisor.common.product.model.dynamodb.Product;

import java.util.List;

@Repository
@EnableScan
public interface ProductRepository extends CrudRepository<Product, String> {
    List<Product> findByNameIn(List<String> names);

    Product findByName(String name);

    List<Product> findByIdIn(List<String> ids);
}
