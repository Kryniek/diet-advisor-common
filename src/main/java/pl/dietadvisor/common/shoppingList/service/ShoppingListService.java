package pl.dietadvisor.common.shoppingList.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dietadvisor.common.product.model.dynamodb.Product;
import pl.dietadvisor.common.product.service.ProductService;
import pl.dietadvisor.common.recipe.model.dynamodb.Recipe;
import pl.dietadvisor.common.recipe.service.RecipeService;
import pl.dietadvisor.common.shared.exception.custom.NotFoundException;
import pl.dietadvisor.common.shoppingList.enums.ProductType;
import pl.dietadvisor.common.shoppingList.model.ShoppingListGeneratorRequest;
import pl.dietadvisor.common.shoppingList.model.dynamodb.ShoppingList;
import pl.dietadvisor.common.shoppingList.repository.dynamodb.ShoppingListRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;
import static pl.dietadvisor.common.shoppingList.enums.ProductType.OTHER;
import static pl.dietadvisor.common.shoppingList.enums.ShoppingListSource.GENERATED;
import static pl.dietadvisor.common.shoppingList.enums.ShoppingListSource.USER;

@Service
@RequiredArgsConstructor
public class ShoppingListService {
    private final RecipeService recipeService;
    private final ProductService productService;
    private final ShoppingListRepository repository;

    public List<ShoppingList> get() {
        return (List<ShoppingList>) repository.findAll();
    }

    public ShoppingList getById(String id) {
        return repository.findById(id).orElseThrow();
    }

    public ShoppingList create(ShoppingList shoppingList) {
        validateIfProductsExists(shoppingList.getProductIdsToQuantities());

        return repository.save(ShoppingList.builder()
                .source(USER)
                .productIdsToQuantities(shoppingList.getProductIdsToQuantities())
                .createdAt(now())
                .build());
    }

    private void validateIfProductsExists(Map<String, BigDecimal> productIdsToQuantities) {
        List<String> productsIds = productIdsToQuantities.keySet()
                .stream()
                .distinct()
                .collect(toList());
        getProductsByIds(productsIds);
    }

    public ShoppingList generate(ShoppingListGeneratorRequest request) {
        List<Recipe> recipes = new ArrayList<>();
        if (!isEmpty(request.getRecipesIdsToQuantities())) {
            List<Recipe> recipesByIds = getRecipesByIds(request);
            recipesByIds.stream()
                    .filter(recipeById -> recipes.stream()
                            .noneMatch(recipe -> recipe.getId().equals(recipeById.getId())))
                    .forEach(recipes::add);
        }
        if (!isEmpty(request.getRecipesNamesToQuantities())) {
            List<Recipe> recipesByNames = getRecipesByNames(request);
            recipesByNames.stream()
                    .filter(recipeByName -> recipes.stream()
                            .noneMatch(recipe -> recipe.getId().equals(recipeByName.getId())))
                    .forEach(recipes::add);
        }

        return repository.save(ShoppingList.builder()
                .source(GENERATED)
                .productIdsToQuantities(getProductIdsToQuantities(request, recipes))
                .createdAt(now())
                .build());
    }

    private List<Recipe> getRecipesByIds(ShoppingListGeneratorRequest request) {
        List<String> recipesIds = request.getRecipesIdsToQuantities()
                .keySet()
                .stream()
                .distinct()
                .collect(toList());
        List<Recipe> recipes = recipeService.getByIds(recipesIds);
        if (isEmpty(recipes)) {
            throw new NotFoundException("Recipes not found for ids: %s", recipesIds);
        }
        if (recipesIds.size() != recipes.size()) {
            throw new NotFoundException("There is one or more not existing recipe which was trying to find by id. Check if recipes ids are correct.");
        }

        return recipes;
    }

    private List<Recipe> getRecipesByNames(ShoppingListGeneratorRequest request) {
        List<String> recipesNames = request.getRecipesNamesToQuantities()
                .keySet()
                .stream()
                .distinct()
                .collect(toList());
        List<Recipe> recipes = recipeService.getByNames(recipesNames);
        if (isEmpty(recipes)) {
            throw new NotFoundException("Recipes not found for names: %s", recipesNames);
        }
        if (recipesNames.size() != recipes.size()) {
            throw new NotFoundException("There is one or more not existing recipe which was trying to find by name. Check if recipes names are correct.");
        }

        return recipes;
    }

    private Map<String, BigDecimal> getProductIdsToQuantities(ShoppingListGeneratorRequest request, List<Recipe> recipes) {
        Map<String, BigDecimal> productIdsToQuantities = new HashMap<>();

        if (!isEmpty(request.getRecipesIdsToQuantities())) {
            request.getRecipesIdsToQuantities().forEach((recipeId, recipeQuantity) -> {
                Recipe recipe = recipes.stream()
                        .filter(r -> r.getId().equals(recipeId))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("Not found recipe with id: %s.", recipeId));
                List<Product> products = productService.getByNames(recipe.getProductsNamesToQuantities()
                        .keySet()
                        .stream()
                        .distinct()
                        .collect(toList()));

                recipe.getProductsNamesToQuantities().forEach((productName, productQuantity) -> {
                    Product product = products.stream()
                            .filter(p -> p.getName().equals(productName))
                            .findFirst()
                            .orElseThrow(() -> new NotFoundException("Not found product with name: %s.", productName));
                    BigDecimal productQuantityMultipliedByRecipeQuantity = productQuantity.multiply(new BigDecimal(recipeQuantity));

                    productIdsToQuantities.computeIfPresent(product.getId(), (productId, value) -> value.add(productQuantityMultipliedByRecipeQuantity));
                    productIdsToQuantities.putIfAbsent(product.getId(), productQuantityMultipliedByRecipeQuantity);
                });
            });
        }

