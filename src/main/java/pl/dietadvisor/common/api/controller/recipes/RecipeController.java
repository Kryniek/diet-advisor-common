package pl.dietadvisor.common.api.controller.recipes;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dietadvisor.common.recipe.model.CreateRecipeResponse;
import pl.dietadvisor.common.recipe.model.dynamodb.Recipe;
import pl.dietadvisor.common.recipe.service.RecipeParserService;
import pl.dietadvisor.common.recipe.service.RecipeService;
import pl.dietadvisor.common.shared.exception.custom.BadRequestException;

import javax.validation.constraints.NotBlank;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.startsWithIgnoreCase;

@RestController
@RequiredArgsConstructor
@RequestMapping("recipes")
public class RecipeController {
    private final RecipeService service;
    private final RecipeParserService recipeParserService;

    @GetMapping
    public ResponseEntity<List<Recipe>> get() {
        return ResponseEntity.ok(service.get());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getById(@PathVariable @NotBlank String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping
    public ResponseEntity<Recipe> update(@RequestBody @NonNull Recipe recipe) {
        requireNonNull(recipe.getId(), "Id must be set.");

        return ResponseEntity.ok(service.update(recipe));
    }

    @PostMapping
    public ResponseEntity<Recipe> create(@RequestBody @NonNull Recipe recipe) {
        if (isEmpty(recipe.getMealNumbers())) {
            throw new BadRequestException("Meal numbers must be set.");
        }
        if (isBlank(recipe.getName())) {
            throw new BadRequestException("Name must be set.");
        }
        if (isEmpty(recipe.getProductsNamesToQuantities())) {
            throw new BadRequestException("Product names to quantities must be set.");
        }

        return new ResponseEntity<>(service.create(recipe), CREATED);
    }

    @PostMapping("/parse")
    public ResponseEntity<List<CreateRecipeResponse>> parse(@RequestBody @NonNull String rawRecipes) {
        if (isBlank(rawRecipes)) {
            throw new BadRequestException("Recipes in body must be set.");
        }
        if (!startsWithIgnoreCase(rawRecipes.trim(), "Dzień")) {
            throw new BadRequestException("Recipes don't start with 'Dzień'.");
        }

        return ResponseEntity.ok(recipeParserService.parse(rawRecipes.trim()));
    }
}
