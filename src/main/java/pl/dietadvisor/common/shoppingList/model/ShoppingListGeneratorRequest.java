package pl.dietadvisor.common.shoppingList.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingListGeneratorRequest {
    private Map<String, Integer> recipesIdsToQuantities;
    private Map<String, Integer> recipesNamesToQuantities;
    private Map<String, BigDecimal> productsIdsToQuantities;
    private Map<String, BigDecimal> productsNamesToQuantities;
}
