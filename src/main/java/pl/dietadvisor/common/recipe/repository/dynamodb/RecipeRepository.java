package pl.dietadvisor.common.recipe.repository.dynamodb;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.dietadvisor.common.recipe.model.dynamodb.Recipe;

import java.util.List;

@Repository
@EnableScan
public interface RecipeRepository extends CrudRepository<Recipe, String> {
    List<Recipe> findByNameIn(List<String> names);
}
