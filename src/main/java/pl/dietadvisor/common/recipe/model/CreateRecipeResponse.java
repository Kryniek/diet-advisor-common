package pl.dietadvisor.common.recipe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateRecipeResponse {
    private List<Integer> mealNumbers;
    private String name;
    private Map<String, BigDecimal> productsNamesToQuantities = new HashMap<>();
    private String recipe;
}
