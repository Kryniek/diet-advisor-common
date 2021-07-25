package pl.dietadvisor.common.api.controller.shoppingList;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dietadvisor.common.shared.exception.custom.BadRequestException;
import pl.dietadvisor.common.shoppingList.model.ShoppingListGeneratorRequest;
import pl.dietadvisor.common.shoppingList.model.dynamodb.ShoppingList;
import pl.dietadvisor.common.shoppingList.service.ShoppingListService;

import javax.validation.constraints.NotBlank;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.util.CollectionUtils.isEmpty;

@RestController
@RequiredArgsConstructor
@RequestMapping("shopping-lists")
public class ShoppingListController {
    private final ShoppingListService service;

    @GetMapping
    public ResponseEntity<List<ShoppingList>> get() {
        return ResponseEntity.ok(service.get());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShoppingList> getById(@PathVariable @NotBlank String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<ShoppingList> create(@RequestBody @NonNull ShoppingList shoppingList) {
        if (isEmpty(shoppingList.getProductIdsToQuantities())) {
            throw new BadRequestException("Products to quantities must be set.");
        }

        return new ResponseEntity<>(service.create(shoppingList), CREATED);
    }

    @PostMapping("/generate")
    public ResponseEntity<ShoppingList> create(@RequestBody @NonNull ShoppingListGeneratorRequest request) {
        if (isEmpty(request.getRecipesIdsToQuantities())) {
            throw new BadRequestException("Recipes ids to quantities must be set.");
        }

        return new ResponseEntity<>(service.generate(request), CREATED);
    }

    @GetMapping("{id}/plain")
    public ResponseEntity<String> getByIdAsPlainText(@PathVariable @NotBlank String id) {
        return ResponseEntity.ok(service.getByIdAsPlainText(id));
    }
}
