package pl.dietadvisor.common.recipeScraper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dietadvisor.common.productScraper.model.dynamodb.Product;
import pl.dietadvisor.common.productScraper.service.ProductService;
import pl.dietadvisor.common.recipeScraper.model.dynamodb.Recipe;
import pl.dietadvisor.common.recipeScraper.repository.dynamodb.RecipeRepository;
import pl.dietadvisor.common.shared.exception.custom.NotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static java.math.MathContext.DECIMAL32;
import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static org.springframework.util.CollectionUtils.isEmpty;
import static pl.dietadvisor.common.recipeScraper.enums.RecipeSource.USER;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository repository;
    private final ProductService productService;

    public List<Recipe> get() {
        return (List<Recipe>) repository.findAll();
    }

    public Recipe getById(String id) {
        return repository.findById(id).orElseThrow();
    }

    public Recipe create(Recipe recipe) {
        recipe.setId(null);
        recipe.setCreatedAt(now());
        setKcalAndMacro(recipe);

        return repository.save(recipe);
    }

    public List<Recipe> create(List<Recipe> recipes) {
        recipes.forEach(recipe -> {
            recipe.setId(null);
            recipe.setCreatedAt(now());
            setKcalAndMacro(recipe);
        });

        return (List<Recipe>) repository.saveAll(recipes);
    }

    private void setKcalAndMacro(Recipe recipe) {
        int kcal = 0;
        BigDecimal proteins = BigDecimal.ZERO;
        BigDecimal carbohydrates = BigDecimal.ZERO;
        BigDecimal fats = BigDecimal.ZERO;

        Map<String, BigDecimal> productsNamesToQuantities = recipe.getProductsNamesToQuantities();
        List<Product> products = productService.getByNames(List.copyOf(productsNamesToQuantities.keySet()));
        validateIfAllRecipeProductsExists(recipe, productsNamesToQuantities, products);

        for (Product product : products) {
            BigDecimal quantity = productsNamesToQuantities.get(product.getName()).divide(new BigDecimal("100"), DECIMAL32);
            kcal += new BigDecimal(product.getKcal()).multiply(quantity).intValue();
            proteins = proteins.add(product.getProteins().multiply(quantity));
            carbohydrates = carbohydrates.add(product.getCarbohydrates().multiply(quantity));
            fats = fats.add(product.getFats().multiply(quantity));
        }

        recipe.setKcal(kcal);
        recipe.setProteins(proteins);
        recipe.setCarbohydrates(carbohydrates);
        recipe.setFats(fats);
    }

    private void validateIfAllRecipeProductsExists(Recipe recipe, Map<String, BigDecimal> productsNamesToQuantities, List<Product> products) {
        if (products.size() != productsNamesToQuantities.size()) {
            List<String> productsNames = products.stream()
                    .map(Product::getName)
                    .collect(toList());
            List<String> notExistingProductsNames = productsNamesToQuantities.keySet()
                    .stream()
                    .filter(productName -> !productsNames.contains(productName))
                    .collect(toList());

            throw new NotFoundException("Not found products: %s, for recipe: %s", notExistingProductsNames, recipe.getName());
        }
    }

    public Recipe update(Recipe recipe) {
        Recipe existingRecipe = getById(recipe.getId());
        if (!isEmpty(recipe.getMealNumbers())) {
            existingRecipe.setMealNumbers(recipe.getMealNumbers());
        }
        if (isNotBlank(recipe.getName())) {
            existingRecipe.setName(recipe.getName());
        }
        if (nonNull(recipe.getKcal())) {
            existingRecipe.setKcal(recipe.getKcal());
        }
        if (nonNull(recipe.getProteins())) {
            existingRecipe.setProteins(recipe.getProteins());
        }
        if (nonNull(recipe.getCarbohydrates())) {
            existingRecipe.setCarbohydrates(recipe.getCarbohydrates());
        }
        if (nonNull(recipe.getFats())) {
            existingRecipe.setFats(recipe.getFats());
        }
        if (!isEmpty(recipe.getProductsNamesToQuantities())) {
            existingRecipe.setProductsNamesToQuantities(recipe.getProductsNamesToQuantities());
        }
        if (isNotBlank(recipe.getRecipe())) {
            existingRecipe.setRecipe(recipe.getRecipe());
        }
        existingRecipe.setUpdatedAt(now());
        existingRecipe.setSource(USER);

        return repository.save(existingRecipe);
    }

    public List<Recipe> getByNames(List<String> names) {
        return repository.findByNameIn(names);
    }
}