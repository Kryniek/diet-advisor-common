package pl.dietadvisor.common.api.controller.recipes;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dietadvisor.common.recipe.model.dynamodb.Recipe;
import pl.dietadvisor.common.recipe.service.RecipeService;

import javax.validation.constraints.NotBlank;
import java.util.List;

import static java.util.Objects.requireNonNull;

@RestController
@RequiredArgsConstructor
@RequestMapping("recipes")
public class RecipeController {
    private final RecipeService service;

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
}