        if (!isEmpty(request.getRecipesNamesToQuantities())) {
            request.getRecipesNamesToQuantities().forEach((recipeName, recipeQuantity) -> {
                Recipe recipe = recipes.stream()
                        .filter(r -> r.getName().equals(recipeName))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("Not found recipe with name: %s.", recipeName));
                List<Product> products = productService.getByNames(recipe.getProductsNamesToQuantities()
                        .keySet()
                        .stream()
                        .distinct()
                        .collect(toList()));

                recipe.getProductsNamesToQuantities().forEach((productName, productQuantity) -> {
                    Product product = products.stream()
                            .filter(p -> p.getName().equals(productName))
                            .findFirst()
                            .orElseThrow(() -> new NotFoundException("Not found product with name: %s.", productName));
                    BigDecimal productQuantityMultipliedByRecipeQuantity = productQuantity.multiply(new BigDecimal(recipeQuantity));

                    productIdsToQuantities.computeIfPresent(product.getId(), (productId, value) -> value.add(productQuantityMultipliedByRecipeQuantity));
                    productIdsToQuantities.putIfAbsent(product.getId(), productQuantityMultipliedByRecipeQuantity);
                });
            });
        }

        if (!isEmpty(request.getProductsIdsToQuantities())) {
            List<Product> productsByIds = productService.getByIds(
                    request.getProductsIdsToQuantities()
                            .keySet()
                            .stream()
                            .distinct()
                            .collect(toList()));

            request.getProductsIdsToQuantities().forEach((productId, productQuantity) -> {
                Product product = productsByIds.stream()
                        .filter(p -> p.getId().equals(productId))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("Not found product with id: %s.", productId));

                productIdsToQuantities.computeIfPresent(product.getId(), (key, value) -> value.add(productQuantity));
                productIdsToQuantities.putIfAbsent(product.getId(), productQuantity);
            });
        }

        if (!isEmpty(request.getProductsNamesToQuantities())) {
            List<Product> productsByNames = productService.getByNames(
                    request.getProductsNamesToQuantities()
                            .keySet()
                            .stream()
                            .distinct()
                            .collect(toList()));

            request.getProductsNamesToQuantities().forEach((productName, productQuantity) -> {
                Product product = productsByNames.stream()
                        .filter(p -> p.getName().equals(productName))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("Not found product with name: %s.", productName));

                productIdsToQuantities.computeIfPresent(product.getId(), (key, value) -> value.add(productQuantity));
                productIdsToQuantities.putIfAbsent(product.getId(), productQuantity);
            });
        }

        return productIdsToQuantities;
    }

    public String getByIdAsPlainText(String id) {
        ShoppingList shoppingList = repository.findById(id).orElseThrow();
        List<String> productsIds = shoppingList.getProductIdsToQuantities()
                .keySet()
                .stream()
                .distinct()
                .collect(toList());
        List<Product> products = getProductsByIds(productsIds);

        Map<String, BigDecimal> productNamesToQuantities = new HashMap<>();
        shoppingList.getProductIdsToQuantities().forEach((productId, productQuantity) -> {
            String productName = products.stream()
                    .filter(p -> p.getId().equals(productId))
                    .map(Product::getName)
                    .findFirst()
                    .orElseThrow();
            productNamesToQuantities.put(productName, productQuantity);
        });

        return buildPlainShoppingList(productNamesToQuantities);
    }

    private List<Product> getProductsByIds(List<String> productsIds) {
        List<Product> products = productService.getByIds(productsIds);
        if (isEmpty(products)) {
            throw new NotFoundException("Products not found for ids: %s", productsIds);
        }
        productsIds.forEach(productId -> {
            boolean isProductExist = products.stream()
                    .anyMatch(product -> product.getId().equals(productId));
            if (!isProductExist) {
                throw new NotFoundException("Product not found for id: %s", productId);
            }
        });

        return products;
    }

    private String buildPlainShoppingList(Map<String, BigDecimal> productNamesToQuantities) {
        Map<ProductType, Map<String, BigDecimal>> productTypesToProductNamesToQuantities = new HashMap<>();
        productNamesToQuantities.forEach((productName, productQuantity) -> {
            ProductType productType = ProductType.parse(productName);
            productTypesToProductNamesToQuantities.putIfAbsent(productType, new HashMap<>());
            productTypesToProductNamesToQuantities.get(productType).put(productName, productQuantity);
        });

        StringBuilder builder = new StringBuilder(format("Lista zakup??w: %tF \n\n", now()));
        productTypesToProductNamesToQuantities.entrySet()
                .stream()
                .sorted(comparingByKey())
                .forEach(productTypeToProductNamesToQuantities -> {
                    ProductType productType = productTypeToProductNamesToQuantities.getKey();
                    if (productType.equals(OTHER)) {
                        return;
                    }
                    appendTypeProducts(builder, productType, productTypeToProductNamesToQuantities.getValue());
                });

        Map<String, BigDecimal> otherProductNamesToQuantities = productTypesToProductNamesToQuantities.get(OTHER);
        if (!isEmpty(otherProductNamesToQuantities)) {
            appendTypeProducts(builder, OTHER, otherProductNamesToQuantities);
        }
        return builder.toString();
    }

    private void appendTypeProducts(StringBuilder builder, ProductType productType, Map<String, BigDecimal> productNamesToQuantities) {
        builder.append(format("%s:\n", productType.getDescription()));
        productNamesToQuantities.forEach((productName, productQuantity) -> builder.append(format(" - %s - %s %s\n", productName, productQuantity, "g")));
    }
}
