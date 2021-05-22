package pl.dietadvisor.common.recipe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.dietadvisor.common.recipe.model.dynamodb.Recipe;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipeMigrationResult {
    private List<Recipe> migratedRecipes;
    private List<Recipe> alreadyExistingRecipes;
}
