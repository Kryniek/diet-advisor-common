package pl.dietadvisor.common.shoppingList.repository.dynamodb;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.dietadvisor.common.shoppingList.model.dynamodb.ShoppingList;

@Repository
@EnableScan
public interface ShoppingListRepository extends CrudRepository<ShoppingList, String> {
}
